import Fastify from "fastify";
import jwt from "@fastify/jwt";
import bcrypt from "bcryptjs";
import { createHash, randomBytes } from "node:crypto";
import { pool } from "./db.js";

type JwtUser={sub:string;role:"master"|"customer";deviceId?:string};
const hash=(v:string)=>createHash("sha256").update(v).digest("hex");
export function buildApp(){
 const app=Fastify({logger:true,bodyLimit:2*1024*1024});
 app.register(jwt,{secret:process.env.JWT_SECRET??"development-only-change-me"});
 const auth=async(req:any,reply:any)=>{try{await req.jwtVerify()}catch{return reply.code(401).send({error:"unauthorized"})}};
 const master=async(req:any,reply:any)=>{await auth(req,reply);if(reply.sent)return;const u=req.user as JwtUser;if(u.role!=="master")return reply.code(403).send({error:"master_required"})};
 app.get("/health",async()=>({status:"ok",service:"superiptv-api",version:"0.3.0"}));
 app.post("/v1/auth/login",async(req:any,reply)=>{const {email,password,deviceKey,deviceName,platform}=req.body??{};if(!email||!password||!deviceKey)return reply.code(400).send({error:"missing_credentials"});
  const r=await pool.query("select * from app_users where lower(email)=lower($1) limit 1",[email]);const user=r.rows[0];if(!user||!(await bcrypt.compare(password,user.password_hash)))return reply.code(401).send({error:"invalid_credentials"});if(user.status!=="active"||(user.expires_at&&new Date(user.expires_at)<=new Date()))return reply.code(403).send({error:"access_inactive"});
  const count=await pool.query("select count(*)::int n from devices where user_id=$1 and active=true",[user.id]);let d=await pool.query("select * from devices where user_id=$1 and device_key=$2",[user.id,deviceKey]);
  if(!d.rows[0]&&count.rows[0].n>=user.device_limit)return reply.code(403).send({error:"device_limit"});
  if(!d.rows[0])d=await pool.query("insert into devices(user_id,device_key,name,platform) values($1,$2,$3,$4) returning *",[user.id,deviceKey,deviceName??"Dispositivo",platform??"unknown"]);
  else if(!d.rows[0].active)return reply.code(403).send({error:"device_blocked"});
  const device=d.rows[0];await pool.query("update devices set last_seen_at=now() where id=$1",[device.id]);
  const access=app.jwt.sign({sub:user.id,role:user.role,deviceId:device.id},{expiresIn:"15m"});const refresh=randomBytes(48).toString("base64url");
  await pool.query("insert into refresh_sessions(user_id,device_id,token_hash,expires_at) values($1,$2,$3,now()+interval '30 days')",[user.id,device.id,hash(refresh)]);
  return {accessToken:access,refreshToken:refresh,user:{id:user.id,name:user.name,role:user.role},device:{id:device.id,name:device.name}};
 });
 app.get("/v1/catalog",{preHandler:auth},async(req:any,reply)=>{const u=req.user as JwtUser;if(u.role!=="customer")return reply.code(403).send({error:"customer_required"});const check=await pool.query("select status,expires_at from app_users where id=$1",[u.sub]);const x=check.rows[0];if(!x||x.status!=="active"||(x.expires_at&&new Date(x.expires_at)<=new Date()))return reply.code(403).send({error:"access_inactive"});const r=await pool.query("select id,name,group_name as \"group\",logo_url as logo,'/v1/play/'||id as \"playbackPath\" from catalog_items where active=true order by group_name,name");return {items:r.rows}});
 app.get("/v1/admin/overview",{preHandler:master},async()=>{const [u,d,c]=await Promise.all([pool.query("select count(*)::int n from app_users where role='customer'"),pool.query("select count(*)::int n from devices where active=true"),pool.query("select count(*)::int n from catalog_items where active=true")]);return {customers:u.rows[0].n,devices:d.rows[0].n,catalogItems:c.rows[0].n}});
 app.post("/v1/admin/customers",{preHandler:master},async(req:any,reply)=>{const {name,email,password,expiresAt,deviceLimit}=req.body??{};if(!name||!email||!password)return reply.code(400).send({error:"required_fields"});const passwordHash=await bcrypt.hash(password,12);const r=await pool.query("insert into app_users(role,name,email,password_hash,expires_at,device_limit) values('customer',$1,$2,$3,$4,$5) returning id,name,email,status,expires_at,device_limit",[name,email,passwordHash,expiresAt??null,deviceLimit??2]);return reply.code(201).send(r.rows[0])});
 app.patch("/v1/admin/devices/:id/revoke",{preHandler:master},async(req:any,reply)=>{const r=await pool.query("update devices set active=false where id=$1 returning id",[req.params.id]);if(!r.rowCount)return reply.code(404).send({error:"not_found"});await pool.query("update refresh_sessions set revoked_at=now() where device_id=$1 and revoked_at is null",[req.params.id]);return {ok:true}});
 return app;
}
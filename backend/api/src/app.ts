import Fastify from "fastify";
import { createHash, randomUUID } from "node:crypto";

type AccessStatus = "active" | "blocked" | "expired";
type Customer = { id:string; name:string; email:string; status:AccessStatus; expiresAt:string|null; deviceLimit:number };
type Device = { id:string; customerId:string; name:string; platform:string; active:boolean; lastSeenAt:string };
type CatalogItem = { id:string; name:string; group:string; logo:string|null; playbackPath:string };

const customers = new Map<string,Customer>();
const devices = new Map<string,Device>();
let sourceConfigured = false;
let catalog:CatalogItem[] = [];

function requireMaster(request:any, reply:any) {
  const expected=process.env.MASTER_ADMIN_TOKEN;
  if(!expected || request.headers.authorization!==`Bearer ${expected}`) return reply.code(401).send({error:"master_unauthorized"});
}
function parseM3u(text:string){
 const rows=text.split(/\r?\n/);const out:CatalogItem[]=[];let info="";
 for(const raw of rows){const line=raw.trim();if(line.startsWith("#EXTINF",0)){info=line;continue}if(!line||line.startsWith("#")||!info)continue;
  const name=info.substring(info.lastIndexOf(",")+1).trim();const group=/group-title="([^"]*)"/i.exec(info)?.[1]||"Sem categoria";const logo=/tvg-logo="([^"]*)"/i.exec(info)?.[1]||null;
  if(name){const id=createHash("sha256").update(line).digest("hex").slice(0,24);out.push({id,name,group,logo,playbackPath:`/v1/play/${id}`})}info="";
 }return out;
}
export function buildApp(){
 const app=Fastify({logger:true,bodyLimit:20*1024*1024});
 app.get("/health",async()=>({status:"ok",service:"superiptv-api",version:"0.2.0"}));
 app.get("/v1/admin/overview",{preHandler:requireMaster},async()=>({sourceConfigured,catalogItems:catalog.length,customers:customers.size,devices:devices.size}));
 app.post("/v1/admin/source",{preHandler:requireMaster},async(req:any,reply)=>{const body=req.body as {m3u?:string};if(!body?.m3u)return reply.code(400).send({error:"m3u_required"});const parsed=parseM3u(body.m3u);if(!parsed.length)return reply.code(400).send({error:"empty_catalog"});catalog=parsed;sourceConfigured=true;return {ok:true,items:catalog.length}});
 app.post("/v1/admin/customers",{preHandler:requireMaster},async(req:any,reply)=>{const b=req.body as Partial<Customer>;if(!b.name||!b.email)return reply.code(400).send({error:"name_email_required"});const item:Customer={id:randomUUID(),name:b.name,email:b.email,status:"active",expiresAt:b.expiresAt??null,deviceLimit:b.deviceLimit??2};customers.set(item.id,item);return reply.code(201).send(item)});
 app.get("/v1/admin/customers",{preHandler:requireMaster},async()=>Array.from(customers.values()));
 app.patch("/v1/admin/customers/:id/status",{preHandler:requireMaster},async(req:any,reply)=>{const c=customers.get(req.params.id);if(!c)return reply.code(404).send({error:"not_found"});const status=req.body?.status as AccessStatus;if(!["active","blocked","expired"].includes(status))return reply.code(400).send({error:"invalid_status"});c.status=status;return c});
 app.get("/v1/catalog",async(req:any,reply)=>{const customerId=String(req.headers["x-customer-id"]??"");const c=customers.get(customerId);if(!c||c.status!=="active")return reply.code(403).send({error:"access_denied"});return {items:catalog}});
 return app;
}
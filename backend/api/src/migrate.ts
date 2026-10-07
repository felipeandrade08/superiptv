import { readdir, readFile } from "node:fs/promises";
import { fileURLToPath } from "node:url";
import { dirname, resolve } from "node:path";
import { createHash } from "node:crypto";
import { pool } from "./db.js";

const here=dirname(fileURLToPath(import.meta.url));
const sqlDir=resolve(here,"../sql");
const checksum=(value:string)=>createHash("sha256").update(value).digest("hex");

async function migrate(){
 await pool.query(`create table if not exists schema_migrations(
  name text primary key,
  checksum text not null,
  applied_at timestamptz not null default now()
 )`);
 const files=(await readdir(sqlDir)).filter(x=>/^\d+.*\.sql$/.test(x)).sort();
 for(const name of files){
  const sql=await readFile(resolve(sqlDir,name),"utf8");
  const sum=checksum(sql);
  const existing=await pool.query("select checksum from schema_migrations where name=$1",[name]);
  if(existing.rowCount){
   if(existing.rows[0].checksum!==sum)throw new Error(`Migration ${name} changed after being applied`);
   console.log(`skip ${name}`);
   continue;
  }
  const client=await pool.connect();
  try{
   await client.query("begin");
   await client.query(sql);
   await client.query("insert into schema_migrations(name,checksum) values($1,$2)",[name,sum]);
   await client.query("commit");
   console.log(`applied ${name}`);
  }catch(error){
   await client.query("rollback");
   throw error;
  }finally{client.release()}
 }
}
migrate().then(()=>pool.end()).catch(async error=>{console.error(error);await pool.end();process.exitCode=1});

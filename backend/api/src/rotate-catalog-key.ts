import { pool } from "./db.js";
import { decryptSecretWithKey, encryptSecret } from "./secrets.js";

const oldKey=process.env.CATALOG_OLD_SECRET_KEY;
if(!oldKey)throw new Error("CATALOG_OLD_SECRET_KEY is required");
if(!process.env.CATALOG_SECRET_KEY)throw new Error("CATALOG_SECRET_KEY is required");
if(oldKey===process.env.CATALOG_SECRET_KEY)throw new Error("old and new catalog keys must differ");

async function run(){
 const sources=await pool.query("select id,source_url_encrypted value from catalog_sources where source_url_encrypted is not null");
 for(const row of sources.rows)await pool.query("update catalog_sources set source_url_encrypted=$1 where id=$2",[encryptSecret(decryptSecretWithKey(row.value,oldKey)),row.id]);
 let rotated=0;
 while(true){
  const rows=await pool.query("select id,stream_url_encrypted value from catalog_items where stream_url_encrypted is not null and coalesce(updated_at,now())<=now() order by id limit 500 offset $1",[rotated]);
  if(!rows.rowCount)break;
  const client=await pool.connect();
  try{await client.query("begin");for(const row of rows.rows)await client.query("update catalog_items set stream_url_encrypted=$1 where id=$2",[encryptSecret(decryptSecretWithKey(row.value,oldKey)),row.id]);await client.query("commit");rotated+=rows.rowCount}catch(error){await client.query("rollback");throw error}finally{client.release()}
 }
 console.log(`catalog key rotated: sources=${sources.rowCount??0}, items=${rotated}`);
}
run().then(()=>pool.end()).catch(async error=>{console.error(error instanceof Error?error.message:"rotation_failed");await pool.end();process.exitCode=1});

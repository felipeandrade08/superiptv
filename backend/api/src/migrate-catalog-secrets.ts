import { pool } from "./db.js";
import { encryptSecret } from "./secrets.js";

async function run(){
 const sources=await pool.query("select id,source_url from catalog_sources where source_url_encrypted is null and source_url is not null");
 for(const row of sources.rows)await pool.query("update catalog_sources set source_url_encrypted=$1,source_url=null where id=$2",[encryptSecret(row.source_url),row.id]);
 let migrated=0;
 while(true){
  const rows=await pool.query("select id,stream_url from catalog_items where stream_url_encrypted is null and stream_url is not null limit 500");
  if(!rows.rowCount)break;
  const client=await pool.connect();
  try{await client.query("begin");for(const row of rows.rows){await client.query("update catalog_items set stream_url_encrypted=$1,stream_url=null where id=$2",[encryptSecret(row.stream_url),row.id]);migrated++}await client.query("commit")}catch(error){await client.query("rollback");throw error}finally{client.release()}
 }
 console.log(`catalog secrets migrated: sources=${sources.rowCount??0}, items=${migrated}`);
}
run().then(()=>pool.end()).catch(async error=>{console.error(error);await pool.end();process.exitCode=1});

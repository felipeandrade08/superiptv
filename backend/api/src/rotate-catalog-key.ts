import { pool } from "./db.js";
import { decryptSecretWithKey, encryptSecret } from "./secrets.js";

const oldKeyValue=process.env.CATALOG_OLD_SECRET_KEY;
const newKeyValue=process.env.CATALOG_SECRET_KEY;
if(!oldKeyValue)throw new Error("CATALOG_OLD_SECRET_KEY is required");
if(!newKeyValue)throw new Error("CATALOG_SECRET_KEY is required");
if(oldKeyValue===newKeyValue)throw new Error("old and new catalog keys must differ");
const oldKey:string=oldKeyValue;

async function run(){
 const client=await pool.connect();let sources=0;let rotated=0;let lastId="";
 try{
  await client.query("begin");
  const sourceRows=await client.query("select id,source_url_encrypted value from catalog_sources where source_url_encrypted is not null for update");
  for(const row of sourceRows.rows){await client.query("update catalog_sources set source_url_encrypted=$1 where id=$2",[encryptSecret(decryptSecretWithKey(row.value,oldKey)),row.id]);sources++}
  while(true){
   const rows=await client.query("select id,stream_url_encrypted value from catalog_items where stream_url_encrypted is not null and id>$1 order by id limit 500 for update",[lastId]);
   if(!rows.rowCount)break;
   for(const row of rows.rows)await client.query("update catalog_items set stream_url_encrypted=$1 where id=$2",[encryptSecret(decryptSecretWithKey(row.value,oldKey)),row.id]);
   rotated+=rows.rowCount;lastId=rows.rows[rows.rows.length-1].id;
  }
  await client.query("commit");
  console.log(`catalog key rotated: sources=${sources}, items=${rotated}`);
 }catch(error){await client.query("rollback");throw error}finally{client.release()}
}
run().then(()=>pool.end()).catch(async error=>{console.error(error instanceof Error?error.message:"rotation_failed");await pool.end();process.exitCode=1});

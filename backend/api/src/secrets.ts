import { createCipheriv, createDecipheriv, createHash, randomBytes } from "node:crypto";

const derive=(raw:string)=>createHash("sha256").update(raw).digest();
const key=()=>{const raw=process.env.CATALOG_SECRET_KEY;if(!raw)throw new Error("CATALOG_SECRET_KEY is required");return derive(raw)};
export function encryptSecret(value:string){
 const iv=randomBytes(12);const cipher=createCipheriv("aes-256-gcm",key(),iv);
 const encrypted=Buffer.concat([cipher.update(value,"utf8"),cipher.final()]);
 return ["v1",iv.toString("base64url"),cipher.getAuthTag().toString("base64url"),encrypted.toString("base64url")].join(".");
}
export function decryptSecret(value:string){
 const [version,iv,tag,data]=value.split(".");if(version!=="v1"||!iv||!tag||!data)throw new Error("invalid_encrypted_secret");
 const decipher=createDecipheriv("aes-256-gcm",key(),Buffer.from(iv,"base64url"));decipher.setAuthTag(Buffer.from(tag,"base64url"));
 return Buffer.concat([decipher.update(Buffer.from(data,"base64url")),decipher.final()]).toString("utf8");
}

export function decryptSecretWithKey(value:string,rawKey:string){
 const [version,iv,tag,data]=value.split(".");if(version!=="v1"||!iv||!tag||!data)throw new Error("invalid_encrypted_secret");
 const decipher=createDecipheriv("aes-256-gcm",derive(rawKey),Buffer.from(iv,"base64url"));decipher.setAuthTag(Buffer.from(tag,"base64url"));
 return Buffer.concat([decipher.update(Buffer.from(data,"base64url")),decipher.final()]).toString("utf8");
}

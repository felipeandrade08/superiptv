package com.superiptv.desktop
import org.json.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import java.util.UUID
import java.util.Base64
import com.sun.jna.platform.win32.Crypt32Util

data class DesktopChannel(val id:String,val name:String,val group:String,val logo:String?,val playbackUrl:String)
class DesktopSession(private val root:Path=Path.of(System.getProperty("user.home"),".superiptv")){
 private val file=root.resolve("session.properties");private val props=Properties()
 init{Files.createDirectories(root);if(Files.exists(file))Files.newInputStream(file).use{props.load(it)}}
 private fun protect(v:String)=if(System.getProperty("os.name").startsWith("Windows",true))Base64.getEncoder().encodeToString(Crypt32Util.cryptProtectData(v.toByteArray(Charsets.UTF_8))) else v
 private fun unprotect(v:String?):String?=if(v==null)null else runCatching{if(System.getProperty("os.name").startsWith("Windows",true))String(Crypt32Util.cryptUnprotectData(Base64.getDecoder().decode(v)),Charsets.UTF_8) else v}.getOrNull()
 val access:String? get()=unprotect(props.getProperty("access"));val refresh:String? get()=unprotect(props.getProperty("refresh"))
 val deviceKey:String get(){val old=props.getProperty("device");if(old!=null)return old;val id=UUID.randomUUID().toString();props.setProperty("device",id);persist();return id}
 fun save(a:String,r:String){props.setProperty("access",protect(a));props.setProperty("refresh",protect(r));persist()}
 fun clear(){props.remove("access");props.remove("refresh");persist()}
 private fun persist(){Files.newOutputStream(file).use{props.store(it,"SuperIPTV Desktop")}}
 fun cache(items:List<DesktopChannel>){val json=org.json.JSONArray();items.forEach{json.put(JSONObject().put("id",it.id).put("name",it.name).put("group",it.group).put("logo",it.logo).put("playbackUrl",it.playbackUrl))};Files.writeString(root.resolve("catalog.json"),json.toString())}
 fun cached():List<DesktopChannel>{val p=root.resolve("catalog.json");if(!Files.exists(p))return emptyList();return runCatching{val a=org.json.JSONArray(Files.readString(p));(0 until a.length()).map{i->val x=a.getJSONObject(i);DesktopChannel(x.getString("id"),x.getString("name"),x.getString("group"),x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},x.getString("playbackUrl"))}}.getOrDefault(emptyList())}
}
class DesktopApi(private val base:String){
 private val http=HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
 fun login(email:String,password:String,key:String):JSONObject=call("/v1/auth/login","POST",JSONObject().put("email",email).put("password",password).put("deviceKey",key).put("deviceName",System.getProperty("os.name")+" PC").put("platform","desktop"),null)
 fun catalog(token:String):List<DesktopChannel>{val a=call("/v1/catalog","GET",null,token).getJSONArray("items");return (0 until a.length()).map{i->val x=a.getJSONObject(i);DesktopChannel(x.getString("id"),x.getString("name"),x.getString("group"),x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},"")}}
 fun playbackTicket(id:String,token:String):String=base.trimEnd('/')+call("/v1/play/$id/ticket","POST",JSONObject(),token).getString("url")
 fun refresh(token:String):JSONObject=call("/v1/auth/refresh","POST",JSONObject().put("refreshToken",token),null)
 fun logout(refresh:String){call("/v1/auth/logout","POST",JSONObject().put("refreshToken",refresh),null)}
 private fun call(path:String,method:String,json:JSONObject?,token:String?):JSONObject{val b=HttpRequest.newBuilder(URI.create(base.trimEnd('/')+path)).header("Content-Type","application/json");if(token!=null)b.header("Authorization","Bearer $token");val req=if(method=="POST")b.POST(HttpRequest.BodyPublishers.ofString(json.toString())).build() else b.GET().build();val r=http.send(req,HttpResponse.BodyHandlers.ofString());if(r.statusCode() !in 200..299)error(runCatching{JSONObject(r.body()).optString("error","request_failed")}.getOrDefault("request_failed"));return JSONObject(r.body())}
}
package com.superiptv.app.data
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
class SuperIptvApi(private val baseUrl:String){
 private val client=OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).build()
 fun login(email:String,password:String,deviceKey:String):JSONObject{val json=JSONObject().put("email",email).put("password",password).put("deviceKey",deviceKey).put("deviceName","Android").put("platform","android");return request("/v1/auth/login","POST",json,null)}
 fun catalog(token:String):JSONObject=request("/v1/catalog","GET",null,token)
 private fun request(path:String,method:String,json:JSONObject?,token:String?):JSONObject{val b=Request.Builder().url(baseUrl.trimEnd('/')+path);if(token!=null)b.header("Authorization","Bearer $token");if(method=="POST")b.post(json.toString().toRequestBody("application/json".toMediaType())) else b.get();client.newCall(b.build()).execute().use{r->val body=r.body?.string().orEmpty();if(!r.isSuccessful)error(JSONObject(body.ifBlank{"{}"}).optString("error","request_failed"));return JSONObject(body)}}
}

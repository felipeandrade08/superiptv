package com.superiptv.app.playlist
import java.io.BufferedReader
import java.io.Reader
data class M3uItem(val name:String,val url:String,val logo:String?,val group:String)
class M3uParser{
 fun parse(reader:Reader,onItem:(M3uItem)->Unit){
  val input=if(reader is BufferedReader) reader else reader.buffered();var metadata:String?=null
  input.useLines{lines->lines.forEach{raw->val line=raw.trim();when{
   line.startsWith("#EXTINF",true)->metadata=line
   line.isNotEmpty()&&!line.startsWith("#")&&metadata!=null->{parseItem(metadata!!,line)?.let(onItem);metadata=null}
  }}}
 }
 private fun parseItem(info:String,url:String):M3uItem?{
  val name=info.substringAfterLast(",",missingDelimiterValue="").trim();if(name.isBlank()||url.isBlank())return null
  fun attr(key:String)=Regex("""$key="([^"]*)"""",RegexOption.IGNORE_CASE).find(info)?.groupValues?.getOrNull(1)?.trim()?.takeIf{it.isNotEmpty()}
  return M3uItem(name,url,attr("tvg-logo"),attr("group-title")?:"Sem categoria")
 }
}
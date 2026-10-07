package com.superiptv.app.data
import android.content.Context
import android.net.Uri
import com.superiptv.app.data.local.*
import com.superiptv.app.playlist.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
class PlaylistRepository(private val context:Context){
 private val db=AppDatabase.get(context);private val parser=M3uParser()
 fun channels():Flow<List<ChannelEntity>> = db.channels().observeAll()
 fun favorites():Flow<List<ChannelEntity>> = db.channels().observeFavorites()
 fun movies():Flow<List<VodEntity>> = db.vod().observeMovies()
 fun series():Flow<List<VodEntity>> = db.vod().observeSeries()
 fun vodFavorites():Flow<List<VodEntity>> = db.vod().observeFavorites()
 suspend fun replaceManagedCatalog(items:List<ChannelEntity>,vod:List<VodEntity>){val channelFav=db.channels().favoriteIds().toSet();val vodFav=db.vod().favoriteIds().toSet();db.channels().deleteForPlaylist("managed");db.channels().saveAll(items.map{it.copy(favorite=it.id in channelFav)});db.vod().clear();db.vod().saveAll(vod.map{it.copy(favorite=it.id in vodFav)})}
 suspend fun toggleFavorite(channel:ChannelEntity)=db.channels().setFavorite(channel.id,!channel.favorite)
 suspend fun toggleFavorite(item:VodEntity)=db.vod().setFavorite(item.id,!item.favorite)
 suspend fun importUrl(name:String,url:String)=withContext(Dispatchers.IO){
  require(url.startsWith("http://")||url.startsWith("https://")){"Use uma URL HTTP ou HTTPS válida."}
  val conn=(URL(url).openConnection() as HttpURLConnection).apply{connectTimeout=15000;readTimeout=30000;instanceFollowRedirects=true;setRequestProperty("User-Agent","SuperIPTV/0.3")}
  try{conn.connect();require(conn.responseCode in 200..299){"Servidor respondeu HTTP "+conn.responseCode};conn.inputStream.bufferedReader().use{importReader(name,url,"url",it)}}finally{conn.disconnect()}
 }
 suspend fun importFile(name:String,uri:Uri)=withContext(Dispatchers.IO){val stream=context.contentResolver.openInputStream(uri)?:error("Não foi possível abrir o arquivo.");stream.bufferedReader().use{importReader(name,uri.toString(),"file",it)}}
 private suspend fun importReader(name:String,source:String,type:String,reader:java.io.Reader){
  val playlistId=UUID.nameUUIDFromBytes(source.toByteArray()).toString();val now=System.currentTimeMillis();val items=mutableListOf<ChannelEntity>()
  parser.parse(reader){i->items+=ChannelEntity(UUID.nameUUIDFromBytes((playlistId+"|"+i.url).toByteArray()).toString(),playlistId,i.name,i.url,i.logo,i.group)}
  require(items.isNotEmpty()){"Nenhum canal M3U válido foi encontrado."}
  db.channels().deleteForPlaylist(playlistId);db.channels().saveAll(items);db.playlists().save(PlaylistEntity(playlistId,name.ifBlank{"Minha playlist"},source,type,now,now))
 }
}
package com.superiptv.app.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.BuildConfig
import com.superiptv.app.data.*
import com.superiptv.app.data.local.ChannelEntity
import com.superiptv.app.data.local.VodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class PlaylistUiState(val loading:Boolean=false,val channels:List<ChannelEntity> = emptyList(),val movies:List<VodEntity> = emptyList(),val series:List<VodEntity> = emptyList(),val query:String="",val message:String?=null)
class PlaylistViewModel(app:Application):AndroidViewModel(app){
 private val repo=PlaylistRepository(app);private val sessions=SessionRepository(app);private val api=SuperIptvApi(BuildConfig.API_BASE_URL);private val query=MutableStateFlow("");private val message=MutableStateFlow<String?>(null);private val loading=MutableStateFlow(false)
 val state=combine(repo.channels(),repo.movies(),repo.series(),query,loading,message){channels,movies,series,q,l,m->
  val filteredChannels=if(q.isBlank())channels else channels.filter{it.name.contains(q,true)||it.groupName.contains(q,true)}
  val filteredMovies=if(q.isBlank())movies else movies.filter{it.name.contains(q,true)||it.groupName.contains(q,true)}
  val filteredSeries=if(q.isBlank())series else series.filter{it.name.contains(q,true)||it.groupName.contains(q,true)||it.seriesName?.contains(q,true)==true}
  PlaylistUiState(l,filteredChannels,filteredMovies,filteredSeries,q,m)
 }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),PlaylistUiState())
 init{refresh()}
 fun setQuery(v:String){query.value=v};fun clearMessage(){message.value=null};fun favorite(channel:ChannelEntity)=viewModelScope.launch{repo.toggleFavorite(channel)};fun favorite(item:VodEntity)=viewModelScope.launch{repo.toggleFavorite(item)}
 fun refresh()=viewModelScope.launch(Dispatchers.IO){val token=sessions.accessToken.firstOrNull();if(token.isNullOrBlank())return@launch;loading.value=true;try{var active=token;val data=try{api.catalog(active).getJSONArray("items")}catch(first:Exception){val refresh=sessions.refreshToken.firstOrNull()?:throw first;val renewed=api.refresh(refresh);active=renewed.getString("accessToken");sessions.save(active,renewed.getString("refreshToken"),sessions.userName.firstOrNull().orEmpty());api.catalog(active).getJSONArray("items")};val channels=mutableListOf<ChannelEntity>();val vod=mutableListOf<VodEntity>();for(i in 0 until data.length()){val x=data.getJSONObject(i);val type=x.optString("type","live");if(type=="live")channels+=ChannelEntity(x.getString("id"),"managed",x.getString("name"),"",x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},x.getString("group"),false) else vod+=VodEntity(x.getString("id"),x.getString("name"),type,x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},x.getString("group"),x.optString("description").takeIf{it.isNotBlank()&&it!="null"},x.optInt("year").takeIf{!x.isNull("year")},x.optInt("seasonNumber").takeIf{!x.isNull("seasonNumber")},x.optInt("episodeNumber").takeIf{!x.isNull("episodeNumber")},x.optString("seriesName").takeIf{it.isNotBlank()&&it!="null"},false)};repo.replaceManagedCatalog(channels,vod);message.value=null}catch(e:Exception){message.value="Usando catálogo salvo. "+(e.message?:"Falha de sincronização.")}finally{loading.value=false}}
}

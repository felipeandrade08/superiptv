package com.superiptv.app.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.BuildConfig
import com.superiptv.app.data.*
import com.superiptv.app.data.local.ChannelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class PlaylistUiState(val loading:Boolean=false,val channels:List<ChannelEntity> = emptyList(),val query:String="",val message:String?=null)
class PlaylistViewModel(app:Application):AndroidViewModel(app){
 private val repo=PlaylistRepository(app);private val sessions=SessionRepository(app);private val api=SuperIptvApi(BuildConfig.API_BASE_URL);private val query=MutableStateFlow("");private val message=MutableStateFlow<String?>(null);private val loading=MutableStateFlow(false)
 val state=combine(repo.channels(),query,loading,message){channels,q,l,m->PlaylistUiState(l,if(q.isBlank())channels else channels.filter{it.name.contains(q,true)||it.groupName.contains(q,true)},q,m)}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),PlaylistUiState())
 init{refresh()}
 fun setQuery(v:String){query.value=v};fun clearMessage(){message.value=null};fun favorite(channel:ChannelEntity)=viewModelScope.launch{repo.toggleFavorite(channel)}
 fun refresh()=viewModelScope.launch(Dispatchers.IO){val token=sessions.accessToken.firstOrNull();if(token.isNullOrBlank())return@launch;loading.value=true;try{val data=api.catalog(token).getJSONArray("items");val items=buildList{for(i in 0 until data.length()){val x=data.getJSONObject(i);add(ChannelEntity(x.getString("id"),"managed",x.getString("name"),BuildConfig.API_BASE_URL.trimEnd('/')+x.getString("playbackPath"),x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},x.getString("group"),false))}};repo.replaceManagedCatalog(items);message.value=null}catch(e:Exception){message.value="Usando catálogo salvo. "+(e.message?:"Falha de sincronização.")}finally{loading.value=false}}
}
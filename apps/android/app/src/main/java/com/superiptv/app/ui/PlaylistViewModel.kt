package com.superiptv.app.ui
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.data.PlaylistRepository
import com.superiptv.app.data.local.ChannelEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class PlaylistUiState(val loading:Boolean=false,val channels:List<ChannelEntity> = emptyList(),val query:String="",val message:String?=null)
class PlaylistViewModel(app:Application):AndroidViewModel(app){
 private val repo=PlaylistRepository(app);private val query=MutableStateFlow("");private val message=MutableStateFlow<String?>(null);private val loading=MutableStateFlow(false)
 val state=combine(repo.channels(),query,loading,message){channels,q,l,m->PlaylistUiState(l,if(q.isBlank())channels else channels.filter{it.name.contains(q,true)||it.groupName.contains(q,true)},q,m)}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),PlaylistUiState())
 fun setQuery(v:String){query.value=v};fun clearMessage(){message.value=null}
 fun importUrl(name:String,url:String)=viewModelScope.launch{runImport{repo.importUrl(name,url)}}
 fun importFile(name:String,uri:Uri)=viewModelScope.launch{runImport{repo.importFile(name,uri)}}
 fun favorite(channel:ChannelEntity)=viewModelScope.launch{repo.toggleFavorite(channel)}
 private suspend fun runImport(block:suspend()->Unit){loading.value=true;message.value=null;try{block();message.value="Playlist importada com sucesso."}catch(e:Exception){message.value=e.message?:"Não foi possível importar a playlist."}finally{loading.value=false}}
}
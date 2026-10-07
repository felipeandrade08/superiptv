package com.superiptv.tv
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.tv.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class TvAuthState(val checking:Boolean=true,val logged:Boolean=false,val loading:Boolean=false,val error:String?=null)
class TvViewModel(app:Application):AndroidViewModel(app){
 private val dao=TvDatabase.get(app).channels();private val session=TvSessionRepository(app);private val api=TvApi(BuildConfig.API_BASE_URL);private val authState=MutableStateFlow(TvAuthState());val auth=authState.asStateFlow()
 val accessToken=session.accessToken.stateIn(viewModelScope,SharingStarted.Eagerly,null);val channels=dao.observeAll().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());val live=channels.map{items->items.filter{it.contentType=="live"}}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());val movies=channels.map{items->items.filter{it.contentType=="movie"}}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());val series=channels.map{items->items.filter{it.contentType=="series"}}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());val groups=live.map{items->items.map{it.groupName}.distinct()}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 init{refreshSession()}
 fun login(email:String,password:String)=viewModelScope.launch(Dispatchers.IO){authState.value=authState.value.copy(loading=true,error=null);try{val d=api.login(email,password,session.deviceKey());session.save(d.getString("accessToken"),d.getString("refreshToken"));authState.value=TvAuthState(false,true)}catch(e:Exception){authState.value=TvAuthState(false,false,false,e.message)}}
 suspend fun playbackUrl(id:String):String=kotlinx.coroutines.withContext(Dispatchers.IO){api.playbackTicket(id,session.accessToken.firstOrNull()?:error("session_required"))}
 fun refreshSession()=viewModelScope.launch(Dispatchers.IO){val r=session.refreshToken.firstOrNull();if(r.isNullOrBlank()){authState.value=TvAuthState(false,false);return@launch};try{val d=api.refresh(r);session.save(d.getString("accessToken"),d.getString("refreshToken"));authState.value=TvAuthState(false,true);sync(d.getString("accessToken"))}catch(_:Exception){session.clear();authState.value=TvAuthState(false,false)}}
 private suspend fun sync(token:String){try{val favorites=dao.favoriteIds().toSet();val a=api.catalog(token).getJSONArray("items");val items=buildList{for(i in 0 until a.length()){val x=a.getJSONObject(i);val id=x.getString("id");add(TvChannel(id,x.getString("name"),"",x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},x.getString("group"),id in favorites,x.optString("type","live"),x.optString("description").takeIf{it.isNotBlank()&&it!="null"},x.optInt("year").takeIf{!x.isNull("year")},x.optInt("seasonNumber").takeIf{!x.isNull("seasonNumber")},x.optInt("episodeNumber").takeIf{!x.isNull("episodeNumber")},x.optString("seriesName").takeIf{it.isNotBlank()&&it!="null"}))}};dao.clear();dao.saveAll(items)}catch(_:Exception){}}
}
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
 val accessToken=session.accessToken.stateIn(viewModelScope,SharingStarted.Eagerly,null);val channels=dao.observeAll().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());val groups=channels.map{items->items.map{it.groupName}.distinct()}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 init{viewModelScope.launch{session.accessToken.collect{token->authState.value=authState.value.copy(checking=false,logged=!token.isNullOrBlank());if(!token.isNullOrBlank())sync(token)}}}
 fun login(email:String,password:String)=viewModelScope.launch(Dispatchers.IO){authState.value=authState.value.copy(loading=true,error=null);try{val d=api.login(email,password,session.deviceKey());session.save(d.getString("accessToken"),d.getString("refreshToken"));authState.value=TvAuthState(false,true)}catch(e:Exception){authState.value=TvAuthState(false,false,false,e.message)}}
 private suspend fun sync(token:String){try{val a=api.catalog(token).getJSONArray("items");val items=buildList{for(i in 0 until a.length()){val x=a.getJSONObject(i);add(TvChannel(x.getString("id"),x.getString("name"),BuildConfig.API_BASE_URL.trimEnd('/')+x.getString("playbackPath"),x.optString("logo").takeIf{it.isNotBlank()&&it!="null"},x.getString("group")))}};dao.clear();dao.saveAll(items)}catch(_:Exception){}}
}
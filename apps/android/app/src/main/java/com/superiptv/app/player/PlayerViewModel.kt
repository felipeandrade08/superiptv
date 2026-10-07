package com.superiptv.app.player
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.data.SessionRepository
import com.superiptv.app.data.SuperIptvApi
import com.superiptv.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import com.superiptv.app.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class PlayableItem(val id:String,val name:String,val logoUrl:String?,val groupName:String,val contentType:String)
class PlayerViewModel(app:Application):AndroidViewModel(app){
 private val db=AppDatabase.get(app);private val sessions=SessionRepository(app);private val api=SuperIptvApi(BuildConfig.API_BASE_URL)
 val channels=db.channels().observeAll().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val movies=db.vod().observeMovies().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val series=db.vod().observeSeries().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val recent=db.history().observeRecent().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val continueWatching=db.history().observeContinue().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val accessToken=sessions.accessToken.stateIn(viewModelScope,SharingStarted.Eagerly,null)
 suspend fun playbackUrl(id:String):String=kotlinx.coroutines.withContext(Dispatchers.IO){api.playbackTicket(id,sessions.accessToken.firstOrNull()?:error("session_required"))}
 fun find(id:String):PlayableItem?{channels.value.firstOrNull{it.id==id}?.let{return PlayableItem(it.id,it.name,it.logoUrl,it.groupName,"live")};(movies.value+series.value).firstOrNull{it.id==id}?.let{return PlayableItem(it.id,it.name,it.logoUrl,it.groupName,it.contentType)};return null}
 suspend fun resumePosition(id:String):Long=kotlinx.coroutines.withContext(Dispatchers.IO){db.history().byChannel(id)?.takeIf{it.contentType!="live"}?.positionMs?:0L}
 fun save(item:PlayableItem,position:Long,duration:Long)=viewModelScope.launch{db.history().save(WatchHistoryEntity(item.id,item.id,item.name,"",item.logoUrl,item.groupName,position.coerceAtLeast(0),duration.coerceAtLeast(0),System.currentTimeMillis(),item.contentType))}
}

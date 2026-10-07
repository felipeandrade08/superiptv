package com.superiptv.app.player
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.data.SessionRepository
import com.superiptv.app.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
class PlayerViewModel(app:Application):AndroidViewModel(app){
 private val db=AppDatabase.get(app);private val sessions=SessionRepository(app)
 val channels=db.channels().observeAll().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val recent=db.history().observeRecent().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val continueWatching=db.history().observeContinue().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())
 val accessToken=sessions.accessToken.stateIn(viewModelScope,SharingStarted.Eagerly,null)
 fun save(channel:ChannelEntity,position:Long,duration:Long)=viewModelScope.launch{db.history().save(WatchHistoryEntity(channel.id,channel.id,channel.name,channel.streamUrl,channel.logoUrl,channel.groupName,position.coerceAtLeast(0),duration.coerceAtLeast(0),System.currentTimeMillis()))}
}
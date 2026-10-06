package com.superiptv.tv
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.tv.data.*
import kotlinx.coroutines.flow.*
class TvViewModel(app:Application):AndroidViewModel(app){private val dao=TvDatabase.get(app).channels();val channels=dao.observeAll().stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList());val groups=channels.map{items->items.map{it.groupName}.distinct()}.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),emptyList())}

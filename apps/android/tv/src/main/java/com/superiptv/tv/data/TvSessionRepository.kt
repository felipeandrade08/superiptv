package com.superiptv.tv.data
import android.content.Context
import android.provider.Settings
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
private val Context.store by preferencesDataStore("tv_session")
class TvSessionRepository(private val context:Context){private val access=stringPreferencesKey("access");private val refresh=stringPreferencesKey("refresh");val accessToken=context.store.data.map{it[access]};val refreshToken=context.store.data.map{it[refresh]};suspend fun save(a:String,r:String){context.store.edit{it[access]=a;it[refresh]=r}};suspend fun clear(){context.store.edit{it.clear()}};fun deviceKey()=Settings.Secure.getString(context.contentResolver,Settings.Secure.ANDROID_ID)?:"android-tv"}
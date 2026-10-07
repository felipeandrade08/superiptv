package com.superiptv.app.data
import android.content.Context
import android.provider.Settings
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
private val Context.sessionStore by preferencesDataStore("session")
class SessionRepository(private val context:Context){
 private val access=stringPreferencesKey("access_token");private val refresh=stringPreferencesKey("refresh_token");private val name=stringPreferencesKey("user_name")
 val accessToken=context.sessionStore.data.map{it[access]};val refreshToken=context.sessionStore.data.map{it[refresh]};val userName=context.sessionStore.data.map{it[name]}
 suspend fun updateAccess(value:String){context.sessionStore.edit{it[access]=value}}
 suspend fun save(accessToken:String,refreshToken:String,userName:String){context.sessionStore.edit{it[access]=accessToken;it[refresh]=refreshToken;it[name]=userName}}
 suspend fun clear(){context.sessionStore.edit{it.clear()}}
 fun deviceKey():String=Settings.Secure.getString(context.contentResolver,Settings.Secure.ANDROID_ID)?:"android"
}

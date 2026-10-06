package com.superiptv.app.data.preferences
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
private val Context.dataStore by preferencesDataStore("superiptv_preferences")
class AppPreferences(private val context:Context){
 private val onboarding=booleanPreferencesKey("onboarding_completed")
 val onboardingCompleted=context.dataStore.data.map{it[onboarding]?:false}
 suspend fun setOnboardingCompleted(value:Boolean){context.dataStore.edit{it[onboarding]=value}}
}

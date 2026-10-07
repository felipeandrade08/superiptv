package com.superiptv.app.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
data class AuthState(val checking:Boolean=true,val logged:Boolean=false,val loading:Boolean=false,val name:String="",val error:String?=null)
class AuthViewModel(app:Application):AndroidViewModel(app){private val sessions=SessionRepository(app);private val api=SuperIptvApi(BuildConfig.API_BASE_URL);private val state=MutableStateFlow(AuthState());val ui=state.asStateFlow()
 init{viewModelScope.launch{sessions.accessToken.collect{token->state.value=state.value.copy(checking=false,logged=!token.isNullOrBlank())}}}
 fun login(email:String,password:String)=viewModelScope.launch(Dispatchers.IO){state.value=state.value.copy(loading=true,error=null);try{val d=api.login(email,password,sessions.deviceKey());val user=d.getJSONObject("user");sessions.save(d.getString("accessToken"),d.getString("refreshToken"),user.getString("name"));state.value=AuthState(false,true,false,user.getString("name"))}catch(e:Exception){state.value=AuthState(false,false,false,error=e.message)}}}
}

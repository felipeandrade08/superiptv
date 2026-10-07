package com.superiptv.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.superiptv.app.BuildConfig
import com.superiptv.app.data.SessionRepository
import com.superiptv.app.data.SuperIptvApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class AuthState(
    val checking: Boolean = true,
    val logged: Boolean = false,
    val loading: Boolean = false,
    val name: String = "",
    val error: String? = null
)

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val sessions = SessionRepository(app)
    private val api = SuperIptvApi(BuildConfig.API_BASE_URL)
    private val state = MutableStateFlow(AuthState())
    val ui = state.asStateFlow()

    init {
        viewModelScope.launch {
            sessions.accessToken.collect { token ->
                state.value = state.value.copy(
                    checking = false,
                    logged = !token.isNullOrBlank()
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            val refresh = sessions.refreshToken.firstOrNull()
            try { if (!refresh.isNullOrBlank()) api.logout(refresh) } catch (_: Exception) {}
            sessions.clear()
            state.value = AuthState(checking = false, logged = false)
        }
    }

    fun refreshSession() {
        viewModelScope.launch(Dispatchers.IO) {
            val refresh = sessions.refreshToken.firstOrNull()
            if (refresh.isNullOrBlank()) { sessions.clear(); return@launch }
            try {
                val data = api.refresh(refresh)
                sessions.updateAccess(data.getString("accessToken"))
            } catch (_: Exception) {
                sessions.clear()
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state.value = state.value.copy(loading = true, error = null)
            try {
                val data = api.login(email, password, sessions.deviceKey())
                val user = data.getJSONObject("user")
                sessions.save(
                    accessToken = data.getString("accessToken"),
                    refreshToken = data.getString("refreshToken"),
                    userName = user.getString("name")
                )
                state.value = AuthState(
                    checking = false,
                    logged = true,
                    loading = false,
                    name = user.getString("name")
                )
            } catch (error: Exception) {
                state.value = AuthState(
                    checking = false,
                    logged = false,
                    loading = false,
                    error = error.message ?: "Não foi possível entrar."
                )
            }
        }
    }
}

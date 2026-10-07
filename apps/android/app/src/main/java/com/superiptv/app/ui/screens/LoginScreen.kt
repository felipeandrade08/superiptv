package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.superiptv.app.ui.AuthState
@Composable fun LoginScreen(state:AuthState,onLogin:(String,String)->Unit){var email by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Card(Modifier.widthIn(max=440.dp).padding(24.dp)){Column(Modifier.padding(28.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){Text("SuperIPTV",style=MaterialTheme.typography.headlineLarge);Text("Entre com a conta liberada pelo administrador.");OutlinedTextField(email,{email=it},label={Text("E-mail")},singleLine=true);OutlinedTextField(password,{password=it},label={Text("Senha")},visualTransformation=PasswordVisualTransformation(),singleLine=true);Button(onClick={onLogin(email,password)},enabled=!state.loading&&email.isNotBlank()&&password.isNotBlank(),modifier=Modifier.fillMaxWidth()){Text(if(state.loading)"Entrando..." else "Entrar")};state.error?.let{Text(it,color=MaterialTheme.colorScheme.error)}}}}}

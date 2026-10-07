package com.superiptv.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.tv.data.TvChannel

class TvActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TvApp() }
    }
}

@Composable
fun TvApp(vm: TvViewModel = viewModel()) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF8B7CFF),
            background = Color(0xFF08090D)
        )
    ) {
        val channels by vm.live.collectAsState()
        val movies by vm.movies.collectAsState()
        val series by vm.series.collectAsState()
        val continueWatching by vm.continueWatching.collectAsState()
        val auth by vm.auth.collectAsState()
        if (auth.checking) { Box(Modifier.fillMaxSize(),contentAlignment=androidx.compose.ui.Alignment.Center){CircularProgressIndicator()}; return@MaterialTheme }
        if (!auth.logged) { TvLogin(auth,vm::login); return@MaterialTheme }
        var group by remember { mutableStateOf<String?>(null) }
        var section by remember { mutableStateOf("live") }
        var playing by remember { mutableStateOf<TvChannel?>(null) }

        when {
            playing != null -> TvPlayerScreen(channel = playing!!, onBack = { playing = null }, vm = vm)
            group != null -> {
                BackHandler { group = null }
                val source=when(section){"movie"->movies;"series"->series;else->channels}
                TvChannelGrid(title=group!!,channels=source.filter{it.groupName==group},onPlay={playing=it})
            }
            else -> TvHome(channels,movies,series,continueWatching,onPlay={id->playing=(channels+movies+series).firstOrNull{it.id==id}},onSection={type,name->section=type;group=name})
        }
    }
}

@Composable
private fun TvHome(live:List<TvChannel>,movies:List<TvChannel>,series:List<TvChannel>,recent:List<com.superiptv.tv.data.TvHistory>,onPlay:(String)->Unit,onSection:(String,String)->Unit) {
    Column(Modifier.fillMaxSize().padding(56.dp),verticalArrangement=Arrangement.spacedBy(24.dp)) {
        Text("SuperIPTV",style=MaterialTheme.typography.displayMedium)
        if(recent.isNotEmpty()){Text("Continue assistindo",style=MaterialTheme.typography.headlineMedium);LazyRow(horizontalArrangement=Arrangement.spacedBy(18.dp)){items(recent.take(8),key={it.itemId}){item->FocusCard(item.name,progressLabel(item.positionMs,item.durationMs)){onPlay(item.itemId)}}}}
        listOf("live" to ("TV ao vivo" to live),"movie" to ("Filmes" to movies),"series" to ("Séries" to series)).forEach{entry->
            val type=entry.first;val title=entry.second.first;val items=entry.second.second
            Text(title,style=MaterialTheme.typography.headlineMedium)
            if(items.isEmpty())Text("Nenhum conteúdo disponível.",color=MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(horizontalArrangement=Arrangement.spacedBy(18.dp)){items(items.map{it.groupName}.distinct()){group->FocusCard(group,items.count{it.groupName==group}.toString()+" itens"){onSection(type,group)}}}
        }
    }
}
private fun progressLabel(position:Long,duration:Long)=if(duration>0)(((position*100)/duration).coerceIn(0,100)).toString()+"% assistido" else "Em andamento"

@Composable
private fun TvChannelGrid(
    title: String,
    channels: List<TvChannel>,
    onPlay: (TvChannel) -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(56.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                FocusCard(
                    title = channel.name,
                    subtitle = channel.groupName,
                    onClick = { onPlay(channel) }
                )
            }
        }
    }
}

@Composable
private fun FocusCard(title: String, subtitle: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(260.dp)
            .height(150.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable(),
        colors = CardDefaults.cardColors(
            containerColor = if (focused) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle)
        }
    }
}

@Composable private fun TvLogin(state:TvAuthState,onLogin:(String,String)->Unit){var email by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};Box(Modifier.fillMaxSize(),contentAlignment=androidx.compose.ui.Alignment.Center){Card(Modifier.width(520.dp)){Column(Modifier.padding(32.dp),verticalArrangement=Arrangement.spacedBy(18.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displaySmall);Text("Entre com a conta liberada pelo administrador.");OutlinedTextField(email,{email=it},label={Text("E-mail")},modifier=Modifier.fillMaxWidth());OutlinedTextField(password,{password=it},label={Text("Senha")},modifier=Modifier.fillMaxWidth());Button(onClick={onLogin(email,password)},enabled=!state.loading,modifier=Modifier.fillMaxWidth()){Text(if(state.loading)"Entrando..." else "Entrar")};state.error?.let{Text(it,color=MaterialTheme.colorScheme.error)}}}}}

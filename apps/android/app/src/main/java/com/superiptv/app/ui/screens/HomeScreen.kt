package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.app.player.PlayerViewModel
@Composable fun HomeScreen(onPlay:(String)->Unit,vm:PlayerViewModel=viewModel()){val recent by vm.continueWatching.collectAsState();Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displaySmall);Text("Seu catálogo liberado pelo administrador.",color=MaterialTheme.colorScheme.onSurfaceVariant);Card{Column(Modifier.fillMaxWidth().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Icon(Icons.Rounded.LiveTv,null,tint=MaterialTheme.colorScheme.primary);Text("TV, filmes e séries",style=MaterialTheme.typography.titleLarge);Text("Reprodução protegida, favoritos e progresso local para conteúdo sob demanda.")}};Text("Continue assistindo",style=MaterialTheme.typography.titleMedium);if(recent.isEmpty())Text("Seu progresso aparecerá aqui após assistir filmes ou séries.",color=MaterialTheme.colorScheme.onSurfaceVariant) else recent.take(8).forEach{item->Card(onClick={onPlay(item.channelId)}){Column(Modifier.fillMaxWidth().padding(14.dp)){Text(item.name,style=MaterialTheme.typography.titleMedium);Text(item.groupName+" · "+formatProgress(item.positionMs,item.durationMs),style=MaterialTheme.typography.bodySmall)}}}}}
private fun formatProgress(position:Long,duration:Long):String{if(duration<=0)return "Em andamento";val percent=((position*100)/duration).coerceIn(0,100);return "$percent% assistido"}

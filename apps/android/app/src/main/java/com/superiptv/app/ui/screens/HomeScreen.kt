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
@Composable fun HomeScreen(vm:PlayerViewModel=viewModel()){val recent by vm.continueWatching.collectAsState();Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displaySmall);Text("Suas fontes. Sua organização. Seu player.",color=MaterialTheme.colorScheme.onSurfaceVariant);Card{Column(Modifier.fillMaxWidth().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Icon(Icons.Rounded.LiveTv,null,tint=MaterialTheme.colorScheme.primary);Text("Player Media3",style=MaterialTheme.typography.titleLarge);Text("Abra um canal na aba TV para iniciar a reprodução. PiP, reconexão e histórico já fazem parte da sessão.")}};Text("Continue assistindo",style=MaterialTheme.typography.titleMedium);if(recent.isEmpty())Text("Seu progresso aparecerá aqui após assistir conteúdo sob demanda.",color=MaterialTheme.colorScheme.onSurfaceVariant) else recent.take(5).forEach{item->Text(item.name+" · "+item.groupName)}}}
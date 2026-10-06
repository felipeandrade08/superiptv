package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.app.ui.PlaylistViewModel
@Composable fun LibraryScreen(vm:PlaylistViewModel=viewModel()){val state by vm.state.collectAsStateWithLifecycle();val favorites=state.channels.filter{channel->channel.favorite};Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("Minha lista",style=MaterialTheme.typography.headlineMedium);Text("Favoritos",style=MaterialTheme.typography.titleMedium);if(favorites.isEmpty())Text("Marque canais com o coração para encontrá-los aqui.") else favorites.forEach{channel->Text("♥  "+channel.name+" · "+channel.groupName)}}}

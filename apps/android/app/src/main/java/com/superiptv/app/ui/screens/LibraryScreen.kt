package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.app.ui.PlaylistViewModel
@Composable fun LibraryScreen(onPlay:(String)->Unit,vm:PlaylistViewModel=viewModel()){val state by vm.state.collectAsStateWithLifecycle();val live=state.channels.filter{it.favorite};val movies=state.movies.filter{it.favorite};val series=state.series.filter{it.favorite};Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("Minha lista",style=MaterialTheme.typography.headlineMedium);Text("Favoritos",style=MaterialTheme.typography.titleMedium);if(live.isEmpty()&&movies.isEmpty()&&series.isEmpty())Text("Marque canais, filmes ou séries com o coração para encontrá-los aqui.") else{if(live.isNotEmpty())Text("TV ao vivo",style=MaterialTheme.typography.titleSmall);live.forEach{item->TextButton(onClick={onPlay(item.id)}){Text("♥  "+item.name+" · "+item.groupName)}};if(movies.isNotEmpty())Text("Filmes",style=MaterialTheme.typography.titleSmall);movies.forEach{item->TextButton(onClick={onPlay(item.id)}){Text("♥  "+item.name+" · "+item.groupName)}};if(series.isNotEmpty())Text("Séries",style=MaterialTheme.typography.titleSmall);series.forEach{item->TextButton(onClick={onPlay(item.id)}){Text("♥  "+(item.seriesName?:item.name)+" · "+item.groupName)}}}}}

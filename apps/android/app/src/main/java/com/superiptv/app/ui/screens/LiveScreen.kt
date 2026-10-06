package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.app.ui.PlaylistViewModel
@Composable fun LiveScreen(onPlay:(String)->Unit,vm:PlaylistViewModel=viewModel()){
 val state by vm.state.collectAsStateWithLifecycle()
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text("TV ao vivo",style=MaterialTheme.typography.headlineMedium)
  Text("Catálogo disponibilizado pela sua conta SuperIPTV.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  OutlinedTextField(value=state.query,onValueChange=vm::setQuery,modifier=Modifier.fillMaxWidth(),label={Text("Buscar canal ou categoria")},singleLine=true)
  if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth())
  state.message?.let{message->AssistChip(onClick=vm::clearMessage,label={Text(message)})}
  if(state.channels.isEmpty())Text("Nenhum conteúdo liberado para esta conta.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(state.channels,key={channel->channel.id}){channel->Card(onClick={onPlay(channel.id)}){Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(channel.name,style=MaterialTheme.typography.titleMedium);Text(channel.groupName,style=MaterialTheme.typography.bodySmall)};IconButton(onClick={vm.favorite(channel)}){Icon(if(channel.favorite)Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,"Favorito")}}}}}
 }
}
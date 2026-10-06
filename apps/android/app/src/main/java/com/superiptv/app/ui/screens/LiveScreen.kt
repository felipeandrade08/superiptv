package com.superiptv.app.ui.screens
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
@Composable fun LiveScreen(vm:PlaylistViewModel=viewModel()){
 val state by vm.state.collectAsStateWithLifecycle();var dialog by remember{mutableStateOf(false)}
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri:Uri?->if(uri!=null)vm.importFile("Playlist importada",uri)}
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("TV ao vivo",style=MaterialTheme.typography.headlineMedium);Button(onClick={dialog=true}){Text("Adicionar")}}
  OutlinedTextField(value=state.query,onValueChange=vm::setQuery,modifier=Modifier.fillMaxWidth(),label={Text("Buscar canal ou categoria")},singleLine=true)
  if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth())
  state.message?.let{message->AssistChip(onClick=vm::clearMessage,label={Text(message)})}
  if(state.channels.isEmpty())Text("Nenhum canal importado. Adicione uma playlist M3U por URL ou arquivo.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(state.channels,key={channel->channel.id}){channel->Card{Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(channel.name,style=MaterialTheme.typography.titleMedium);Text(channel.groupName,style=MaterialTheme.typography.bodySmall)};IconButton(onClick={vm.favorite(channel)}){Icon(if(channel.favorite)Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,"Favorito")}}}}}
 }
 if(dialog)AddPlaylistDialog(onDismiss={dialog=false},onUrl={name,url->vm.importUrl(name,url);dialog=false},onFile={picker.launch(arrayOf("audio/x-mpegurl","application/vnd.apple.mpegurl","text/plain","*/*"));dialog=false})
}
@Composable private fun AddPlaylistDialog(onDismiss:()->Unit,onUrl:(String,String)->Unit,onFile:()->Unit){var name by remember{mutableStateOf("")};var url by remember{mutableStateOf("")};AlertDialog(onDismissRequest=onDismiss,title={Text("Adicionar playlist")},text={Column(verticalArrangement=Arrangement.spacedBy(10.dp)){OutlinedTextField(name,{name=it},label={Text("Nome")});OutlinedTextField(url,{url=it},label={Text("URL M3U/M3U8")});OutlinedButton(onClick=onFile,modifier=Modifier.fillMaxWidth()){Text("Selecionar arquivo")}}},confirmButton={Button(onClick={onUrl(name,url)},enabled=url.isNotBlank()){Text("Importar URL")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancelar")}})}

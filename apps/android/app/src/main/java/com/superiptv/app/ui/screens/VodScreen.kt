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
@Composable fun VodScreen(type:String,onPlay:(String)->Unit,vm:PlaylistViewModel=viewModel()){
 val state by vm.state.collectAsStateWithLifecycle();val items=if(type=="movie")state.movies else state.series;val title=if(type=="movie")"Filmes" else "Séries"
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text(title,style=MaterialTheme.typography.headlineMedium);OutlinedTextField(state.query,vm::setQuery,Modifier.fillMaxWidth(),label={Text("Buscar em $title")},singleLine=true)
  if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth());if(items.isEmpty())Text("Nenhum conteúdo de $title liberado para esta conta.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(items,key={it.id}){item->Card(onClick={onPlay(item.id)}){Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(item.seriesName?:item.name,style=MaterialTheme.typography.titleMedium);val meta=listOfNotNull(item.groupName,item.year?.toString(),item.seasonNumber?.let{"T$it"},item.episodeNumber?.let{"E$it"}).joinToString(" · ");Text(meta,style=MaterialTheme.typography.bodySmall);item.description?.let{Text(it,maxLines=2,style=MaterialTheme.typography.bodySmall)}};IconButton(onClick={vm.favorite(item)}){Icon(if(item.favorite)Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,"Favorito")}}}}}
 }
}

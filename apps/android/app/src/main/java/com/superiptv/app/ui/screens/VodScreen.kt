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
import com.superiptv.app.data.local.VodEntity
import com.superiptv.app.ui.PlaylistViewModel
@Composable fun VodScreen(type:String,onPlay:(String)->Unit,vm:PlaylistViewModel=viewModel()){
 val state by vm.state.collectAsStateWithLifecycle();val all=if(type=="movie")state.movies else state.series;val title=if(type=="movie")"Filmes" else "Séries";var category by remember{mutableStateOf<String?>(null)}
 val categories=all.map{it.groupName}.distinct();val items=category?.let{selected->all.filter{it.groupName==selected}}?:all
 Column(Modifier.fillMaxSize().padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
  Text(title,style=MaterialTheme.typography.headlineMedium);OutlinedTextField(state.query,vm::setQuery,Modifier.fillMaxWidth(),label={Text("Buscar em $title")},singleLine=true)
  if(categories.isNotEmpty())LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){item{FilterChip(selected=category==null,onClick={category=null},label={Text("Todos")})};items(categories){name->FilterChip(selected=category==name,onClick={category=name},label={Text(name)})}}
  if(state.loading)LinearProgressIndicator(Modifier.fillMaxWidth());if(items.isEmpty())Text("Nenhum conteúdo de $title nesta seleção.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  if(type=="series")SeriesList(items,onPlay,vm::favorite) else VodList(items,onPlay,vm::favorite)
 }
}
@Composable private fun VodList(items:List<VodEntity>,onPlay:(String)->Unit,onFavorite:(VodEntity)->Unit){LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(items,key={it.id}){item->VodCard(item,onPlay,onFavorite)}}}
@Composable private fun SeriesList(items:List<VodEntity>,onPlay:(String)->Unit,onFavorite:(VodEntity)->Unit){
 val grouped=items.groupBy{it.seriesName?.takeIf{name->name.isNotBlank()}?:it.name}
 LazyColumn(verticalArrangement=Arrangement.spacedBy(12.dp)){grouped.forEach{(seriesName,episodes)->item(key="series:$seriesName"){Column(verticalArrangement=Arrangement.spacedBy(6.dp)){Text(seriesName,style=MaterialTheme.typography.titleLarge);episodes.sortedWith(compareBy<VodEntity>{it.seasonNumber?:Int.MAX_VALUE}.thenBy{it.episodeNumber?:Int.MAX_VALUE}.thenBy{it.name}).forEach{episode->VodCard(episode,onPlay,onFavorite)}}}}}
}
@Composable private fun VodCard(item:VodEntity,onPlay:(String)->Unit,onFavorite:(VodEntity)->Unit){Card(onClick={onPlay(item.id)}){Row(Modifier.fillMaxWidth().padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Column(Modifier.weight(1f)){Text(item.name,style=MaterialTheme.typography.titleMedium);val meta=listOfNotNull(item.groupName,item.year?.toString(),item.seasonNumber?.let{"T$it"},item.episodeNumber?.let{"E$it"}).joinToString(" · ");Text(meta,style=MaterialTheme.typography.bodySmall);item.description?.let{Text(it,maxLines=2,style=MaterialTheme.typography.bodySmall)}};IconButton(onClick={onFavorite(item)}){Icon(if(item.favorite)Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,"Favorito")}}}}

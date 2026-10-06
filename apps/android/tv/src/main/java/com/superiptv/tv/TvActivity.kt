package com.superiptv.tv
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.tv.data.TvChannel
class TvActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{TvApp()}}}
@Composable fun TvApp(vm:TvViewModel=viewModel()){MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF8B7CFF),background=Color(0xFF08090D))){val channels by vm.channels.collectAsState();var group by remember{mutableStateOf<String?>(null)};var playing by remember{mutableStateOf<TvChannel?>(null)};when{playing!=null->TvPlayerScreen(playing!!){playing=null};group!=null->{BackHandler{group=null};TvChannelGrid(group!!,channels.filter{it.groupName==group}){playing=it}};else->TvHome(channels){group=it}}}}}
@Composable private fun TvHome(channels:List<TvChannel>,onGroup:(String)->Unit){val groups=channels.map{it.groupName}.distinct();Column(Modifier.fillMaxSize().padding(56.dp),verticalArrangement=Arrangement.spacedBy(24.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displayMedium);Text("Ao vivo",style=MaterialTheme.typography.headlineMedium);if(groups.isEmpty())Text("Nenhuma playlist configurada nesta TV. A importação/sincronização será ligada na próxima etapa.",color=MaterialTheme.colorScheme.onSurfaceVariant);LazyRow(horizontalArrangement=Arrangement.spacedBy(18.dp)){items(groups){g->FocusCard(g,channels.count{it.groupName==g}.toString()+" canais"){onGroup(g)}}}}}
@Composable private fun TvChannelGrid(title:String,items:List<TvChannel>,onPlay:(TvChannel)->Unit){Column(Modifier.fillMaxSize().padding(56.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){Text(title,style=MaterialTheme.typography.headlineLarge);LazyVerticalGrid(columns=androidx.compose.foundation.lazy.grid.GridCells.Adaptive(240.dp),horizontalArrangement=Arrangement.spacedBy(16.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){items(items.size){i->val c=items[i];FocusCard(c.name,c.groupName){onPlay(c)}}}}}
@Composable private fun FocusCard(title:String,subtitle:String,onClick:()->Unit){var focused by remember{mutableStateOf(false)};Card(onClick=onClick,modifier=Modifier.width(260.dp).height(150.dp).onFocusChanged{focused=it.isFocused}.focusable(),colors=CardDefaults.cardColors(containerColor=if(focused)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)){Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.Bottom){Text(title,style=MaterialTheme.typography.titleLarge);Text(subtitle)}}}

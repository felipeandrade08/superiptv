package com.superiptv.tv
import android.os.Bundle
import androidx.activity.ComponentActivity
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

class TvActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{TvApp()}}}
data class TvDestination(val title:String,val subtitle:String)
@Composable fun TvApp(){MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF8B7CFF),background=Color(0xFF08090D))){val sections=listOf(TvDestination("Ao vivo","Canais das suas playlists"),TvDestination("Filmes","Seu catálogo VOD"),TvDestination("Séries","Temporadas e episódios"),TvDestination("Minha lista","Favoritos e histórico"),TvDestination("Configurações","Fontes e preferências"));Column(Modifier.fillMaxSize().padding(horizontal=56.dp,vertical=40.dp),verticalArrangement=Arrangement.spacedBy(28.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displayMedium);Text("Escolha onde quer assistir",style=MaterialTheme.typography.headlineSmall,color=MaterialTheme.colorScheme.onSurfaceVariant);LazyRow(horizontalArrangement=Arrangement.spacedBy(18.dp)){items(sections){section->TvCard(section)}}}}}
@Composable private fun TvCard(section:TvDestination){var focused by remember{mutableStateOf(false)};Card(modifier=Modifier.width(260.dp).height(160.dp).onFocusChanged{focused=it.isFocused}.focusable(),colors=CardDefaults.cardColors(containerColor=if(focused)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)){Column(Modifier.fillMaxSize().padding(22.dp),verticalArrangement=Arrangement.Bottom){Text(section.title,style=MaterialTheme.typography.headlineSmall);Text(section.subtitle,style=MaterialTheme.typography.bodyMedium)}}}

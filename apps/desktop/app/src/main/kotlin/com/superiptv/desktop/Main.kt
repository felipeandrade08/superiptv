package com.superiptv.desktop
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
fun main()=application{Window(onCloseRequest=::exitApplication,title="SuperIPTV"){DesktopApp()}}
@Composable fun DesktopApp(){MaterialTheme(colorScheme=darkColorScheme()){var section by remember{mutableStateOf("Início")};Row(Modifier.fillMaxSize()){NavigationRail{listOf("Início","Ao vivo","Filmes","Séries","Minha lista","Configurações").forEach{s->NavigationRailItem(selected=section==s,onClick={section=s},icon={Text(s.take(1))},label={Text(s)})}};Column(Modifier.fillMaxSize().padding(32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){Text("SuperIPTV Desktop",style=MaterialTheme.typography.displaySmall);Text(section,style=MaterialTheme.typography.headlineMedium);Text("Fundação Desktop preparada. O próximo bloco liga importação M3U, persistência e player nativo.")}}}}

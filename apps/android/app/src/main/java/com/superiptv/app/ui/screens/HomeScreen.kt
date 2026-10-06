package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLink
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun HomeScreen(){
 Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){
  Text("Boa noite",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.secondary)
  Text("SuperIPTV",style=MaterialTheme.typography.displaySmall)
  Text("Sua central de entretenimento, organizada do seu jeito.",color=MaterialTheme.colorScheme.onSurfaceVariant)
  Card{Column(Modifier.fillMaxWidth().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
   Icon(Icons.Rounded.AddLink,null,tint=MaterialTheme.colorScheme.primary)
   Text("Adicione sua primeira fonte",style=MaterialTheme.typography.titleLarge)
   Text("No próximo pacote conectaremos playlists M3U/M3U8. A estrutura local já está preparada para receber seus dados.")
   Button(onClick={}){Text("Preparar playlist")}
  }}
  Text("Continue assistindo",style=MaterialTheme.typography.titleMedium)
  Text("Nada por aqui ainda.",color=MaterialTheme.colorScheme.onSurfaceVariant)
 }
}

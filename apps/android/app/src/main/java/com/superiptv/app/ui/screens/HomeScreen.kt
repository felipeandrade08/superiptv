package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun HomeScreen(){Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(20.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displaySmall);Text("Suas fontes. Sua organização. Seu player.",color=MaterialTheme.colorScheme.onSurfaceVariant);Card{Column(Modifier.fillMaxWidth().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Icon(Icons.Rounded.LiveTv,null,tint=MaterialTheme.colorScheme.primary);Text("TV e playlists",style=MaterialTheme.typography.titleLarge);Text("Importe sua própria playlist pela aba TV. O SuperIPTV não fornece canais, filmes, séries ou listas.")}};Text("Continue assistindo",style=MaterialTheme.typography.titleMedium);Text("O histórico de reprodução será ativado junto do player.",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
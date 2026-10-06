package com.superiptv.app.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable fun SectionScreen(title:String,description:String){Column(Modifier.fillMaxSize().padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text(title,style=MaterialTheme.typography.headlineMedium);Text(description,color=MaterialTheme.colorScheme.onSurfaceVariant);HorizontalDivider();Text("Estado vazio",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)}}

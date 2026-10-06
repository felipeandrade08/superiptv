package com.superiptv.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superiptv.tv.data.TvChannel

class TvActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TvApp() }
    }
}

@Composable
fun TvApp(vm: TvViewModel = viewModel()) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF8B7CFF),
            background = Color(0xFF08090D)
        )
    ) {
        val channels by vm.channels.collectAsState()
        var group by remember { mutableStateOf<String?>(null) }
        var playing by remember { mutableStateOf<TvChannel?>(null) }

        when {
            playing != null -> TvPlayerScreen(playing!!) { playing = null }
            group != null -> {
                BackHandler { group = null }
                TvChannelGrid(
                    title = group!!,
                    channels = channels.filter { it.groupName == group },
                    onPlay = { playing = it }
                )
            }
            else -> TvHome(channels = channels, onGroup = { group = it })
        }
    }
}

@Composable
private fun TvHome(channels: List<TvChannel>, onGroup: (String) -> Unit) {
    val groups = channels.map { it.groupName }.distinct()
    Column(
        Modifier.fillMaxSize().padding(56.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("SuperIPTV", style = MaterialTheme.typography.displayMedium)
        Text("Ao vivo", style = MaterialTheme.typography.headlineMedium)
        if (groups.isEmpty()) {
            Text(
                "Nenhum catálogo liberado para esta TV.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            items(groups) { group ->
                FocusCard(
                    title = group,
                    subtitle = channels.count { it.groupName == group }.toString() + " canais",
                    onClick = { onGroup(group) }
                )
            }
        }
    }
}

@Composable
private fun TvChannelGrid(
    title: String,
    channels: List<TvChannel>,
    onPlay: (TvChannel) -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(56.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        LazyVerticalGrid(
            columns = GridCells.Adaptive(240.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(channels, key = { it.id }) { channel ->
                FocusCard(
                    title = channel.name,
                    subtitle = channel.groupName,
                    onClick = { onPlay(channel) }
                )
            }
        }
    }
}

@Composable
private fun FocusCard(title: String, subtitle: String, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(260.dp)
            .height(150.dp)
            .onFocusChanged { focused = it.isFocused }
            .focusable(),
        colors = CardDefaults.cardColors(
            containerColor = if (focused) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle)
        }
    }
}

package com.superiptv.tv
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.superiptv.tv.data.TvChannel
@OptIn(UnstableApi::class)
@Composable fun TvPlayerScreen(channel:TvChannel,onBack:()->Unit){val context=LocalContext.current;val player=remember{ExoPlayer.Builder(context).build()};LaunchedEffect(channel.id){player.setMediaItem(MediaItem.fromUri(channel.streamUrl));player.prepare();player.playWhenReady=true};DisposableEffect(player){onDispose{player.release()}};AndroidView(factory={PlayerView(it).apply{this.player=player;useController=true;layoutParams=ViewGroup.LayoutParams(-1,-1);isFocusable=true;requestFocus()}},modifier=Modifier.fillMaxSize().onPreviewKeyEvent{e->if(e.type==KeyEventType.KeyUp&&e.nativeKeyEvent.keyCode==KeyEvent.KEYCODE_BACK){onBack();true}else false})}

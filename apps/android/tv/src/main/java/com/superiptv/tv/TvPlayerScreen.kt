package com.superiptv.tv
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.superiptv.tv.data.TvChannel

@Composable fun TvPlayerScreen(channel:TvChannel,onBack:()->Unit,vm:TvViewModel=viewModel()){
 val context=LocalContext.current
 val player=remember{ExoPlayer.Builder(context).build()}
 fun save(){vm.saveProgress(channel,player.currentPosition,player.duration.takeIf{it>0}?:0)}
 LaunchedEffect(channel.id){val url=vm.playbackUrl(channel.id);val resume=vm.resumePosition(channel.id);player.setMediaItem(MediaItem.fromUri(url));player.prepare();if(resume>0)player.seekTo(resume);player.playWhenReady=true}
 DisposableEffect(player){onDispose{save();player.release()}}
 AndroidView(factory={PlayerView(it).apply{this.player=player;useController=true;layoutParams=ViewGroup.LayoutParams(-1,-1);isFocusable=true;requestFocus()}},modifier=Modifier.fillMaxSize().onPreviewKeyEvent{e->if(e.type==KeyEventType.KeyUp&&e.nativeKeyEvent.keyCode==KeyEvent.KEYCODE_BACK){save();onBack();true}else false})
}

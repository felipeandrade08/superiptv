package com.superiptv.app.ui.screens
import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.superiptv.app.data.local.ChannelEntity
import com.superiptv.app.player.PlayerViewModel

@OptIn(UnstableApi::class)
@Composable fun PlayerScreen(channelId:String,onBack:()->Unit,vm:PlayerViewModel=viewModel()){
 val channels by vm.channels.collectAsState();val initial=channels.indexOfFirst{it.id==channelId}
 if(initial<0){LaunchedEffect(Unit){onBack()};return}
 val context=LocalContext.current;val activity=context as? Activity
 val player=remember{ExoPlayer.Builder(context).build()};var currentIndex by remember(channelId){mutableIntStateOf(initial)};var retries by remember{mutableIntStateOf(0)}
 val current=channels.getOrNull(currentIndex)?:return
 fun saveCurrent(){vm.save(current,player.currentPosition,player.duration.takeIf{it>0}?:0)}
 fun playAt(index:Int){val target=channels.getOrNull(index)?:return;saveCurrent();currentIndex=index;retries=0;player.setMediaItem(MediaItem.fromUri(target.streamUrl));player.prepare();player.playWhenReady=true}
 LaunchedEffect(channelId){player.setMediaItem(MediaItem.fromUri(current.streamUrl));player.prepare();player.playWhenReady=true}
 DisposableEffect(player){val listener=object:Player.Listener{override fun onPlayerError(error:androidx.media3.common.PlaybackException){if(retries<3){retries++;player.prepare();player.play()}}};player.addListener(listener);onDispose{saveCurrent();player.removeListener(listener);player.release()}}
 Column(Modifier.fillMaxSize()){
  AndroidView(factory={PlayerView(it).apply{this.player=player;useController=true;layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)}},modifier=Modifier.weight(1f).fillMaxWidth())
  Text(current.name,modifier=Modifier.padding(horizontal=16.dp),style=MaterialTheme.typography.titleMedium)
  Row(Modifier.fillMaxWidth().padding(8.dp),horizontalArrangement=Arrangement.SpaceEvenly){
   IconButton(onClick={playAt(currentIndex-1)},enabled=currentIndex>0){Icon(Icons.Rounded.SkipPrevious,"Anterior")}
   IconButton(onClick={if(player.isPlaying)player::pause else player::play}){Icon(if(player.isPlaying)Icons.Rounded.Pause else Icons.Rounded.PlayArrow,"Reproduzir")}
   IconButton(onClick={playAt(currentIndex+1)},enabled=currentIndex<channels.lastIndex){Icon(Icons.Rounded.SkipNext,"Próximo")}
   IconButton(onClick={if(Build.VERSION.SDK_INT>=26)activity?.enterPictureInPictureMode(PictureInPictureParams.Builder().setAspectRatio(Rational(16,9)).build())}){Icon(Icons.Rounded.PictureInPictureAlt,"PiP")}
   TextButton(onClick=onBack){Text("Voltar")}
  }
 }
}
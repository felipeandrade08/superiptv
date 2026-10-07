package com.superiptv.desktop
import com.sun.jna.NativeLibrary
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.BorderLayout
import java.nio.file.Path
import javax.swing.JPanel

object VlcRuntime {
 fun configure():Boolean{
  val bundled=Path.of(System.getProperty("user.dir"),"vlc").toFile()
  if(bundled.exists()){System.setProperty("jna.library.path",bundled.absolutePath);NativeLibrary.addSearchPath("libvlc",bundled.absolutePath);return true}
  return NativeDiscovery().discover()
 }
}
class DesktopPlayerPanel:JPanel(BorderLayout()){
 private val available=VlcRuntime.configure();private val component=if(available)EmbeddedMediaPlayerComponent() else null
 init{component?.let{add(it,BorderLayout.CENTER)}}
 fun isAvailable()=available
 fun play(url:String){component?.mediaPlayer()?.media()?.play(url,":http-referrer=SuperIPTV",":http-user-agent=SuperIPTV Desktop")}
 fun stop(){component?.mediaPlayer()?.controls()?.stop()}
 fun release(){component?.release()}
}
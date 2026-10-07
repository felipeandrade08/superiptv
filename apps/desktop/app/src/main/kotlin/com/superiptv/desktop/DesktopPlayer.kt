package com.superiptv.desktop
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.BorderLayout
import javax.swing.JPanel

class DesktopPlayerPanel(private val tokenProvider:()->String?):JPanel(BorderLayout()){
 private val component=EmbeddedMediaPlayerComponent()
 init{add(component,BorderLayout.CENTER)}
 fun play(url:String){val token=tokenProvider().orEmpty();component.mediaPlayer().media().play(url,":http-referrer=SuperIPTV",":http-user-agent=SuperIPTV Desktop",":http-header=Authorization: Bearer $token")}
 fun stop(){component.mediaPlayer().controls().stop()}
 fun release(){component.release()}
}

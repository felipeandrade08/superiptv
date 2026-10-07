package com.superiptv.desktop
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

private const val API="http://localhost:3000"
fun main()=application{Window(onCloseRequest=::exitApplication,title="SuperIPTV"){DesktopApp()}}
@Composable fun DesktopApp(){
 val session=remember{DesktopSession()};val api=remember{DesktopApi(API)};val scope=rememberCoroutineScope()
 var logged by remember{mutableStateOf(!session.access.isNullOrBlank())};var selected by remember{mutableStateOf<DesktopChannel?>(null)};var loading by remember{mutableStateOf(false)};var error by remember{mutableStateOf<String?>(null)};var channels by remember{mutableStateOf(session.cached())}
 MaterialTheme(colorScheme=darkColorScheme()){
  if(!logged){Login(loading,error){email,password->scope.launch{loading=true;error=null;try{val d=withContext(Dispatchers.IO){api.login(email,password,session.deviceKey)};session.save(d.getString("accessToken"),d.getString("refreshToken"));logged=true;channels=withContext(Dispatchers.IO){api.catalog(d.getString("accessToken"))};session.cache(channels)}catch(e:Exception){error=e.message}finally{loading=false}}};return@MaterialTheme}
  LaunchedEffect(logged){if(logged){while(logged){val refresh=session.refresh;if(refresh==null){session.clear();logged=false;break};val renewed=runCatching{withContext(Dispatchers.IO){api.refresh(refresh)}}.getOrNull();if(renewed==null){session.clear();logged=false;break};session.save(renewed.getString("accessToken"),renewed.getString("refreshToken"));runCatching{withContext(Dispatchers.IO){api.catalog(session.access!!)}}.onSuccess{channels=it;session.cache(it)};delay(10*60*1000L)}}}
  var section by remember{mutableStateOf("Início")};Row(Modifier.fillMaxSize()){NavigationRail{listOf("Início","Ao vivo","Filmes","Séries","Minha lista","Configurações").forEach{s->NavigationRailItem(selected=section==s,onClick={section=s},icon={Text(s.take(1))},label={Text(s)})}}
   Column(Modifier.fillMaxSize().padding(32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column{Text("SuperIPTV Desktop",style=MaterialTheme.typography.displaySmall);Text(section,style=MaterialTheme.typography.headlineMedium)};TextButton(onClick={scope.launch{val refresh=session.refresh;withContext(Dispatchers.IO){runCatching{if(refresh!=null)api.logout(refresh)}};session.clear();logged=false}}){Text("Sair")}}
    when{selected!=null->DesktopPlayer(selected!!,session){selected=null};section=="Ao vivo"->ChannelList(channels){selected=it};else->Text(if(channels.isEmpty())"Catálogo ainda não sincronizado." else "${channels.size} itens liberados pelo administrador.")}}}
 }}
@Composable private fun Login(loading:Boolean,error:String?,onLogin:(String,String)->Unit){var email by remember{mutableStateOf("")};var password by remember{mutableStateOf("")};Box(Modifier.fillMaxSize().padding(64.dp)){Card(Modifier.width(460.dp)){Column(Modifier.padding(32.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){Text("SuperIPTV",style=MaterialTheme.typography.displaySmall);Text("Entre com a conta liberada pelo administrador.");OutlinedTextField(email,{email=it},label={Text("E-mail")},modifier=Modifier.fillMaxWidth());OutlinedTextField(password,{password=it},label={Text("Senha")},modifier=Modifier.fillMaxWidth());Button(onClick={onLogin(email,password)},enabled=!loading,modifier=Modifier.fillMaxWidth()){Text(if(loading)"Entrando..." else "Entrar")};error?.let{Text(it,color=MaterialTheme.colorScheme.error)}}}}}
@Composable private fun ChannelList(channels:List<DesktopChannel>,onPlay:(DesktopChannel)->Unit){if(channels.isEmpty()){Text("Nenhum canal liberado para esta conta.");return};LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(channels,key={it.id}){c->Card(Modifier.fillMaxWidth().clickable{onPlay(c)}){Row(Modifier.padding(16.dp)){Column{Text(c.name,style=MaterialTheme.typography.titleMedium);Text(c.group)}}}}}}
@Composable private fun DesktopPlayer(channel:DesktopChannel,session:DesktopSession,onBack:()->Unit){val panel=remember{DesktopPlayerPanel{session.access}};DisposableEffect(panel){panel.play(channel.playbackUrl);onDispose{panel.stop();panel.release()}};Column(Modifier.fillMaxSize(),verticalArrangement=Arrangement.spacedBy(12.dp)){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Column{Text(channel.name,style=MaterialTheme.typography.headlineMedium);Text(channel.group)};TextButton(onClick=onBack){Text("Voltar")}};SwingPanel(factory={panel},modifier=Modifier.fillMaxSize())}}

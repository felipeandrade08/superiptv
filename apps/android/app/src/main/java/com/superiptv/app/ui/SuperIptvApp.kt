package com.superiptv.app.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.superiptv.app.ui.screens.*
import com.superiptv.app.ui.theme.SuperIptvTheme

@Composable fun SuperIptvApp(){
 SuperIptvTheme{
  val nav=rememberNavController()
  Scaffold(bottomBar={
   NavigationBar{
    AppDestination.entries.forEach{d->
     val current=nav.currentBackStackEntryAsState().value?.destination?.route
     NavigationBarItem(selected=current==d.route,onClick={nav.navigate(d.route){launchSingleTop=true;popUpTo(AppDestination.Home.route)}},icon={Icon(d.icon,d.label)},label={Text(d.label)})
    }
   }
  }){padding->
   NavHost(navController=nav,startDestination=AppDestination.Home.route,modifier=Modifier.padding(padding)){
    composable("home"){HomeScreen()}
    composable("live"){SectionScreen("TV ao vivo","Seus canais aparecerão aqui quando uma playlist for adicionada.")}
    composable("movies"){SectionScreen("Filmes","Catálogo organizado a partir das suas próprias fontes.")}
    composable("series"){SectionScreen("Séries","Temporadas e episódios organizados em uma experiência própria.")}
    composable("library"){SectionScreen("Minha lista","Favoritos, histórico e continuar assistindo ficarão reunidos aqui.")}
   }
  }
 }
}

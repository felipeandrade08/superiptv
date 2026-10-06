package com.superiptv.app.ui
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.superiptv.app.ui.screens.*
import com.superiptv.app.ui.theme.SuperIptvTheme
@Composable fun SuperIptvApp(){SuperIptvTheme{val nav=rememberNavController();Scaffold(bottomBar={NavigationBar{AppDestination.entries.forEach{d->val current=nav.currentBackStackEntryAsState().value?.destination?.route;NavigationBarItem(selected=current==d.route,onClick={nav.navigate(d.route){launchSingleTop=true;popUpTo(AppDestination.Home.route)}},icon={Icon(d.icon,d.label)},label={Text(d.label)})}}}){padding->NavHost(navController=nav,startDestination=AppDestination.Home.route,modifier=Modifier.padding(padding)){composable("home"){HomeScreen()};composable("live"){LiveScreen()};composable("movies"){SectionScreen("Filmes","A classificação de VOD será refinada na evolução do catálogo.")};composable("series"){SectionScreen("Séries","Séries terão navegação própria por temporadas e episódios.")};composable("library"){LibraryScreen()}}}}}

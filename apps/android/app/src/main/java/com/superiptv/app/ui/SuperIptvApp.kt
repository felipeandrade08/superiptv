package com.superiptv.app.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.superiptv.app.ui.screens.*
import com.superiptv.app.ui.theme.SuperIptvTheme
@Composable fun SuperIptvApp(auth:AuthViewModel=viewModel()){SuperIptvTheme{val state by auth.ui.collectAsStateWithLifecycle();when{state.checking->Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){CircularProgressIndicator()};!state.logged->LoginScreen(state,auth::login);else->AuthenticatedApp()}}}
@Composable private fun AuthenticatedApp(){val nav=rememberNavController();Scaffold(bottomBar={NavigationBar{AppDestination.entries.forEach{d->val current=nav.currentBackStackEntryAsState().value?.destination?.route;NavigationBarItem(selected=current==d.route,onClick={nav.navigate(d.route){launchSingleTop=true;popUpTo(AppDestination.Home.route)}},icon={Icon(d.icon,d.label)},label={Text(d.label)})}}}){padding->NavHost(navController=nav,startDestination=AppDestination.Home.route,modifier=Modifier.padding(padding)){composable("home"){HomeScreen(onPlay={id->nav.navigate("player/"+id)})};composable("live"){LiveScreen(onPlay={id->nav.navigate("player/"+id)})};composable("player/{channelId}"){entry->PlayerScreen(channelId=entry.arguments?.getString("channelId").orEmpty(),onBack={nav.popBackStack()})};composable("movies"){VodScreen("movie",onPlay={id->nav.navigate("player/"+id)})};composable("series"){VodScreen("series",onPlay={id->nav.navigate("player/"+id)})};composable("library"){LibraryScreen(onPlay={id->nav.navigate("player/"+id)})}}}}

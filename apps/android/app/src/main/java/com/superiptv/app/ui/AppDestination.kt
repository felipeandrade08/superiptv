package com.superiptv.app.ui
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(val route:String,val label:String,val icon:ImageVector){
 Home("home","Início",Icons.Rounded.Home),
 Live("live","TV",Icons.Rounded.LiveTv),
 Movies("movies","Filmes",Icons.Rounded.Movie),
 Series("series","Séries",Icons.Rounded.VideoLibrary),
 Library("library","Minha lista",Icons.Rounded.Bookmarks)
}

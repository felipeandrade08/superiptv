package com.superiptv.app.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SuperColors=darkColorScheme(
 primary=Color(0xFF8B7CFF),secondary=Color(0xFF54D6C7),background=Color(0xFF08090D),
 surface=Color(0xFF11131A),surfaceVariant=Color(0xFF1A1D27),onPrimary=Color.White,
 onBackground=Color(0xFFF5F4FA),onSurface=Color(0xFFF5F4FA)
)
@Composable fun SuperIptvTheme(content:@Composable()->Unit){MaterialTheme(colorScheme=SuperColors,typography=Typography(),content=content)}

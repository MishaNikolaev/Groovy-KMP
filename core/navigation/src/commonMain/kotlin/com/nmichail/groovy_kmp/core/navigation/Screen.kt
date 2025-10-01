package com.nmichail.groovy_kmp.core.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object FullPlayer : Screen("full_player")
    
    sealed class MainSection(route: String) : Screen(route) {
        data object Home : MainSection("home")
        data object Search : MainSection("search")
        data object Favourite : MainSection("favourite")
        data object Profile : MainSection("profile")
    }

    data class Lyrics(val trackId: String) : Screen("lyrics/$trackId")
    data class Artist(val artistName: String) : Screen("artist/$artistName")
    data class AllTracks(val artistName: String) : Screen("all_tracks/$artistName")
    data object AllArtists : Screen("all_artists")
}

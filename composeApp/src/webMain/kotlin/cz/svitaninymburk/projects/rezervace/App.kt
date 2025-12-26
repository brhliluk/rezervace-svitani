package cz.svitaninymburk.projects.rezervace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.svitaninymburk.projects.rezervace.mock.randomEventList
import cz.svitaninymburk.projects.rezervace.theming.Theme
import cz.svitaninymburk.projects.rezervace.ui.Dashboard
import cz.svitaninymburk.projects.rezervace.ui.Toolbar
import org.jetbrains.compose.resources.stringResource
import rezervace.composeapp.generated.resources.Res
import rezervace.composeapp.generated.resources.dashboard


@Composable
fun App() {
    Theme {
        Column(Modifier.fillMaxSize()) {
            Toolbar(stringResource(Res.string.dashboard), null, onNavigateHome = {}, onAccountClick = {})
            Dashboard(randomEventList)
        }
    }
}
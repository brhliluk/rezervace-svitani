package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.vectorResource
import rezervace.composeapp.generated.resources.Res
import rezervace.composeapp.generated.resources.Svitani_small

@Composable fun Toolbar(
    title: String,
) {
    MediumTopAppBar(
        title = { Text(title) },
        navigationIcon = { Icon(vectorResource(Res.drawable.Svitani_small), contentDescription = null) },
        actions = { TODO("hiding navigation + user") },
    )
}
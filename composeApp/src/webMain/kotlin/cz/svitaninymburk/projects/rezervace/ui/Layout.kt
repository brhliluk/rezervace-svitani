package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable fun Toolbar(
    title: String,
) {
    MediumTopAppBar(
        title = { Text(title) },
        navigationIcon = { TODO("AppLogo") },
        actions = { TODO("hiding navigation + user") },
        colors = TODO("AppColors"),
    )
}
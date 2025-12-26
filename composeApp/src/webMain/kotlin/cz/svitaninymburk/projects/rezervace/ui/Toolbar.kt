package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cz.svitaninymburk.projects.rezervace.user.User
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import rezervace.composeapp.generated.resources.Res
import rezervace.composeapp.generated.resources.Svitani_small
import rezervace.composeapp.generated.resources.log_in


@Composable fun Toolbar(
    title: String,
    user: User? = null,
    onNavigateHome: () -> Unit,
    onAccountClick: () -> Unit,
) {
    MediumTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton({ onNavigateHome() }) { Icon(vectorResource(Res.drawable.Svitani_small), contentDescription = null) }
        },
        actions = {
            IconButton({ onAccountClick() }) { Icon(Icons.Filled.AccountCircle, "Account") }
            user?.let { Text("${it.name} ${it.surname}") } ?: Text(stringResource(Res.string.log_in))
        },
    )
}

@Preview
@Composable private fun ToolbarPreview() = Toolbar("Preview", onNavigateHome = {}, onAccountClick = {})
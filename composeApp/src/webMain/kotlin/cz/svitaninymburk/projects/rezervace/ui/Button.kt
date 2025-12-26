package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable fun Button(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick, modifier, shape = CircleShape) {
        Text(text)
    }
}
package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import rezervace.composeapp.generated.resources.Res
import rezervace.composeapp.generated.resources.calendar_view
import rezervace.composeapp.generated.resources.list_view

@Composable fun DashboardModeSwitch(selectedMode: DashboardMode, modifier: Modifier = Modifier, onModeSelected: (DashboardMode) -> Unit) = Surface(modifier.padding(16.dp), shape = CircleShape) {
    Row(Modifier.width(400.dp), verticalAlignment = Alignment.CenterVertically) {
        Item(
            stringResource(Res.string.list_view),
            Icons.AutoMirrored.Filled.List,
            selectedMode == DashboardMode.LIST,
            Modifier.weight(1f),
        ) { onModeSelected(DashboardMode.LIST) }
        Item(
            stringResource(Res.string.calendar_view),
            Icons.Filled.CalendarMonth,
            selectedMode == DashboardMode.CALENDAR,
            Modifier.weight(1f),
        ) { onModeSelected(DashboardMode.CALENDAR) }
    }
}

@Composable private fun Item(text: String, icon: ImageVector, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) = Column(
    modifier
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            onClick = onClick,
        )
        .padding(16.dp)
    ,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Icon(
        icon,
        contentDescription = null,
        if (selected) Modifier.width(48.dp).background(color = MaterialTheme.colorScheme.inverseOnSurface, shape = CircleShape)
        else Modifier.alpha(.8f)
    )
    Text(text, if (!selected) Modifier.alpha(.8f) else Modifier)
}
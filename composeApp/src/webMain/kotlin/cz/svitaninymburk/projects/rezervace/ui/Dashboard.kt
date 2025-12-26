package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.svitaninymburk.projects.rezervace.event.EventInstance
import kotlinx.collections.immutable.ImmutableList

enum class DashboardMode {
    LIST, CALENDAR
}

@Composable fun Dashboard(events: ImmutableList<EventInstance>, modifier: Modifier = Modifier) {
    var mode by remember { mutableStateOf(DashboardMode.LIST) }

    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        DashboardModeSwitch(mode, onModeSelected = { mode = it })
        when (mode) {
            DashboardMode.LIST -> EventList(events, onEventClick = { /* TODO: open detail */ })
            DashboardMode.CALENDAR -> {}
        }
    }
}
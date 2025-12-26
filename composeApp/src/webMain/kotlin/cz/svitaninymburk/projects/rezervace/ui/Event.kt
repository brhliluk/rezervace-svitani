package cz.svitaninymburk.projects.rezervace.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.svitaninymburk.projects.rezervace.event.EventInstance
import cz.svitaninymburk.projects.rezervace.util.humanReadable
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import rezervace.composeapp.generated.resources.Res
import rezervace.composeapp.generated.resources.reserve


@Composable fun Event(event: EventInstance, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(modifier.fillMaxWidth(), border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleLarge)
            Text("${event.startDateTime.humanReadable} - ${event.endDateTime.humanReadable}", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(16.dp))
            Text(event.description)
            Spacer(Modifier.height(16.dp))
            Button(stringResource(Res.string.reserve), Modifier.align(Alignment.End), onClick)
        }
    }
}

@Composable fun EventList(events: ImmutableList<EventInstance>, modifier: Modifier = Modifier, onEventClick: (EventInstance) -> Unit) = LazyColumn(
    modifier.widthIn(400.dp, 1000.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
) {
    items(events, key = { it.id }) {
        Event(it) { onEventClick(it) }
    }
}

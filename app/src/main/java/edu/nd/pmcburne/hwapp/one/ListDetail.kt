package edu.nd.pmcburne.hwapp.one

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ListDetail(
    item: Game,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onItemClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ---- Left: Team names ----
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.awayTeam,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (item.homeWinner == false && item.status == "post")
                        FontWeight.Bold else FontWeight.Normal
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${item.homeTeam} (H)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (item.homeWinner == true)
                        FontWeight.Bold else FontWeight.Normal
                )
            }

            // ---- Middle: Scores or start time ----
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                when (item.status) {
                    "pre" -> {
                        Text(
                            text = formatStartTime(item.startTime),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    "in", "post" -> {
                        Text(
                            text = "${item.awayScore ?: "-"}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (item.homeWinner == false && item.status == "post")
                                FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${item.homeScore ?: "-"}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (item.homeWinner == true)
                                FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ---- Right: Status badge ----
            Column(horizontalAlignment = Alignment.End) {
                val (badgeText, badgeColor) = when (item.status) {
                    "post" -> "Final" to MaterialTheme.colorScheme.outline
                    "in"   -> "LIVE" to MaterialTheme.colorScheme.error
                    else   -> "Upcoming" to MaterialTheme.colorScheme.primary
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badgeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (item.status == "in" && item.clock != null) {
                    Spacer(Modifier.height(4.dp))
                    item.clock?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

private fun formatStartTime(isoDate: String?): String {
    if (isoDate == null) return "TBD"
    return try {
        val instant = java.time.Instant.parse(isoDate)
        val local = instant.atZone(java.time.ZoneId.systemDefault())
        local.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
    } catch (e: Exception) {
        isoDate
    }
}

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onItemClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ---- Top row: Team names (full width) + scores on right ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team names take all available horizontal space
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.awayTeam,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (item.homeWinner == false && item.status == "post")
                            FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.homeTeam,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (item.homeWinner == true)
                                FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "H",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Scores only appear for live / finished games
                if (item.status == "in" || item.status == "post") {
                    Spacer(Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.End) {
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

            // ---- Bottom row: time/clock on left, status badge on right ----
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val timeText = when (item.status) {
                    "pre" -> formatStartTime(item.startTime)
                    "in"  -> item.clock ?: ""
                    else  -> ""
                }
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item.status == "in")
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.outline
                )

                val (badgeText, badgeColor) = when (item.status) {
                    "post" -> "Final"    to MaterialTheme.colorScheme.outline
                    "in"   -> "LIVE"     to MaterialTheme.colorScheme.error
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
            }
        }
    }
}

private fun formatStartTime(isoDate: String?): String {
    if (isoDate == null) return "TBD"
    return try {
        val instant = java.time.Instant.parse(isoDate)
        val local = instant.atZone(java.time.ZoneId.systemDefault())
        local.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a z"))
    } catch (e: Exception) {
        isoDate
    }
}
package edu.nd.pmcburne.hwapp.one

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class Game(
    @PrimaryKey val id: String,           // ESPN game ID — used for upsert
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: String,                   // "pre", "in", or "post"
    val startTime: String?,               // shown for upcoming games
    val period: Int?,                     // current period if in progress
    val clock: String?,                   // time remaining if in progress
    val homeWinner: Boolean?,
    val gender: String,                   // "men" or "women"
    val date: String                      // "yyyy-MM-dd"
)

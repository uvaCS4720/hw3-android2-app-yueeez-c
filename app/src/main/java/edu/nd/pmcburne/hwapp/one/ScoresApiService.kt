package edu.nd.pmcburne.hwapp.one

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// ---- Retrofit Interface ----

interface ScoresApiService {
    @GET("scoreboard/basketball-{gender}/d1/{year}/{month}/{day}")
    suspend fun getScores(
        @Path("gender") gender: String,
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("day") day: String
    ):
            ScoreboardResponse
}

// ---- Retrofit Singleton ----

object RetrofitInstance {
    val api: ScoresApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://ncaa-api.henrygd.me/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ScoresApiService::class.java)
    }
}

// ---- ESPN JSON response models ----
data class ScoreboardResponse(
    val games: List<GameWrapper>?
)

data class GameWrapper(
    val game: ApiGame?
)

data class ApiGame(
    @SerializedName("gameID") val gameID: String,
    val home: ApiTeamData?,
    val away: ApiTeamData?,
    val gameState: String?,
    val startTime: String?,
    val startDate: String?,
    val currentPeriod: String?,
    val contestClock: String?
)

data class ApiTeamData(
    val score: String?,
    val names: ApiTeamNames?,
    val winner: Boolean?
)

data class ApiTeamNames(
    val short: String?,
    val full: String?
)


fun parseGames(response: ScoreboardResponse, gender: String, date: String): List<Game> {
    return response.games?.mapNotNull { wrapper ->
        val g = wrapper.game ?: return@mapNotNull null

        val status = when (g.gameState?.lowercase()) {
            "final" -> "post"
            "live"  -> "in"
            else    -> "pre"
        }
        android.util.Log.d("ScoresVM", "Game: ${g.gameID} state=${g.gameState} period=${g.currentPeriod} clock=${g.contestClock}")

        Game(
            id        = g.gameID,
            homeTeam  = g.home?.names?.short ?: "Unknown",
            awayTeam  = g.away?.names?.short ?: "Unknown",
            homeScore = g.home?.score?.toIntOrNull(),
            awayScore = g.away?.score?.toIntOrNull(),
            status    = status,
            startTime = if (status == "pre") "${g.startDate} ${g.startTime}" else null,
            period    = null,
            clock     = if (status == "in") "${g.currentPeriod} - ${g.contestClock}" else null,
            homeWinner = g.home?.winner,
            gender    = gender,
            date      = date
        )

    } ?: emptyList()
}
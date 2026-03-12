package edu.nd.pmcburne.hwapp.one

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE date = :date AND gender = :gender")
    fun getGames(date: String, gender: String): Flow<List<Game>>

    @Upsert
    suspend fun upsertGames(games: List<Game>)
}

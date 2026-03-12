package edu.nd.pmcburne.hwapp.one

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ScoresViewModel(
    private val gameDao: GameDao,
    private val context: Context
) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    private val _gender = MutableStateFlow("men")
    private val _isLoading = MutableStateFlow(false)

    val date: StateFlow<LocalDate> = _date.asStateFlow()
    val gender: StateFlow<String> = _gender.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val games: StateFlow<List<Game>> = combine(_date, _gender) { date, gender ->
        Pair(date.toString(), gender)
    }.flatMapLatest { (date, gender) ->
        gameDao.getGames(date, gender)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        fetchScores()
    }

    fun onDateChange(newDate: LocalDate) {
        _date.value = newDate
        fetchScores()
    }

    fun onGenderChange(newGender: String) {
        _gender.value = newGender
        fetchScores()
    }

    fun fetchScores() {
        if (!isOnline(context)) {
            android.util.Log.d("ScoresVM", "OFFLINE - skipping fetch")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val d = _date.value
                val year  = d.year.toString()
                val month = d.monthValue.toString().padStart(2, '0')
                val day   = d.dayOfMonth.toString().padStart(2, '0')

                android.util.Log.d("ScoresVM", "Fetching: gender=${_gender.value} $year/$month/$day")

                val response = RetrofitInstance.api.getScores(
                    gender = _gender.value,
                    year = year,
                    month = month,
                    day = day
                )

                android.util.Log.d("ScoresVM", "Games received: ${response.games?.size ?: 0}")
                val games = parseGames(response, _gender.value, d.toString())
                android.util.Log.d("ScoresVM", "Games parsed: ${games.size}")
                gameDao.upsertGames(games)

            } catch (e: Exception) {
                android.util.Log.e("ScoresVM", "Error fetching scores", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class ScoresViewModelFactory(
    private val gameDao: GameDao,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ScoresViewModel(gameDao, context) as T
    }
}

package edu.nd.pmcburne.hwapp.one
// updated

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ScoresViewModel by viewModels {
        ScoresViewModelFactory(
            gameDao = AppDatabase.getInstance(applicationContext).gameDao(),
            context = applicationContext
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HWStarterRepoTheme {
                    Gamescreen(viewModel = viewModel)
            }
        }
    }
}
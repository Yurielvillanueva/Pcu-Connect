package edu.pcu.connect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.pcu.connect.data.AppViewModel
import edu.pcu.connect.nav.RootNavHost
import edu.pcu.connect.theme.PCUConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PCUConnectTheme {
                val appViewModel: AppViewModel = viewModel()
                RootNavHost(viewModel = appViewModel)
            }
        }
    }
}

package dev.jamescullimore.hophub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.jamescullimore.hophub.ui.HopHubApp
import dev.jamescullimore.hophub.ui.theme.HopHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HopHubTheme {
                HopHubApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HopHubTheme {
        HopHubApp()
    }
}
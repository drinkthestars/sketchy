package com.goofy.goober.sketchy.scaffolding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.goofy.goober.style.MicPermContainer
import com.goofy.goober.style.Sizing
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

sealed interface Screen {
    val title: String
    val description: String?
}

data class NestedNavScreen(
    override val title: String,
    override val description: String? = null,
    val nestedGraph: NavGraphBuilder.(onNavigate: (Screen) -> Unit) -> Unit
) : Screen

data class DestinationScreen(
    override val title: String,
    override val description: String? = null,
    val content: @Composable () -> Unit
) : Screen

fun NavGraphBuilder.nestedContent(
    onNavigate: (Screen) -> Unit,
    screens: List<DestinationScreen>,
    home: String
) {
    composable(home) {
        MicPermContainer {
            List(
                screens = screens,
                onClick = { screen ->
                    onNavigate(screen)
                }
            )
        }
    }
    screens.forEach { screen ->
        composable(screen.title) { screen.content() }
    }
}

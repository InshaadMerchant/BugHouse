package com.example.bughouse

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Set theme before super.onCreate
        setTheme(R.style.Theme_BugHouse)

        // Make sure we have transparent background
        window.setBackgroundDrawableResource(android.R.color.transparent)

        // Set additional window properties to ensure splash screen is disabled
        window.decorView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })

        setContent {
            BugHouseTheme {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current

    // Animation states
    var animationStarted by remember { mutableStateOf(false) }
    var navigateToLogin by remember { mutableStateOf(false) }

    // Logo animation (start at center, move up)
    val logoOffsetY by animateDpAsState(
        targetValue = if (animationStarted) (-135).dp else 0.dp,
        animationSpec = tween(
            durationMillis = 700,
            easing = EaseOutQuad
        ),
        label = "logo animation"
    )

    // Start animations with a short delay
    LaunchedEffect(key1 = true) {
        delay(300)
        animationStarted = true

        // Wait for animation to complete, then navigate
        delay(1000)
        navigateToLogin = true
    }

    // Navigation effect
    LaunchedEffect(navigateToLogin) {
        if (navigateToLogin) {
            // Navigate to LoginActivity
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            context.startActivity(intent)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0795DD)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = logoOffsetY)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "BugHouse",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
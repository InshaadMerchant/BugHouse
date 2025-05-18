package com.example.bughouse

import ApiClient
import android.app.Activity
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.example.bughouse.DTO.UserInfo

import com.example.bughouse.ui.theme.BugHouseTheme
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set window background color to prevent white flash
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.decorView.setBackgroundColor(resources.getColor(android.R.color.transparent, theme))

        // Ensure theme is set
        setTheme(R.style.Theme_BugHouse)

        // Handle back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Either do nothing or exit app
                finishAffinity()
            }
        })

        setContent {
            BugHouseTheme {
                LoginScreen()
            }
        }
    }
    suspend fun fetchAcctDetails(authObj: SingleAccountModeFragment, context: Context){
        Log.d("LOGIN", authObj.accountDetails!!.toString())
        val call = ApiClient.apiService.postObj(authObj.accountDetails!!)
        call.enqueue(object : Callback<UserInfo> {
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if (response.isSuccessful) {
                    val info = response.body();
                    if (info != null) {
                        // Save user ID to SharedPreferences
                        val sharedPrefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPrefs.edit()) {
                            putString("userId", info.id)
                            putString("userRole", info.role)
                            putString("fullName", info.username)
                            putString("email", info.emailAddress)
                            apply()
                        }

                        if(info.role.toString() == "student"){
                            val intent = Intent(context, DashboardActivity::class.java)
                            context.startActivity(intent)
                        }
                        else if(info.role.toString() == "admin"){
                            val intent = Intent(context, AdminDashboardActivity::class.java)
                            context.startActivity(intent)
                        }
                        else if(info.role.toString() == "tutor"){
                            val intent = Intent(context, TutorDashboardActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                    Log.d("APISERV", response.body().toString())
                } else {
                    Log.d("APISERV", response.toString())
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                Log.d("APISERV FAILURE", t.toString())
            }
        })
    }
    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        val scopeRoutine = CoroutineScope(Dispatchers.Main)
        val activity = context as Activity
        val authObject = SingleAccountModeFragment(context = context)

        // Animation state (only for button)
        var buttonAnimationStarted by remember { mutableStateOf(false) }

        // Button animation (starts below screen)
        val buttonOffsetY by animateDpAsState(
            targetValue = if (buttonAnimationStarted) 0.dp else 200.dp,
            animationSpec = tween(
                durationMillis = 800,
                easing = EaseOutQuad
            ),
            label = "button animation"
        )

        // Start button animation immediately
        LaunchedEffect(key1 = true) {
            delay(200) // Small delay for smoother transition
            buttonAnimationStarted = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0795DD)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1.3f))

                // App Logo - fixed position without animation
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "BugHouse Logo",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(bottom = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(2.5f))

                // Microsoft Login Button with animation
                Box(
                    modifier = Modifier
                        .offset(y = buttonOffsetY)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Login with Microsoft", Toast.LENGTH_SHORT).show()
                            // Navigate to Dashboard
                            scopeRoutine.launch {
                                authObject.signIn(activity = activity)
                                authObject.callGraphApiInteractive(activity = activity)
                                var isFetched = false
                                while(!isFetched) {
                                    if (authObject.accountDetails == null) {
                                        delay(1000)
                                    } else {
                                        isFetched = true
                                        fetchAcctDetails(authObject, context)
                                    }
                                }

                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .padding(bottom = 50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        // Microsoft Logo Icon
                        Image(
                            painter = painterResource(id = R.drawable.microsoft_logo),
                            contentDescription = "Microsoft Logo",
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )

                        // Button Text
                        Text(
                            text = "Login with Microsoft",
                            color = Color.Black
                        )
                    }
                }
            }
        }

    }
}




package com.example.bughouse.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bughouse.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TutorNavigationDrawer(
    drawerState: DrawerState,
    scope: CoroutineScope,
    currentScreen: String,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val authobj = SingleAccountModeFragment(context)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
                    .background(Color.White)
                // Remove the vertical padding from here
            ) {
                // Profile Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0795DD))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Profile Circle with TD initials
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TD",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tutor User",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "tutor@bughouse.edu",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                // Remove the spacer that was here

                // Navigation Items section with padding
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                ) {
                    NavigationDrawerItem(
                        icon = Icons.Default.Home,
                        label = "Tutor Dashboard",
                        selected = currentScreen == "Tutor Dashboard",
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (currentScreen != "Tutor Dashboard") {
                                    context.startActivity(Intent(context, TutorDashboardActivity::class.java))
                                }
                            }
                        }
                    )

                    NavigationDrawerItem(
                        icon = Icons.Default.Person,
                        label = "Profile",
                        selected = currentScreen == "Profile",
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (currentScreen != "Profile") {
                                    context.startActivity(Intent(context, TutorUpdateProfileActivity::class.java))
                                }
                            }
                        }
                    )

                    NavigationDrawerItem(
                        icon = Icons.Default.DateRange,
                        label = "Upcoming Appointments",
                        selected = currentScreen == "Upcoming Appointments",
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (currentScreen != "Upcoming Appointments") {
                                    context.startActivity(Intent(context, TutorManageAppointmentsActivity::class.java))
                                }
                            }
                        }
                    )


                    NavigationDrawerItem(
                        icon = Icons.Default.Email,
                        label = "Contact Us",
                        selected = currentScreen == "Contact Us",
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (currentScreen != "Contact Us") {
                                    context.startActivity(Intent(context, TutorContactUsActivity::class.java))
                                }
                            }
                        }
                    )

                    NavigationDrawerItem(
                        icon = Icons.Default.Info,
                        label = "About",
                        selected = currentScreen == "About",
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                if (currentScreen != "About") {
                                    context.startActivity(Intent(context, TutorAboutActivity::class.java))
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }

                // Separate logout section at the bottom with padding
                NavigationDrawerItem(
                    icon = Icons.Default.Logout,
                    label = "Logout",
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            val intent = Intent(context, LoginActivity::class.java)
                            authobj.signOut()
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    }
                )

                // Add bottom padding for system navigation
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) {
        content()
    }
}
@Composable
private fun NavigationDrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF0795DD) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(32.dp))
        Text(
            text = label,
            color = if (selected) Color(0xFF0795DD) else Color.Black,
            fontSize = 16.sp
        )
    }
} 
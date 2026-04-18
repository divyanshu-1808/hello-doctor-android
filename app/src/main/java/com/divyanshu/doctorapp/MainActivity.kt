package com.divyanshu.doctorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.divyanshu.doctorapp.ui.theme.DoctorAppointmentAppTheme
import com.divyanshu.doctorapp.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Always start fresh at login (show login + register)
        TokenManager.token = ""
        TokenManager.userId = 0
        TokenManager.userRole = ""

        setContent {
            var isDarkMode by androidx.compose.runtime.remember { mutableStateOf(false) }
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            
            // Only initialize once based on system theme
            androidx.compose.runtime.LaunchedEffect(Unit) {
                isDarkMode = systemTheme
            }

            DoctorAppointmentAppTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // ── Auth ────────────────────────────────────────────
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                isDarkMode = isDarkMode,
                                onThemeToggle = { isDarkMode = it }
                            )
                        }
                        composable("register") {
                            RegisterScreen(navController = navController)
                        }
                        composable("admin_login") {
                            AdminLoginScreen(navController = navController)
                        }

                        // ── Patient / Doctor screens ─────────────────────────
                        composable(
                            route = "doctor_list?specialization={specialization}",
                            arguments = listOf(navArgument("specialization") {
                                type = NavType.StringType; nullable = true; defaultValue = null
                            })
                        ) { backStackEntry ->
                            DoctorListScreen(
                                navController = navController,
                                initialSpecialization = backStackEntry.arguments?.getString("specialization")
                            )
                        }

                        composable(
                            route = "doctor_info/{doctor_id}",
                            arguments = listOf(navArgument("doctor_id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            DoctorInfoScreen(
                                navController = navController,
                                doctorId = backStackEntry.arguments?.getString("doctor_id")
                            )
                        }

                        composable(
                            route = "availability/{doctor_id}",
                            arguments = listOf(navArgument("doctor_id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            AvailabilityScreen(
                                navController = navController,
                                doctorId = backStackEntry.arguments?.getString("doctor_id")
                            )
                        }

                        composable("symptoms") {
                            SymptomScreen(navController = navController)
                        }

                        composable("patient_appointments") {
                            PatientAppointmentsScreen(navController = navController)
                        }

                        composable("patient_profile") {
                            PatientProfileScreen(navController = navController)
                        }

                        composable("doctor_dashboard") {
                            DoctorDashboardScreen(navController = navController)
                        }

                        // ── Admin ────────────────────────────────────────────
                        composable("admin_panel") {
                            AdminPanelScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    DoctorAppointmentAppTheme {
        LoginScreen(
            navController = rememberNavController(),
            isDarkMode = false,
            onThemeToggle = {}
        )
    }
}

package com.divyanshu.doctorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.divyanshu.doctorapp.ui.theme.DoctorAppointmentAppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.divyanshu.doctorapp.network.RetrofitInstance
import android.widget.Toast
import android.content.Context.MODE_PRIVATE
import kotlinx.coroutines.withContext
import androidx.navigation.compose.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.divyanshu.doctorapp.ui.DoctorListScreen
import com.divyanshu.doctorapp.ui.LoginScreen
import com.divyanshu.doctorapp.ui.AvailabilityScreen
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentAppTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        composable("login") {
                            LoginScreen(navController = navController)
                        }

                        composable("doctor_list") {
                            DoctorListScreen(navController)
                        }
                        composable("availability/{doctor_id}") { backStackEntry ->

                            val doctorId = backStackEntry.arguments?.getString("doctor_id")

                            AvailabilityScreen(navController, doctorId)
                        }

                    }
                }
            }}
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DoctorAppointmentAppTheme {
        Greeting("Android")
    }
}

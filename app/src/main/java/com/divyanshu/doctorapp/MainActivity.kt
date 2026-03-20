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
import com.divyanshu.doctorapp.network.LoginRequest
import com.divyanshu.doctorapp.network.RetrofitInstance
import android.widget.Toast
import android.content.Context.MODE_PRIVATE
import kotlinx.coroutines.withContext
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointmentAppTheme {

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    LoginScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                }

            }
        }
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

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("hello Doctor")

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {

            // API Call
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.login(
                        LoginRequest(email, password)
                    )

                    if (response.isSuccessful) {

                        val token = response.body()?.access_token

                        // Save token
                        val sharedPref = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
                        sharedPref.edit().putString("token", token).apply()

                        // Show success message on UI thread
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        Log.d("API", "Error: ${response.errorBody()?.string()}")
                    }

                } catch (e: Exception) {
                    Log.d("API", "Exception: ${e.message}")
                }
            }

        }) {
            Text("Login")
        }
    }
}
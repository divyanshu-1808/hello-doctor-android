package com.divyanshu.doctorapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.divyanshu.doctorapp.network.repository.DoctorRepository
import com.divyanshu.doctorapp.network.Doctor
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun DoctorListScreen(navController: NavController) {

    val repository = DoctorRepository()

    var doctorList by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    LaunchedEffect(Unit) {
        try {
            val response = repository.getDoctors()

            if (response.isSuccessful) {
                doctorList = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Available Doctors",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(doctorList) { doctor ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = doctor.specialization,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

            }
        }

    }
}
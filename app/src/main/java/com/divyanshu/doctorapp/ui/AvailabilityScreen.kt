package com.divyanshu.doctorapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.divyanshu.doctorapp.network.repository.AvailabilityRepository
import com.divyanshu.doctorapp.network.Availability
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.divyanshu.doctorapp.network.repository.AppointmentRepository
import com.divyanshu.doctorapp.network.AppointmentRequest
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
@Composable

fun AvailabilityScreen(
    navController: NavController,
    doctorId: String?
) {
    val appointmentRepository = AppointmentRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val repository = AvailabilityRepository()

    var availabilityList by remember { mutableStateOf<List<Availability>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            doctorId?.let {
                val response = repository.getAvailability(it)

                if (response.isSuccessful) {
                    availabilityList = response.body() ?: emptyList()
                }
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
            text = "Doctor Availability",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Doctor ID: $doctorId")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(availabilityList) { slot ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(
                            text = slot.date,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = slot.time_slot,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {

                                scope.launch {

                                    try {

                                        val request = AppointmentRequest(
                                            patient_id = 1, // temporary (we'll fix later)
                                            doctor_id = doctorId?.toInt() ?: 0,
                                            date = slot.date,
                                            time_slot = slot.time_slot
                                        )

                                        val response = appointmentRepository.bookAppointment(request)

                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Appointment Booked", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Booking Failed", Toast.LENGTH_SHORT).show()
                                        }

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        ) {
                            Text("Book Appointment")
                        }
                    }
                }
            }
        }

    }
}
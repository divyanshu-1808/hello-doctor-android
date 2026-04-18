package com.divyanshu.doctorapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.divyanshu.doctorapp.TokenManager
import com.divyanshu.doctorapp.network.Availability
import com.divyanshu.doctorapp.network.AppointmentRequest
import com.divyanshu.doctorapp.network.repository.AvailabilityRepository
import com.divyanshu.doctorapp.network.repository.AppointmentRepository
import kotlinx.coroutines.launch

@Composable
fun AvailabilityScreen(
    navController: NavController,
    doctorId: String?
) {
    val appointmentRepository = AppointmentRepository()
    val availabilityRepository = AvailabilityRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var availabilityList by remember { mutableStateOf<List<Availability>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var bookingSlotId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        try {
            doctorId?.let {
                val response = availabilityRepository.getAvailability(it)
                if (response.isSuccessful) {
                    availabilityList = response.body() ?: emptyList()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Available Slots",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Select a slot to book your appointment",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (availabilityList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No slots available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(availabilityList) { slot ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = slot.date,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = slot.time_slot,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                bookingSlotId = slot.id
                                scope.launch {
                                    try {
                                        val patientId = TokenManager.userId.takeIf { it > 0 } ?: 1

                                        val request = AppointmentRequest(
                                            patient_id = patientId,
                                            doctor_id = doctorId?.toInt() ?: 0,
                                            date = slot.date,
                                            time_slot = slot.time_slot
                                        )

                                        val response = appointmentRepository.bookAppointment(request)

                                        bookingSlotId = null

                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "✅ Appointment Booked Successfully!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("patient_appointments") {
                                                popUpTo("doctor_list")
                                            }
                                        } else {
                                            val err = response.errorBody()?.string() ?: "Booking failed"
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }

                                    } catch (e: Exception) {
                                        bookingSlotId = null
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = bookingSlotId == null
                        ) {
                            if (bookingSlotId == slot.id) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Book This Slot")
                        }
                    }
                }
            }
        }
    }
}
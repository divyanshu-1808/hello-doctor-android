package com.divyanshu.doctorapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
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
import com.divyanshu.doctorapp.network.AppointmentItem
import com.divyanshu.doctorapp.network.repository.AppointmentRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(navController: NavController) {

    val repository = AppointmentRepository()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var appointments by remember { mutableStateOf<List<AppointmentItem>>(emptyList()) }
    var availabilities by remember { mutableStateOf<List<com.divyanshu.doctorapp.network.Availability>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }



    fun fetchData() {
        isLoading = true
        scope.launch {
            try {
                val apptResponse = repository.getDoctorAppointments(TokenManager.userId)
                if (apptResponse.isSuccessful) appointments = apptResponse.body() ?: emptyList()

                val availResponse = com.divyanshu.doctorapp.network.RetrofitInstance.api.getAvailability(TokenManager.userId.toString())
                if (availResponse.isSuccessful) availabilities = availResponse.body() ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchData()
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newDate by remember { mutableStateOf("") }
    var newTime by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Availability")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Doctor Dashboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Manage your appointments",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
                IconButton(onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    TokenManager.token = ""
                    TokenManager.userId = 0
                    TokenManager.userRole = ""
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (appointments.isEmpty() && availabilities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No appointments or availability slots",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val unbookedAvails = availabilities.filter { avail ->
                    appointments.none { it.date == avail.date && it.time_slot == avail.time_slot }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(appointments) { appointment ->

                        val statusColor = when (appointment.status) {
                            "booked" -> MaterialTheme.colorScheme.primary
                            "completed" -> MaterialTheme.colorScheme.secondary
                            "cancelled" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Patient ID: ${appointment.patient_id}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Surface(
                                        color = statusColor.copy(alpha = 0.15f),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = appointment.status.uppercase(),
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Date: ${appointment.date}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Time: ${appointment.time_slot}", color = MaterialTheme.colorScheme.onSurfaceVariant)

                                if (appointment.status == "booked") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val resp = repository.updateStatus(appointment.id, "completed")
                                                        if (resp.isSuccessful) {
                                                            Toast.makeText(context, "Marked as completed", Toast.LENGTH_SHORT).show()
                                                            appointments = appointments.map {
                                                                if (it.id == appointment.id) it.copy(status = "completed") else it
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Complete", fontSize = 13.sp)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val resp = repository.cancelAppointment(appointment.id)
                                                        if (resp.isSuccessful) {
                                                            Toast.makeText(context, "Appointment cancelled", Toast.LENGTH_SHORT).show()
                                                            appointments = appointments.map {
                                                                if (it.id == appointment.id) it.copy(status = "cancelled") else it
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Cancel", fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(unbookedAvails) { availability ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Available Slot",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = "OPEN",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Date: ${availability.date}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Time: ${availability.time_slot}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val resp = com.divyanshu.doctorapp.network.RetrofitInstance.api.deleteAvailability(availability.id)
                                                    if (resp.isSuccessful) {
                                                        Toast.makeText(context, "Slot completed/closed", Toast.LENGTH_SHORT).show()
                                                        fetchData()
                                                    } else {
                                                        Toast.makeText(context, "Failed to close slot", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Complete", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState()
        val timePickerState = rememberTimePickerState(is24Hour = false)

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                            newDate = formatter.format(java.util.Date(millis))
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { Text("Select Start Time") },
                text = {
                    TimePicker(state = timePickerState)
                },
                confirmButton = {
                    TextButton(onClick = {
                        val cal = java.util.Calendar.getInstance().apply {
                            set(java.util.Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(java.util.Calendar.MINUTE, timePickerState.minute)
                        }
                        val format = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
                        val startTime = format.format(cal.time)
                        cal.add(java.util.Calendar.HOUR_OF_DAY, 1)
                        val endTime = format.format(cal.time)
                        newTime = "$startTime - $endTime"
                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                }
            )
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Availability") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (newDate.isBlank()) "Select Date" else "Date: $newDate")
                        }
                        
                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (newTime.isBlank()) "Select Start Time" else "Time: $newTime")
                        }
                        Text(
                            text = "Note: A 1-hour slot is automatically generated from the start time.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newDate.isNotBlank() && newTime.isNotBlank()) {
                                isAdding = true
                                scope.launch {
                                    try {
                                        val req = com.divyanshu.doctorapp.network.AvailabilityRequest(
                                            doctor_id = TokenManager.userId,
                                            date = newDate,
                                            time_slot = newTime
                                        )
                                        val response = com.divyanshu.doctorapp.network.RetrofitInstance.api.createAvailability(req)
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Availability added", Toast.LENGTH_SHORT).show()
                                            showAddDialog = false
                                            newDate = ""
                                            newTime = ""
                                            fetchData() // Refresh list
                                        } else {
                                            Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                    isAdding = false
                                }
                            }
                        },
                        enabled = !isAdding
                    ) {
                        if (isAdding) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        else Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
}

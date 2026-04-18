package com.divyanshu.doctorapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.divyanshu.doctorapp.network.DoctorDetail
import com.divyanshu.doctorapp.network.RetrofitInstance
import kotlinx.coroutines.launch

@Composable
fun DoctorInfoScreen(navController: NavController, doctorId: String?) {

    val scope = rememberCoroutineScope()
    var doctor by remember { mutableStateOf<DoctorDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(doctorId) {
        scope.launch {
            try {
                val id = doctorId?.toIntOrNull()
                if (id != null) {
                    val response = RetrofitInstance.api.getDoctorDetail(id)
                    if (response.isSuccessful) {
                        doctor = response.body()
                    } else {
                        error = "Could not load doctor info"
                    }
                }
            } catch (e: Exception) {
                error = "Network error: ${e.message}"
            }
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Surface(color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Doctor Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary)
                Text("View info before booking", fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (error.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error, color = MaterialTheme.colorScheme.error)
            }
            return@Column
        }

        doctor?.let { doc ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = doc.name.first().toString(),
                            fontSize = 42.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(doc.name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(doc.specialization, fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(24.dp))

                // Bio
                if (!doc.bio.isNullOrBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("About", fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(doc.bio, fontSize = 15.sp, lineHeight = 22.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Info Cards
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoCard(modifier = Modifier.weight(1f), label = "Specialization", value = doc.specialization)
                    InfoCard(modifier = Modifier.weight(1f), label = "Status", value = if (doc.is_approved) "Verified ✓" else "Pending")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("availability/$doctorId") },
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Text("Book Appointment", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Doctors")
                }
            }
        }
    }
}

@Composable
private fun InfoCard(modifier: Modifier = Modifier, label: String, value: String) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

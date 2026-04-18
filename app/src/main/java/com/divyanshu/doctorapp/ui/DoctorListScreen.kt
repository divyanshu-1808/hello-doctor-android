package com.divyanshu.doctorapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.divyanshu.doctorapp.network.Doctor
import com.divyanshu.doctorapp.network.repository.DoctorRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    navController: NavController,
    initialSpecialization: String? = null
) {
    val repository = DoctorRepository()

    var doctorList by remember { mutableStateOf<List<Doctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedSpecialization by remember { mutableStateOf(initialSpecialization ?: "") }
    var filterExpanded by remember { mutableStateOf(false) }

    val specializations = listOf(
        "", "General Physician", "Cardiologist", "Neurologist",
        "Dentist", "Ophthalmologist", "Dermatologist",
        "Orthopedic", "Gastroenterologist", "Psychiatrist"
    )

    // Fetch doctors whenever specialization filter changes
    LaunchedEffect(selectedSpecialization) {
        isLoading = true
        try {
            val spec = selectedSpecialization.ifEmpty { null }
            val response = repository.getDoctors(spec)
            if (response.isSuccessful) {
                doctorList = response.body() ?: emptyList()
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Available Doctors",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Find and book an appointment",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                }
                IconButton(onClick = { navController.navigate("patient_profile") }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "My Profile",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {

            // Specialization filter dropdown
            ExposedDropdownMenuBox(
                expanded = filterExpanded,
                onExpandedChange = { filterExpanded = !filterExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = if (selectedSpecialization.isEmpty()) "All Specializations" else selectedSpecialization,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter by Specialization") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = filterExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = filterExpanded,
                    onDismissRequest = { filterExpanded = false }
                ) {
                    specializations.forEach { spec ->
                        DropdownMenuItem(
                            text = { Text(if (spec.isEmpty()) "All Specializations" else spec) },
                            onClick = {
                                selectedSpecialization = spec
                                filterExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Symptom recommendation button
            OutlinedButton(
                onClick = { navController.navigate("symptoms") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔍  Find Doctor by Symptoms")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // My appointments button
            TextButton(
                onClick = { navController.navigate("patient_appointments") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View My Appointments")
            }
        }

        HorizontalDivider()

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (doctorList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No doctors found",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(doctorList) { doctor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("doctor_info/${doctor.id}") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = doctor.name.first().toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = doctor.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = doctor.specialization,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
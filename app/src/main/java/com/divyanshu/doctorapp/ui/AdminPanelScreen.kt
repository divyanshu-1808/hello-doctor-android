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
import com.divyanshu.doctorapp.network.DoctorDetail
import com.divyanshu.doctorapp.network.PatientAdminInfo
import com.divyanshu.doctorapp.network.PendingDoctor
import com.divyanshu.doctorapp.network.RetrofitInstance
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp

@Composable
fun AdminPanelScreen(navController: NavController) {
    val tabs = listOf("Pending Approvals", "All Doctors", "Patients")
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Admin Panel", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onError
                    )
                    Text(
                        "System management & oversight", fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                    )
                }
                IconButton(onClick = {
                    val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().clear().apply()
                    com.divyanshu.doctorapp.TokenManager.token = ""
                    com.divyanshu.doctorapp.TokenManager.userId = 0
                    com.divyanshu.doctorapp.TokenManager.userRole = ""
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> PendingApprovalsTab()
            1 -> AllDoctorsTab()
            2 -> PatientsTab()
        }
    }
}

// ── Tab 1: Pending Doctor Approvals ──────────────────────────────────────────

@Composable
fun PendingApprovalsTab() {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current                                      // ✅ Fixed
    var pendingList by remember { mutableStateOf<List<PendingDoctor>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getPendingDoctors()
            if (response.isSuccessful) {
                pendingList = response.body() ?: emptyList()
            } else {
                errorMsg = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMsg = "Network error: ${e.message}"
        }
        isLoading = false
    }

    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        errorMsg.isNotEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
        pendingList.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅", fontSize = 40.sp)
                Spacer(Modifier.height(8.dp))
                Text("No pending approvals", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pendingList, key = { it.id }) { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(doc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            doc.specialization,
                            color = MaterialTheme.colorScheme.primary, fontSize = 14.sp
                        )

                        if (!doc.bio.isNullOrBlank()) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Bio: ${doc.bio}", fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3
                            )
                        }

                        if (!doc.license_path.isNullOrBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "📄 License submitted", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val resp = RetrofitInstance.api.approveDoctor(doc.id)
                                            if (resp.isSuccessful) {
                                                pendingList = pendingList.filter { it.id != doc.id }
                                                Toast.makeText(ctx, "${doc.name} approved!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(ctx, "Failed to approve", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) { Text("Approve ✓", fontSize = 13.sp) }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val resp = RetrofitInstance.api.rejectDoctor(doc.id)
                                            if (resp.isSuccessful) {
                                                pendingList = pendingList.filter { it.id != doc.id }
                                                Toast.makeText(ctx, "${doc.name} rejected", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(ctx, "Failed to reject", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) { Text("Reject ✗", fontSize = 13.sp) }
                        }
                    }
                }
            }
        }
    }
}

// ── Tab 2: All Doctors ────────────────────────────────────────────────────────

@Composable
fun AllDoctorsTab() {
    var doctors by remember { mutableStateOf<List<DoctorDetail>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.adminGetAllDoctors()
            if (response.isSuccessful) {
                doctors = response.body() ?: emptyList()
            } else {
                errorMsg = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMsg = "Network error: ${e.message}"
        }
        isLoading = false
    }

    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        errorMsg.isNotEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
        doctors.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No doctors found", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(doctors) { doc ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (doc.is_approved) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    doc.name.first().toString(), fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                doc.specialization,
                                color = MaterialTheme.colorScheme.primary, fontSize = 13.sp
                            )
                            if (!doc.bio.isNullOrBlank()) {
                                Text(
                                    doc.bio, fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = if (doc.is_approved) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (doc.is_approved) "Approved" else "Pending",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = if (doc.is_approved) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Tab 3: Patients ───────────────────────────────────────────────────────────

@Composable
fun PatientsTab() {
    var patients by remember { mutableStateOf<List<PatientAdminInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.adminGetPatients()
            if (response.isSuccessful) {
                patients = response.body() ?: emptyList()
            } else {
                errorMsg = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMsg = "Network error: ${e.message}"
        }
        isLoading = false
    }

    when {
        isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        errorMsg.isNotEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
        }
        patients.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No patients registered yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(patients) { patient ->
                Card(modifier = Modifier.fillMaxWidth()) {
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
                                    patient.name.first().toString(), fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(patient.name, fontWeight = FontWeight.SemiBold)
                            Text(
                                patient.email, fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Patient ID: ${patient.id}", fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}

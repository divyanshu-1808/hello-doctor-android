package com.divyanshu.doctorapp.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.divyanshu.doctorapp.network.RegisterRequest
import com.divyanshu.doctorapp.network.RetrofitInstance
import com.divyanshu.doctorapp.network.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("patient") }
    var roleExpanded by remember { mutableStateOf("") }
    var isRoleDropOpen by remember { mutableStateOf(false) }

    // Doctor-specific fields
    var specialization by remember { mutableStateOf("") }
    var isSpecDropOpen by remember { mutableStateOf(false) }
    var bio by remember { mutableStateOf("") }
    var licenseUri by remember { mutableStateOf<Uri?>(null) }
    var licenseFileName by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val repository = AuthRepository()

    val roles = listOf("patient", "doctor")
    val specializations = listOf(
        "General Physician", "Cardiologist", "Neurologist", "Dentist",
        "Ophthalmologist", "Dermatologist", "Orthopedic",
        "Gastroenterologist", "Psychiatrist"
    )

    // File picker for license
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        licenseUri = uri
        licenseFileName = uri?.lastPathSegment ?: "file selected"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Text("Register to get started", fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = name, onValueChange = { name = it },
            label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = email, onValueChange = { email = it },
            label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = password, onValueChange = { password = it },
            label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), singleLine = true)

        Spacer(modifier = Modifier.height(12.dp))

        // Role Dropdown
        ExposedDropdownMenuBox(expanded = isRoleDropOpen,
            onExpandedChange = { isRoleDropOpen = !isRoleDropOpen },
            modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedRole.replaceFirstChar { it.uppercase() },
                onValueChange = {}, readOnly = true,
                label = { Text("Register As") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropOpen) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = isRoleDropOpen,
                onDismissRequest = { isRoleDropOpen = false }) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.replaceFirstChar { it.uppercase() }) },
                        onClick = { selectedRole = role; isRoleDropOpen = false }
                    )
                }
            }
        }

        // ── Doctor-only fields ─────────────────────────────────────────────
        if (selectedRole == "doctor") {

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Doctor Details", fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))

            // Specialization dropdown
            ExposedDropdownMenuBox(expanded = isSpecDropOpen,
                onExpandedChange = { isSpecDropOpen = !isSpecDropOpen },
                modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = specialization.ifEmpty { "Select Specialization" },
                    onValueChange = {}, readOnly = true,
                    label = { Text("Specialization") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSpecDropOpen) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = isSpecDropOpen,
                    onDismissRequest = { isSpecDropOpen = false }) {
                    specializations.forEach { spec ->
                        DropdownMenuItem(
                            text = { Text(spec) },
                            onClick = { specialization = spec; isSpecDropOpen = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bio
            OutlinedTextField(
                value = bio, onValueChange = { bio = it },
                label = { Text("Bio / About You") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5,
                placeholder = { Text("Briefly describe your experience and qualifications...") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // License upload
            OutlinedButton(
                onClick = { filePicker.launch("*/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (licenseFileName.isNotEmpty()) "📄 $licenseFileName"
                    else "📎 Upload Medical License",
                    fontSize = 14.sp
                )
            }
            if (licenseFileName.isNotEmpty()) {
                Text("License file selected ✓", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.Start))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "⏳ Your registration will be reviewed by an admin before you can login.",
                    modifier = Modifier.padding(10.dp), fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    name.isBlank() || email.isBlank() || password.isBlank() -> {
                        Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    selectedRole == "doctor" && specialization.isBlank() -> {
                        Toast.makeText(context, "Please select a specialization", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                }
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (selectedRole == "doctor") {
                            // Multipart for doctor
                            val namePart = name.toRequestBody("text/plain".toMediaType())
                            val emailPart = email.toRequestBody("text/plain".toMediaType())
                            val passwordPart = password.toRequestBody("text/plain".toMediaType())
                            val specPart = specialization.toRequestBody("text/plain".toMediaType())
                            val bioPart = bio.toRequestBody("text/plain".toMediaType())

                            var licensePart: MultipartBody.Part? = null
                            if (licenseUri != null) {
                                val inputStream = context.contentResolver.openInputStream(licenseUri!!)
                                val bytes = inputStream?.readBytes()
                                inputStream?.close()
                                if (bytes != null) {
                                    val mimeType = context.contentResolver.getType(licenseUri!!) ?: "application/octet-stream"
                                    val reqBody = bytes.toRequestBody(mimeType.toMediaType())
                                    licensePart = MultipartBody.Part.createFormData("license_file", licenseFileName, reqBody)
                                }
                            }

                            val response = RetrofitInstance.api.registerDoctor(
                                namePart, emailPart, passwordPart, specPart, bioPart, licensePart
                            )
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Registration submitted! Await admin approval.", Toast.LENGTH_LONG).show()
                                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                                } else {
                                    Toast.makeText(context, response.errorBody()?.string() ?: "Failed", Toast.LENGTH_LONG).show()
                                }
                            }

                        } else {
                            // JSON for patient
                            val response = repository.register(name, email, password, selectedRole)
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Registration Successful! Please login.", Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                                } else {
                                    Toast.makeText(context, response.errorBody()?.string() ?: "Failed", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(), enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary)
            else Text("Register", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Already have an account? ")
            Text("Login", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                })
        }
    }
}

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
import com.divyanshu.doctorapp.network.repository.RecommendRepository
import kotlinx.coroutines.launch

// Full list of selectable symptoms mapped to their display labels
val SYMPTOM_OPTIONS = listOf(
    "fever" to "Fever",
    "cough" to "Cough",
    "cold" to "Cold",
    "sore_throat" to "Sore Throat",
    "runny_nose" to "Runny Nose",
    "fatigue" to "Fatigue",
    "chest_pain" to "Chest Pain",
    "palpitations" to "Palpitations",
    "shortness_of_breath" to "Shortness of Breath",
    "high_blood_pressure" to "High Blood Pressure",
    "headache" to "Headache",
    "dizziness" to "Dizziness",
    "migraine" to "Migraine",
    "seizure" to "Seizure",
    "memory_loss" to "Memory Loss",
    "tooth_pain" to "Tooth Pain",
    "gum_bleeding" to "Gum Bleeding",
    "eye_pain" to "Eye Pain",
    "blurred_vision" to "Blurred Vision",
    "skin_rash" to "Skin Rash",
    "itching" to "Itching",
    "acne" to "Acne",
    "hair_loss" to "Hair Loss",
    "joint_pain" to "Joint Pain",
    "back_pain" to "Back Pain",
    "muscle_pain" to "Muscle Pain",
    "stomach_pain" to "Stomach Pain",
    "nausea" to "Nausea",
    "vomiting" to "Vomiting",
    "diarrhea" to "Diarrhea",
    "anxiety" to "Anxiety",
    "depression" to "Depression",
    "insomnia" to "Insomnia"
)

@Composable
fun SymptomScreen(navController: NavController) {

    val selectedSymptoms = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = RecommendRepository()

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Symptom Checker",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Select your symptoms to find the right specialist",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // Result Banner
        if (resultMessage.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = resultMessage,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Selection count
        if (selectedSymptoms.isNotEmpty()) {
            Text(
                text = "${selectedSymptoms.size} symptom(s) selected",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        // Symptom checkboxes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            items(SYMPTOM_OPTIONS) { (key, label) ->
                val checked = key in selectedSymptoms
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { isChecked ->
                            if (isChecked) selectedSymptoms.add(key)
                            else selectedSymptoms.remove(key)
                        }
                    )
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

        // Bottom buttons
        Column(modifier = Modifier.padding(16.dp)) {

            Button(
                onClick = {
                    if (selectedSymptoms.isEmpty()) {
                        Toast.makeText(context, "Please select at least one symptom", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    scope.launch {
                        try {
                            val response = repository.recommend(selectedSymptoms.toList())
                            isLoading = false
                            if (response.isSuccessful) {
                                val result = response.body()
                                if (result != null) {
                                    resultMessage = result.message
                                    // Navigate to doctor list filtered by recommendation
                                    navController.navigate("doctor_list?specialization=${result.specialization}")
                                }
                            } else {
                                Toast.makeText(context, "Could not get recommendation", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Find Doctor by Symptoms", fontSize = 15.sp)
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

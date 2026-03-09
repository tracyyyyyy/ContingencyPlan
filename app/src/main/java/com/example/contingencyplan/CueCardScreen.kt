package com.example.contingencyplan

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CueCardScreen(
    scenarioFile: String, // e.g. "sickperson.json"
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()

    // Load JSON schema from assets
    val schema = remember { loadSchema(context, scenarioFile) }
    val title = schema.getString("title")
    val fields = schema.getJSONArray("fields")

    // State map for all fields
    val fieldStates = remember {
        mutableStateMapOf<String, String>().apply {
            for (i in 0 until fields.length()) {
                this[fields.getString(i)] = ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Cue Card - $title", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // Generate input fields dynamically
        for (i in 0 until fields.length()) {
            val label = fields.getString(i)

            // Detect Yes/No fields by keywords
            if (isYesNoField(label)) {
                var selected by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = selected,
                        onCheckedChange = {
                            selected = it
                            fieldStates[label] = if (it) "Yes" else "No"
                        }
                    )
                }
            } else {
                OutlinedTextField(
                    value = fieldStates[label] ?: "",
                    onValueChange = { fieldStates[label] = it },
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            val data = fieldStates.toMutableMap()
            data["uploadedAt"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            data["type"] = title

            db.collection("cuecards")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "✅ Uploaded successfully", Toast.LENGTH_SHORT).show()
                    // Clear fields
                    for (key in fieldStates.keys) fieldStates[key] = ""
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "❌ Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }) {
            Text("Upload")
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

fun loadSchema(context: Context, filename: String): JSONObject {
    val inputStream = context.assets.open("cuecards/$filename")
    val reader = BufferedReader(InputStreamReader(inputStream))
    val content = reader.readText()
    reader.close()
    return JSONObject(content)
}

fun isYesNoField(label: String): Boolean {
    val yesNoKeywords = listOf("受傷", "煙", "火", "爆炸", "傷亡", "危險品", "水閥", "能否看見", "GPS")
    return yesNoKeywords.any { label.contains(it) }
}

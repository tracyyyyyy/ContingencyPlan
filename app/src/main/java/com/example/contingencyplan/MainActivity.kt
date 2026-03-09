package com.example.contingencyplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.contingencyplan.ui.theme.ContingencyPlanTheme
import com.google.gson.Gson
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        setContent {
            ContingencyPlanTheme {
                val scope = rememberCoroutineScope()

                var selectedScenario by remember { mutableStateOf<Scenario?>(null) }
                var selectedCueCard by remember { mutableStateOf<String?>(null) }

                var plan by remember { mutableStateOf<ContingencyPlan?>(null) }
                var loadError by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(false) }

                // Map cue card names to JSON filenames
                val cueCardFileMap = mapOf(
                    "Sick Person" to "sickperson.json",
                    "Police" to "police.json",
                    "FSD" to "fsd.json",
                    "LP on Track" to "lpontrack.json",
                    "Missing Person" to "missingperson.json"
                )

                when {
                    // Cue Card flow
                    selectedCueCard != null -> {
                        CueCardScreen(
                            scenarioFile = cueCardFileMap[selectedCueCard] ?: "sickperson.json",
                            onBack = { selectedCueCard = null }
                        )
                    }

                    // ==========================
                    // Main Menu
                    // ==========================
                    selectedScenario == null && selectedCueCard == null -> {
                        MainScreen(
                            onScenarioSelected = { scenario ->
                                selectedScenario = scenario
                                isLoading = true

                                scope.launch {
                                    try {
                                        val parsedPlan = withContext(Dispatchers.IO) {
                                            val inputStream = assets.open(scenario.fileName)
                                            val json = inputStream.bufferedReader().use { it.readText() }
                                            Gson().fromJson(json, ContingencyPlan::class.java)
                                        }
                                        plan = parsedPlan
                                        loadError = null
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        plan = null
                                        loadError = "Failed to load ${scenario.title}"
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            onCueCardSelected = { card ->
                                selectedCueCard = card
                            }
                        )
                    }

                    // ==========================
                    // Loading screen
                    // ==========================
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // ==========================
                    // Critical Steps Screen
                    // ==========================
                    plan != null -> {
                        CriticalStepsScreen(
                            plan!!,
                            onBack = {
                                selectedScenario = null
                                plan = null
                            }
                        )
                    }

                    // ==========================
                    // Error Screen
                    // ==========================
                    else -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("❌ $loadError")
                            Button(onClick = { selectedScenario = null }) {
                                Text("Back to Main Page")
                            }
                        }
                    }
                }
            }
        }
    }
}

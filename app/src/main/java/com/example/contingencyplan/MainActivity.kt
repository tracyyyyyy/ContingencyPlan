package com.example.contingencyplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
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
                var plan by remember { mutableStateOf<ContingencyPlan?>(null) }
                var loadError by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(false) }

                if (selectedScenario == null) {

                    MainScreen(onScenarioSelected = { scenario ->

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
                    })

                } else {

                    if (isLoading) {

                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

                    } else if (plan != null) {

                        CriticalStepsScreen(
                            plan!!,
                            onBack = { selectedScenario = null }
                        )

                    } else {

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
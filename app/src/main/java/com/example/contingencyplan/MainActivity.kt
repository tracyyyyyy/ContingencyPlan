package com.example.contingencyplan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.contingencyplan.ui.theme.ContingencyPlanTheme
import com.google.gson.Gson
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import com.google.firebase.firestore.DocumentSnapshot



// 資料模型
data class CriticalStep(val id: Int, val task: String, val responsible: List<String>)
data class ContingencyPlan(val plan: String, val critical_steps: List<CriticalStep>)
data class Scenario(val title: String, val fileName: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            ContingencyPlanTheme {
                var selectedScenario by remember { mutableStateOf<Scenario?>(null) }
                var plan by remember { mutableStateOf<ContingencyPlan?>(null) }
                var loadError by remember { mutableStateOf<String?>(null) }

                if (selectedScenario == null) {
                    MainScreen(onScenarioSelected = { scenario ->
                        selectedScenario = scenario
                        try {
                            val inputStream = assets.open(scenario.fileName)
                            val json = inputStream.bufferedReader().use { it.readText() }
                            plan = Gson().fromJson(json, ContingencyPlan::class.java)
                            loadError = null
                        } catch (e: Exception) {
                            e.printStackTrace()
                            plan = null
                            loadError = "Failed to load ${scenario.title}"
                        }
                    })
                } else {
                    if (plan != null) {
                        CriticalStepsScreen(plan!!, onBack = { selectedScenario = null })
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

@Composable
fun MainScreen(onScenarioSelected: (Scenario) -> Unit) {
    val scenarios = listOf(
        // A Emergency evacuation
        Scenario("A1 Station on Fire (TH)", "a1_station_on_fire.json"),
        Scenario("A2 Station on Fire (NTH)", "a2_station_on_fire.json"),
        Scenario("A3 Train on Fire at Platform", "a3_train_on_fire_platform.json"),
        Scenario("A4 Train on Fire Between Stations", "a4_train_on_fire_between.json"),
        Scenario("A5 CBRN", "a5_cbrn.json"),
        Scenario("A6 Bomb Threat", "a6_bomb_threat.json"),
        Scenario("A7 SCR Evacuation", "a7_scr_evacuation.json"),

        // B Non-emergency evacuation
        Scenario("B1 Train Service Suspension", "b1_train_service_suspension.json"),
        Scenario("B2 Train-to-Track Derailment", "b2_train_derailment.json"),
        Scenario("B3 Total Power Supply Failure", "b3_total_power_failure.json"),
        Scenario("B4 Partial Power Supply Failure", "b4_partial_power_failure.json"),

        // C Equipment failure & incident
        Scenario("C1 Person Under Train", "c1_person_under_train.json"),
        Scenario("C2 MTR Shuttle Bus Operation", "c2_shuttle_bus.json"),
        Scenario("C3 OCC Evacuation", "c3_occ_evacuation.json"),
        Scenario("C4 Point Failure", "c4_point_failure.json"),
        Scenario("C5 Bi-directional Operation", "c5_bi_directional.json"),

        // D Crowd management
        Scenario("D1 Integrated Crowd Management Plan A", "d1_crowd_plan_a.json"),
        Scenario("D2 Integrated Crowd Management Plan B", "d2_crowd_plan_b.json"),
        Scenario("D3 Station Crowd Control", "d3_station_crowd_control.json"),
        Scenario("D4 Incident Outside Station", "d4_incident_outside.json"),
        Scenario("D5 Flooding", "d5_flooding.json"),
        Scenario("D5A Disastrous Flooding", "d5a_disastrous_flooding.json"),
        Scenario("D6 Tropical Cyclone Signal No. 8", "d6_typhoon.json"),
        Scenario("D7 Protest", "d7_protest.json"),
        Scenario("D8 Handling of POE / Riot", "d8_poe_riot.json"),
        Scenario("D9 Station Security Management Plan", "d9_security.json"),
        Scenario("D10 Multiple Entrance Close", "d10_entrance_close.json")
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Scenario", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            // Group A
            item { Text("A Emergency evacuation", style = MaterialTheme.typography.titleMedium) }
            items(scenarios.filter { it.title.startsWith("A") }) { scenario ->
                Card(
                    onClick = { onScenarioSelected(scenario) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(scenario.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }

            // Group B
            item { Text("B Non-emergency evacuation", style = MaterialTheme.typography.titleMedium) }
            items(scenarios.filter { it.title.startsWith("B") }) { scenario ->
                Card(
                    onClick = { onScenarioSelected(scenario) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(scenario.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }

            // Group C
            item { Text("C Equipment failure & incident", style = MaterialTheme.typography.titleMedium) }
            items(scenarios.filter { it.title.startsWith("C") }) { scenario ->
                Card(
                    onClick = { onScenarioSelected(scenario) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(scenario.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }

            // Group D
            item { Text("D Crowd management", style = MaterialTheme.typography.titleMedium) }
            items(scenarios.filter { it.title.startsWith("D") }) { scenario ->
                Card(
                    onClick = { onScenarioSelected(scenario) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(scenario.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun CriticalStepsScreen(plan: ContingencyPlan, onBack: () -> Unit) {
    var completedSteps by remember { mutableStateOf(setOf<Int>()) }
    val tickRecords = remember { mutableStateMapOf<Int, Long>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Plan: ${plan.plan}", style = MaterialTheme.typography.titleLarge)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(plan.critical_steps) { step ->
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Checkbox(
                            checked = completedSteps.contains(step.id),
                            onCheckedChange = { checked ->
                                completedSteps = if (checked) {
                                    completedSteps + step.id
                                } else {
                                    completedSteps - step.id
                                }

                                if (checked) {
                                    val now = System.currentTimeMillis()
                                    tickRecords[step.id] = tickRecords[step.id]?.let { old ->
                                        minOf(old, now)
                                    } ?: now
                                }
                            }
                        )
                        Column {
                            Text(text = "${step.id}. ${step.task}")
                            Text(text = "Responsible: ${step.responsible.joinToString(" 或 ")}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        isUploading = true
                        scope.launch(Dispatchers.IO) {
                            uploadToFirebase(plan.plan, tickRecords,
                                onSuccess = {
                                    isUploading = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("✅ Upload success")
                                    }
                                },
                                onFailure = { e ->
                                    isUploading = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("❌ Upload failed: ${e.message}")
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("⬆ Upload Records")
                    }
                }

                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("⬅ Back to Menu")
                }
            }
        }
    }
}

// Firebase Upload Function
fun uploadToFirebase(
    planName: String,
    records: Map<Int, Long>,
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    try {
        val db = FirebaseFirestore.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // ✅ 將 Int key 轉成 String，並保留最舊 timestamp
        val formattedRecords = records.mapKeys { (key, _) -> key.toString() }
            .mapValues { (_, ts) -> format.format(Date(ts)) }

        // ✅ Document ID：日期 + 計劃名（空格轉底線 + 小寫）
        val todayId = "${dateOnlyFormat.format(Date())}_${planName.replace(" ", "_").lowercase()}"

        val data = mapOf(
            "plan" to planName,
            "records" to formattedRecords,
            "uploadedAt" to format.format(Date(System.currentTimeMillis()))
        )

        // ✅ 主 document + 子 collection → 保留所有 Upload 歷史
        db.collection("uploads")
            .document(todayId)
            .collection("records")
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }

    } catch (e: Exception) {
        onFailure(e)
    }
}

@Composable
fun MergedRecordsScreen(plan: ContingencyPlan, docs: List<DocumentSnapshot>) {
    val allChecked = remember {
        mutableSetOf<String>().apply {
            docs.forEach { doc ->
                val records = doc.get("records") as? Map<String, String> ?: emptyMap()
                addAll(records.keys)
            }
        }
    }

    val unchecked = plan.critical_steps.filter { step ->
        !allChecked.contains(step.id.toString())
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Merged Records for ${plan.plan}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))
        Text("✅ Checked Items:", style = MaterialTheme.typography.titleMedium)
        allChecked.forEach { id ->
            Text("Step $id")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("❌ Unchecked Items:", style = MaterialTheme.typography.titleMedium)
        unchecked.forEach { step ->
            Text("${step.id}. ${step.task}")
        }
    }
}


package com.example.contingencyplan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

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

                                completedSteps =
                                    if (checked) completedSteps + step.id
                                    else completedSteps - step.id

                                if (checked) {

                                    val now = System.currentTimeMillis()

                                    tickRecords[step.id] =
                                        tickRecords[step.id]?.let { old ->
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

                            uploadToFirebase(
                                plan.plan,
                                tickRecords,

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
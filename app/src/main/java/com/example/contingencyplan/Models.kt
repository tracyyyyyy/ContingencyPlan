package com.example.contingencyplan

data class CriticalStep(val id: Int, val task: String, val responsible: List<String>)
data class ContingencyPlan(val plan: String, val critical_steps: List<CriticalStep>)
data class Scenario(val title: String, val fileName: String)
data class CueCard(val title: String)
data class Incident(
    val type: String,
    val fields: Map<String, String>,
    val uploadedAt: String = java.text.SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        java.util.Locale.getDefault()
    ).format(java.util.Date())
)
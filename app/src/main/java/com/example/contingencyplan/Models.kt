package com.example.contingencyplan

data class CriticalStep(val id: Int, val task: String, val responsible: List<String>)
data class ContingencyPlan(val plan: String, val critical_steps: List<CriticalStep>)
data class Scenario(val title: String, val fileName: String)
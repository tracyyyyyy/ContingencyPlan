package com.example.contingencyplan

import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

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

        val formattedRecords = records.mapKeys { (key, _) -> key.toString() }
            .mapValues { (_, ts) -> format.format(Date(ts)) }

        val todayId = "${dateOnlyFormat.format(Date())}_${planName.replace(" ", "_").lowercase()}"

        val data = mapOf(
            "plan" to planName,
            "records" to formattedRecords,
            "uploadedAt" to format.format(Date(System.currentTimeMillis()))
        )

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
package com.example.fitnesstrackerapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val dailyGoalSteps = 10_000
    private var currentSteps = 0
    private var goalToastShown = false

    private lateinit var tvDate: TextView
    private lateinit var tvStepsValue: TextView
    private lateinit var progressSteps: ProgressBar
    private lateinit var tvPercent: TextView
    private lateinit var btnUpdateStats: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDate = findViewById(R.id.tvDate)
        tvStepsValue = findViewById(R.id.tvStepsValue)
        progressSteps = findViewById(R.id.progressSteps)
        tvPercent = findViewById(R.id.tvPercent)
        btnUpdateStats = findViewById(R.id.btnUpdateStats)

        setTodayDate()
        updateProgressUI()

        btnUpdateStats.setOnClickListener { showUpdateStepsDialog() }
    }

    private fun setTodayDate() {
        val formatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        tvDate.text = "Date: ${formatter.format(Date())}"
    }

    private fun showUpdateStepsDialog() {
        val input = EditText(this).apply {
            hint = "Enter steps (e.g., 4500)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        AlertDialog.Builder(this)
            .setTitle("Update Steps")
            .setMessage("Daily goal is $dailyGoalSteps steps.")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val steps = input.text.toString().trim().toIntOrNull()
                if (steps == null || steps < 0) {
                    Toast.makeText(this, "Please enter a valid step count.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                currentSteps = steps
                goalToastShown = false
                updateProgressUI()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateProgressUI() {
        tvStepsValue.text = currentSteps.toString()

        val percent = ((currentSteps.toDouble() / dailyGoalSteps) * 100).toInt().coerceIn(0, 100)
        progressSteps.progress = percent
        tvPercent.text = "$percent%"

        if (percent >= 100 && !goalToastShown) {
            goalToastShown = true
            Toast.makeText(this, "Goal achieved! Keep it up!", Toast.LENGTH_SHORT).show()
        }
    }
}
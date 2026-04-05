package com.example.gradereportapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

data class SubjectEntry(
    val subject: String,
    val obtained: Int,
    val total: Int,
    val grade: String,
    val gpaPoint: Double,
    val passed: Boolean
)

class MainActivity : AppCompatActivity() {

    private lateinit var tableGrades: TableLayout
    private lateinit var tvSummary: TextView
    private lateinit var tvGpaValue: TextView

    private lateinit var etSubjectName: EditText
    private lateinit var etObtained: EditText
    private lateinit var etTotal: EditText

    private val entries = mutableListOf<SubjectEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tableGrades = findViewById(R.id.tableGrades)
        tvSummary = findViewById(R.id.tvSummary)
        tvGpaValue = findViewById(R.id.tvGpaValue)

        etSubjectName = findViewById(R.id.etSubjectName)
        etObtained = findViewById(R.id.etObtained)
        etTotal = findViewById(R.id.etTotal)

        findViewById<Button>(R.id.btnAdd).setOnClickListener { onAddClicked() }
        findViewById<Button>(R.id.btnShare).setOnClickListener { shareReport() }

        // Fill the initial 6 rows with sample data (meets requirement)
        seedInitialSixRows()
        refreshSummaryAndGpa()
    }

    private fun seedInitialSixRows() {
        val sample = listOf(
            Triple("OOP", 85, 100),
            Triple("DSA", 73, 100),
            Triple("Database", 66, 100),
            Triple("Networks", 58, 100),
            Triple("AI", 92, 100),
            Triple("Mobile Dev", 35, 100) // fail example
        )

        for ((subject, obtained, total) in sample) {
            addEntry(subject, obtained, total)
        }
    }

    private fun onAddClicked() {
        val subject = etSubjectName.text.toString().trim()
        val obtainedStr = etObtained.text.toString().trim()
        val totalStr = etTotal.text.toString().trim()

        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(obtainedStr) || TextUtils.isEmpty(totalStr)) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val obtained = obtainedStr.toIntOrNull()
        val total = totalStr.toIntOrNull()

        if (obtained == null || total == null) {
            Toast.makeText(this, "Marks must be numbers.", Toast.LENGTH_SHORT).show()
            return
        }

        if (total <= 0) {
            Toast.makeText(this, "Total marks must be greater than 0.", Toast.LENGTH_SHORT).show()
            return
        }

        if (obtained < 0 || obtained > total) {
            Toast.makeText(this, "Obtained marks must be between 0 and Total.", Toast.LENGTH_SHORT).show()
            return
        }

        addEntry(subject, obtained, total)
        refreshSummaryAndGpa()

        etSubjectName.text?.clear()
        etObtained.text?.clear()
        etTotal.text?.clear()
        etSubjectName.requestFocus()
    }

    private fun addEntry(subject: String, obtained: Int, total: Int) {
        val percent = (obtained.toDouble() / total.toDouble()) * 100.0
        val grade = calcGrade(percent)
        val gpaPoint = gradeToPoint(grade)
        val passed = grade != "F"

        val entry = SubjectEntry(subject, obtained, total, grade, gpaPoint, passed)
        entries.add(entry)

        addRowToTable(entry)
    }

    private fun addRowToTable(entry: SubjectEntry) {
        // Alternate shading for readability (requirement)
        val index = entries.size // 1-based after add
        val baseBg = if (index % 2 == 0) Color.parseColor("#F5F5F5") else Color.WHITE

        // Highlight pass/fail row (requirement)
        val rowBg = if (entry.passed) {
            // light green overlay
            blend(baseBg, Color.parseColor("#C8E6C9"), 0.35f)
        } else {
            // light red overlay
            blend(baseBg, Color.parseColor("#FFCDD2"), 0.45f)
        }

        val row = TableRow(this).apply {
            setBackgroundColor(rowBg)
            setPadding(6, 6, 6, 6)
        }

        row.addView(cell(entry.subject, Gravity.START))
        row.addView(cell(entry.obtained.toString(), Gravity.CENTER))
        row.addView(cell(entry.total.toString(), Gravity.CENTER))
        row.addView(cell(entry.grade, Gravity.CENTER, bold = true))

        // Insert BEFORE the summary row (summary is last TableRow inside TableLayout)
        val summaryIndex = tableGrades.indexOfChild(findViewById(R.id.rowSummary))
        tableGrades.addView(row, summaryIndex)
    }

    private fun cell(text: String, gravity: Int, bold: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            setPadding(8, 10, 8, 10)
            setTextColor(Color.parseColor("#212121"))
            textSize = 14f
            this.gravity = gravity
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
    }

    private fun refreshSummaryAndGpa() {
        val totalSubjects = entries.size
        val passed = entries.count { it.passed }
        val failed = totalSubjects - passed

        tvSummary.text = "Total Subjects: $totalSubjects   Passed: $passed   Failed: $failed"

        val gpa = if (totalSubjects == 0) 0.0 else entries.sumOf { it.gpaPoint } / totalSubjects
        tvGpaValue.text = String.format(Locale.getDefault(), "%.2f", gpa)

        // Make GPA color a bit meaningful
        tvGpaValue.setTextColor(
            if (gpa >= 3.0) Color.parseColor("#1B5E20")
            else if (gpa >= 2.0) Color.parseColor("#E65100")
            else Color.parseColor("#B71C1C")
        )
    }

    private fun calcGrade(percent: Double): String {
        return when {
            percent >= 90.0 -> "A+"
            percent >= 80.0 -> "A"
            percent >= 70.0 -> "B+"
            percent >= 60.0 -> "B"
            percent >= 50.0 -> "C"
            percent >= 40.0 -> "D"
            else -> "F"
        }
    }

    private fun gradeToPoint(grade: String): Double {
        return when (grade) {
            "A+" -> 4.0
            "A" -> 3.7
            "B+" -> 3.3
            "B" -> 3.0
            "C" -> 2.0
            "D" -> 1.0
            else -> 0.0
        }
    }

    private fun shareReport() {
        val sb = StringBuilder()
        sb.append("Student Grade Report\n\n")
        sb.append("Subject | Obtained | Total | Grade\n")
        sb.append("---------------------------------\n")
        for (e in entries) {
            sb.append("${e.subject} | ${e.obtained} | ${e.total} | ${e.grade}\n")
        }
        val totalSubjects = entries.size
        val passed = entries.count { it.passed }
        val failed = totalSubjects - passed
        val gpa = if (totalSubjects == 0) 0.0 else entries.sumOf { it.gpaPoint } / totalSubjects

        sb.append("\nTotal Subjects: $totalSubjects  Passed: $passed  Failed: $failed\n")
        sb.append(String.format(Locale.getDefault(), "GPA: %.2f\n", gpa))

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Student Grade Report")
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    /**
     * Blend two colors. ratio=0 => base, ratio=1 => overlay.
     */
    private fun blend(base: Int, overlay: Int, ratio: Float): Int {
        val inv = 1f - ratio
        val r = (Color.red(base) * inv + Color.red(overlay) * ratio).toInt()
        val g = (Color.green(base) * inv + Color.green(overlay) * ratio).toInt()
        val b = (Color.blue(base) * inv + Color.blue(overlay) * ratio).toInt()
        return Color.rgb(r, g, b)
    }
}
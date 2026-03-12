package com.example.studentregistrationapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var studentId: EditText
    lateinit var fullName: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var age: EditText
    lateinit var genderGroup: RadioGroup
    lateinit var football: CheckBox
    lateinit var cricket: CheckBox
    lateinit var basketball: CheckBox
    lateinit var badminton: CheckBox
    lateinit var spinner: Spinner
    lateinit var dateButton: Button
    lateinit var submitBtn: Button
    lateinit var resetBtn: Button

    var dob = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        studentId = findViewById(R.id.studentId)
        fullName = findViewById(R.id.fullName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        age = findViewById(R.id.age)
        genderGroup = findViewById(R.id.genderGroup)

        football = findViewById(R.id.football)
        cricket = findViewById(R.id.cricket)
        basketball = findViewById(R.id.basketball)
        badminton = findViewById(R.id.badminton)

        spinner = findViewById(R.id.countrySpinner)
        dateButton = findViewById(R.id.dateButton)

        submitBtn = findViewById(R.id.submitBtn)
        resetBtn = findViewById(R.id.resetBtn)

        val countries = arrayOf("Bangladesh","India","USA","UK","Canada")

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            countries)

        spinner.adapter = adapter

        dateButton.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this,
                { _, y, m, d ->
                    dob = "$d/${m+1}/$y"
                    dateButton.text = dob
                },
                year, month, day)

            datePicker.show()
        }

        submitBtn.setOnClickListener {

            val id = studentId.text.toString()
            val name = fullName.text.toString()
            val mail = email.text.toString()
            val pass = password.text.toString()
            val ageValue = age.text.toString()

            val genderId = genderGroup.checkedRadioButtonId

            if(id.isEmpty() || name.isEmpty() || mail.isEmpty()
                || pass.isEmpty() || ageValue.isEmpty()
                || genderId == -1 || dob == "") {

                Toast.makeText(this,
                    "Please complete all required fields",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!mail.contains("@")){

                Toast.makeText(this,
                    "Invalid Email",
                    Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val gender = findViewById<RadioButton>(genderId).text

            val sports = mutableListOf<String>()

            if(football.isChecked) sports.add("Football")
            if(cricket.isChecked) sports.add("Cricket")
            if(basketball.isChecked) sports.add("Basketball")
            if(badminton.isChecked) sports.add("Badminton")

            val country = spinner.selectedItem.toString()

            val message = """
                ID: $id
                Name: $name
                Gender: $gender
                Sports: ${sports.joinToString()}
                Country: $country
                DOB: $dob
            """.trimIndent()

            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        }

        resetBtn.setOnClickListener {

            studentId.text.clear()
            fullName.text.clear()
            email.text.clear()
            password.text.clear()
            age.text.clear()

            genderGroup.clearCheck()

            football.isChecked = false
            cricket.isChecked = false
            basketball.isChecked = false
            badminton.isChecked = false

            spinner.setSelection(0)

            dob = ""
            dateButton.text = "Select Date"
        }
    }
}
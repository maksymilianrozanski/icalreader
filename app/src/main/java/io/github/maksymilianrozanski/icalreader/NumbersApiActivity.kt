package io.github.maksymilianrozanski.icalreader

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

import kotlinx.android.synthetic.main.activity_numbers_api.*

class NumbersApiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numbers_api)
        setSupportActionBar(toolbar)

        val layout = findViewById<LinearLayout>(R.id.numbersApiLinearLayout)
        val description = TextView(this)
        description.text = "Hello! 'text view added in Kotlin code'"
        description.textSize = 24.toFloat()
        layout.addView(description)
    }
}

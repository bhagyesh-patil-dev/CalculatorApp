package com.example.calculatorapp

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var textViewInput: TextView
    private lateinit var textViewOutput: TextView
    private lateinit var buttonAllClear: Button
    private lateinit var buttonBackspace: Button
    private lateinit var buttonCalculate: Button

    private var currentExpression = StringBuilder()
    private var resultExpression = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        splashScreen.setOnExitAnimationListener { splashProvider ->
            splashProvider.view.animate().alpha(0f).setDuration(400).withEndAction {
                splashProvider.remove()
            }.start()
        }

        textViewInput = findViewById(R.id.inputText)
        textViewOutput = findViewById(R.id.outputText)
        textViewInput.text = ""
        textViewOutput.text = ""

        buttonAllClear = findViewById(R.id.buttonAC)
        buttonBackspace = findViewById(R.id.buttonBackspace)
        buttonCalculate = findViewById(R.id.buttonEqualTo)

        val numberButtons = listOf(
            R.id.button00,
            R.id.button0,
            R.id.button1,
            R.id.button2,
            R.id.button3,
            R.id.button4,
            R.id.button5,
            R.id.button6,
            R.id.button7,
            R.id.button8,
            R.id.button9,
            R.id.buttonPlus,
            R.id.buttonMinus,
            R.id.buttonPercent,
            R.id.buttonDot,
        )

        for (id in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                val text = (it as Button).text.toString()
                appendInput(text)
            }
        }

        val multiply = findViewById<Button>(R.id.buttonMultiply)
        multiply.setOnClickListener {
            appendInput("*")
        }

        val divide = findViewById<Button>(R.id.buttonDivide)
        divide.setOnClickListener {
            appendInput("/")
        }

        buttonAllClear.setOnClickListener {
            onClearInput()
        }

        buttonBackspace.setOnClickListener {
            onBackspace()
        }

        buttonCalculate.setOnClickListener {
            val output = evaluateExpression(currentExpression.toString())
            resultExpression.clear()
            resultExpression.append(output)
            textViewOutput.text = resultExpression.toString()
        }
    }

    private fun appendInput(text: String) {
        val invalidStartOperators: Array<Char> = arrayOf('*', '/', '%')
        val invalidDoubleOperators: Array<Char> = arrayOf('+', '-', '*', '/', '%')
        // val invalidStartDoubleOperators: Array<Char> = arrayOf('+', '-')

        if (currentExpression.isEmpty() && text == ".") {
            currentExpression.append("0.")
            textViewInput.text = currentExpression
            return
        }

        if (text == ".") {
            val lastNumber = currentExpression.split(Regex("[+\\-*/%]")).last()
            if (lastNumber.contains(".")) return
        }

        if (currentExpression.isEmpty() && text[0] in invalidStartOperators) {
            return
        }

        if (currentExpression.isNotEmpty()) {
            if (currentExpression.last() in invalidDoubleOperators && text.last() in invalidDoubleOperators) {
                return
            }
        }

        currentExpression.append(text)
        textViewInput.text = currentExpression

        val displayText = currentExpression.toString().replace('*', '×').replace('/', '÷')

        textViewInput.text = displayText

    }

    private fun onClearInput() {
        currentExpression.clear()
        textViewInput.text = ""
        textViewOutput.text = ""
    }

    private fun onBackspace() {
        if (currentExpression.isNotEmpty()) {
            currentExpression.deleteCharAt(currentExpression.length - 1)
            textViewInput.text = currentExpression
        }
    }

    private fun evaluateExpression(expression: String): String {
        return try {
            val result = ExpressionBuilder(expression).build().evaluate()
            if (result == result.toLong().toDouble()) result.toLong().toString()
            else DecimalFormat("#.###").format(result)
        } catch (e: Exception) {
            "Error"
        }
    }
}
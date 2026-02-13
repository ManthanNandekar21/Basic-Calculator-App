package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var tvInput: TextView
    private var input = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvInput = findViewById(R.id.tvInput)

        // Number button IDs
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9
        )

        // Set number button listeners
        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener { button ->
                input += (button as Button).text.toString()
                tvInput.text = input
            }
        }

        // Operator buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { addOperator("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { addOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { addOperator("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { addOperator("/") }

        // Clear button
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            input = ""
            tvInput.text = ""
        }

        // Equal button
        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            try {
                val result = eval(input)
                tvInput.text = result.toString()
                input = result.toString()
            } catch (e: Exception) {
                tvInput.text = "Error"
            }
        }
    }

    private fun addOperator(op: String) {
        if (input.isNotEmpty()) {
            input += op
            tvInput.text = input
        }
    }

    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                return parseExpression()
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos

                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch in '0'.code..'9'.code || ch == '.'.code) {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    x = expression.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected character")
                }
                return x
            }

        }.parse()
    }
}

package com.example.storyapp.view.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = p0.toString()
                val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if (!isValid) {
                    setError("Invalid Email", null)
                } else {
                    error = null
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

}
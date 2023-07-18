package com.colworx.soccorapp.ui

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.colworx.soccorapp.R
import com.google.android.material.textfield.TextInputLayout

class DialogHelper {

    companion object {

        fun showToast(ctx: Context, msg: String) {
            Handler(Looper.getMainLooper()).post {
                // write your code here
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
            }

        }

        fun showAlertWithEmailInput(context: Context, callback: (String) -> Unit) {
            val textInputLayout = TextInputLayout(context)
            textInputLayout.setPadding(
                context.resources.getDimensionPixelOffset(R.dimen.dp_19), // if you look at android alert_dialog.xml, you will see the message textview have margin 14dp and padding 5dp. This is the reason why I use 19 here
                0,
                context.resources.getDimensionPixelOffset(R.dimen.dp_19),
                0
            )
            val input = EditText(context)
            input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            input.hint = "Email Address"
            textInputLayout.addView(input)

            val alert = AlertDialog.Builder(context)
                .setTitle("Enter your email address")
                .setView(textInputLayout)
                //.setMessage("Please enter your email address")
                .setPositiveButton("Submit") { dialog, _ ->
                    // do some thing with input.text
                    dialog.cancel()
                    callback(input.text.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.create()

            alert.show()
        }

        fun showAlertWithOTPInput(context: Context, callback: (String) -> Unit) {
            val textInputLayout = TextInputLayout(context)
            textInputLayout.setPadding(
                context.resources.getDimensionPixelOffset(R.dimen.dp_19), // if you look at android alert_dialog.xml, you will see the message textview have margin 14dp and padding 5dp. This is the reason why I use 19 here
                0,
                context.resources.getDimensionPixelOffset(R.dimen.dp_19),
                0
            )
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            textInputLayout.addView(input)

            val alert = AlertDialog.Builder(context)
                .setTitle("Enter Otp")
                .setView(textInputLayout)
                //.setMessage("Please enter your email address")
                .setPositiveButton("Submit") { dialog, _ ->
                    // do some thing with input.text
                    dialog.cancel()
                    callback(input.text.toString())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.create()

            alert.show()
        }

        fun showDialog2Input(context: Context, callback: (Int, Int) -> Unit) {

            val textInputLayout = LinearLayout(context)
            textInputLayout.orientation = LinearLayout.VERTICAL

            val input1 = EditText(context)
            input1.inputType = InputType.TYPE_CLASS_NUMBER
            input1.hint = "Home"
            textInputLayout.addView(input1)

            val input2 = EditText(context)
            input2.inputType = InputType.TYPE_CLASS_NUMBER
            input2.hint = "Visit"
            textInputLayout.addView(input2)

            val alert = AlertDialog.Builder(context)
                .setTitle("Enter Score")
                .setView(textInputLayout)
                //.setMessage("Please enter your email address")
                .setPositiveButton("Submit") { dialog, _ ->
                    // do some thing with input.text
                    dialog.cancel()
                    callback(input1.text.toString().toInt(), input2.text.toString().toInt())
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }.create()

            alert.show()
        }

    }
}
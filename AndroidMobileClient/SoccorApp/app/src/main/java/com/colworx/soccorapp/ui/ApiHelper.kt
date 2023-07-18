package com.colworx.soccorapp.ui

import android.widget.TextView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiHelper {

    companion object {
        fun performApi(call: Call<ResponseBody?>?, resultLabel: TextView) {
            call!!.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    try {
                        if (response.code() == 200) {
                            resultLabel.setText("success  = " + response.body()?.string() ?: "error")
                        } else {
                            resultLabel.setText("failed")
                        }
                    } catch (e : java.lang.Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    resultLabel.setText(t.localizedMessage ?: "- error")
                }
            })
        }
    }
}
package com.colworx.soccorapp

import com.colworx.soccorapp.api.RetrofitAPI
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Constants {


    /*
    *
     http://memberservicelb-226444084.us-east-1.elb.amazonaws.com/member/
http://sportservicelb-1546092084.us-east-1.elb.amazonaws.com/sport/
http://authservicelb-1914683537.us-east-1.elb.amazonaws.com/auth/
    * */

    companion object {
        var DOMAIN_URL = "https://oog65r0xq6.execute-api.us-east-1.amazonaws.com/"
        val userAgent = "Mozilla/5.0 (X11; U; Linux i586; en-US; rv:1.7.3) Gecko/20040924 Epiphany/1.4.4 (Ubuntu)"

//        var api_member = "http://54.173.93.46:8083/"
//        var api_sport = "http://52.91.229.223:8081/"
//        var api_auth = "http://44.202.164.100:8080/"
//        var api_stream = "stream/"

        var token = "asd"
        var gameId = "test"
        var isAdmin = false

        fun api(): RetrofitAPI{
            val gson = GsonBuilder()
                .setLenient()
                .create()


            val api = Retrofit.Builder().baseUrl(Constants.DOMAIN_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return api.create(RetrofitAPI::class.java)
        }
    }
}
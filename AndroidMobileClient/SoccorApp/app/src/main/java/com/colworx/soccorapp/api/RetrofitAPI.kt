package com.colworx.soccorapp.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface RetrofitAPI {

    @POST("auth")
    // on below line we are creating a method to post our data.
    fun authLogin(@Body dataModal: AuthRequestParam): Call<ResponseAuth?>?

    @GET("auth")
    // on below line we are creating a method to post our data.
    fun validateToken(@Query("token") token: String): Call<ResponseBody?>?

    @PUT("auth")
    // on below line we are creating a method to post our data.
    fun isAdmin(@Query("token") token: String): Call<ResponseBody?>?

    @GET("member")
    // on below line we are creating a method to post our data.
    fun testMemberApi(): Call<ResponseBody?>?

    @POST("member/comments")
    // on below line we are creating a method to post our data.
    fun addComments(@Query("token") token: String, @Body dataModel: CommentModel): Call<ResponseBody?>?

    @GET("member/comments")
    // on below line we are creating a method to post our data.
    fun getComments(@Query("gameId") gameId: String): Call<ResponseBody?>?

    @POST("member/votes")
    // on below line we are creating a method to post our data.
    fun addVotes(@Query("token") token: String, @Body dataModel: VotesModel): Call<ResponseBody?>?


    @GET("member/comments")
    // on below line we are creating a method to post our data.
    fun getVotes(@Query("gameId") gameId: String): Call<ResponseBody?>?


    @GET("sport/getAllGame")
    // on below line we are creating a method to post our data.
    fun getAllGames(@Query("token") token: String): Call<ResponseBody?>?

    @GET("sport/getLiveGame")
    // on below line we are creating a method to post our data.
    fun getLiveGame(@Query("token") token: String): Call<ResponseBody?>?

    @GET("sport/deleteAll")
    fun deleteAllGames(@Query("token") token: String): Call<ResponseBody?>?


    @POST("sport/setStop")
    fun stopGame(@Query("token") token: String): Call<ResponseBody?>?

    @POST("sport/setStart")
    fun startGame(@Query("token") token: String): Call<ResponseBody?>?

    @POST("sport/setScore")
    fun setScore(@Query("token") token: String, @Body score: ScoreParams): Call<ResponseBody?>?


    @POST("sport/addGame")
    fun addGame(@Query("token") token: String, @Body game: AddGameParam): Call<ResponseBody?>?


    @GET("sport")
    // on below line we are creating a method to post our data.
    fun testSportApi(): Call<ResponseBody?>?
}
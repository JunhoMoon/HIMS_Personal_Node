package com.hims.personal_node

import com.hims.personal_node.Model.*
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService{

    //post
//    @FormUrlEncoded
//    @POST("rest/request")
//    fun postRequest(@Field("message") message: String): Call<MutableList<Gender>>

    @GET("Authentication/getGender")
    fun getGender(): Call<MutableList<Gender>>

    @GET("Authentication/getOtherGenders")
    fun getOtherGenders(): Call<MutableList<Gender>>

    @GET("Authentication/getRace")
    fun getRace(): Call<MutableList<Race>>

    @GET("Authentication/getCountry")
    fun getCountry(): Call<MutableList<Country>>

    @FormUrlEncoded
    @POST("Authentication/getAdmin")
    fun getAdmin(@Field("country_no") country_no:Int): Call<MutableList<Admin>>

    @FormUrlEncoded
    @POST("Authentication/getCity")
    fun getCity(@Field("admin_no") admin_no:Int): Call<MutableList<City>>

    @FormUrlEncoded
    @POST("Authentication/getJob")
    fun getJob(@Field("parent_key") parent_key:String): Call<MutableList<Job>>

//    @FormUrlEncoded
//    @POST("/Message")
//    fun postMessage(message: String): Call<ResponseDTO>

    //post2
//    @POST("/{path}")
//    fun testRequest(@Path("path")path: String, @Body parameters: HashMap<String, Any>): Call<ResponseDTO>
}
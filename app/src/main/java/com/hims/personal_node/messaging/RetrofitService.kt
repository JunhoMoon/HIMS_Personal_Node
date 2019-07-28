package com.hims.personal_node.messaging

import com.hims.personal_node.Model.ResponseDTO
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService{

    //post
    @FormUrlEncoded
    @POST("/Authentication/Login")
    fun postRequest(@Field("id") id: String,
                    @Field("pw") pw: String): Call<ResponseDTO>

    //post2
    @POST("/{path}")
    fun testRequest(@Path("path")path: String, @Body parameters: HashMap<String, Any>): Call<ResponseDTO>
}
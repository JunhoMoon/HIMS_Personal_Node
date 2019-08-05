package com.hims.personal_node

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.hims.personal_node.Model.ResponseDTO
import com.hims.personal_node.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.AccessControlContext

object MyFirebaseInstanceIDService {

    internal fun initToken(context: Context){
//        if(FirebaseInstanceId.getInstance().instanceId != null){
//            Thread(Runnable {
//                try {
//                    FirebaseInstanceId.getInstance().deleteInstanceId()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }).start()
//        }
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result!!.token
                saveToken(token, context)
                println("token: " +token)
            })
    }

    private fun saveToken(token: String, context: Context){
        var server: RetrofitService? = null

        var retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.HIMS_Server_AP))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)

        var requestType = "saveToken"
//        var message = ParsingJSON.encordJSON(requestType ,token)

//        server?.postRequest(message)?.enqueue(object : Callback<String> {
//            override fun onFailure(call: Call<String>?, t: Throwable?) {
//            }
//            override fun onResponse(call: Call<String>?, response: Response<String>?) {
//            }
//        })
    }
}
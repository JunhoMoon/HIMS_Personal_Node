package com.hims.personal_node

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId

class MyFirebaseInstanceIDService {
    var tokenId:String = ""
    //토큰 확인
    fun checkToken(): String{

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FIREBASE", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                var token = task.result!!.token
            })

        println("test3:" + tokenId)
        return tokenId
    }
}
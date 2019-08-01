package com.hims.personal_node

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.text.HtmlCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.widget.Toast
import com.hims.personal_node.Model.ResponseDTO
import com.hims.personal_node.messaging.RetrofitService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.hims.personal_node.Model.NodeIdentity
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.X509EncodedKeySpec


class MainActivity : AppCompatActivity() {

    var server: RetrofitService? = null

    var VALID_EMAIL_ADDRESS_REGEX: Pattern =
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z.]{2,6}$", Pattern.CASE_INSENSITIVE)
    var VALID_EMAIL_ADDRESS_REGEX_C: Pattern = Pattern.compile("[^a-zA-Z0-9._%+@-]")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM: Pattern = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM_C: Pattern = Pattern.compile("[^a-zA-Z0-9!@.#\$%^&*?_~]")

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //밑줄 추가
        bt_otherWay.text = HtmlCompat.fromHtml("<u>" + bt_otherWay.text + "</u>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        //Retrofit Rest 수신 / ID PW
        var retrofit = Retrofit.Builder()
            .baseUrl("http://220.149.87.125:10000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)

        //아이디 체크
        user_id.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_EMAIL_ADDRESS_REGEX_C.matcher(user_id.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_id.setText(
                        user_id.getText().toString().substring(
                            0,
                            user_id.getText().toString().length - 1
                        )
                    )
                    user_id.setSelection(user_id.getText().toString().length)
                }
            }
        })

        //암호 체크
        user_pw.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_PASSWOLD_REGEX_ALPHA_NUM_C.matcher(user_pw.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_pw.setText(user_pw.getText().toString().substring(0, user_pw.getText().toString().length - 1))
                    user_pw.setSelection(user_pw.getText().toString().length)
                }
            }
        })

        //로그인 정보 확인
        bt_login.setOnClickListener() {
            var id = user_id.getText().toString()
            var pw = user_pw.getText().toString()
            if (id.equals("")) {
                messageToast("Please enter e-Mail")
                user_id.requestFocus()
            } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(id).matches()) {
                messageToast("Please enter in e-Mail format")
                user_id.requestFocus()
            } else if (!VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pw).matches()) {
                messageToast("Please enter Password")
                user_pw.requestFocus()
            } else {
                //중앙 서버 인증 공개키 획득

                //SHA512 암호화
                pw = EncryptionSHA.Encryption(pw)
//                println("pw:" + pw)

                //데이터 객체화
                var nodeIdentity = NodeIdentity(id, pw,null)

                //객체 JSON화
                val gson = Gson()
                val nodeIdentity_json = gson.toJson(nodeIdentity)
//                println("nodeIdentity_json: "+ nodeIdentity_json.toString())

                //TEST RSA 키 생성
                var secureRandom = SecureRandom()
                var gen = KeyPairGenerator.getInstance("RSA")
                gen.initialize(2048, secureRandom)
                var keyPair = gen.genKeyPair()

                EncryptionRSA.init(this.applicationContext)

                //RSA 암호화
                val message = EncryptionRSATest.encrypt(nodeIdentity_json, keyPair.public)
//                println("message:" + message)

                //RSA 복호화
                val reMessage = EncryptionRSATest.decrypt(message, keyPair.private)
//                println("reMessage:"+reMessage)

                //String Rest화
                var nodeIdentity2 = gson.fromJson(reMessage, NodeIdentity::class.java)
//                println("nodeIdentity2_test ID:"+nodeIdentity2.id)

               //서버 로그인 체크 REST
                server?.postRequest(id,pw)?.enqueue(object : Callback<ResponseDTO> {
                    override fun onFailure(call: Call<ResponseDTO>?, t: Throwable?) {
                        messageToast(t.toString())
//                        println("fail:" + t.toString())
                    }
                    override fun onResponse(call: Call<ResponseDTO>?, response: Response<ResponseDTO>?) {
                        messageToast(response?.body().toString())
//                        println("respon:" + response?.body().toString())

                        //FCM 토큰값 업데이트
                        FirebaseInstanceId.getInstance().instanceId
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (!task.isSuccessful) {
//                                    Log.w("FIREBASE", "getInstanceId failed", task.exception)
                                    return@OnCompleteListener
                                }

                                // Get new Instance ID token
                                val token = task.result!!.token
//                                println("token:"+token)

//                         Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                    Log.d("FIREBASE", token)
//                        Toast.makeText(this, token, Toast.LENGTH_SHORT).show()
                            })
                    }
                })
            }
        }
        bt_join.setOnClickListener(){
            val publicKey = EncryptionRSA.getPublicKey()
            val pubKey:PublicKey = EncryptionRSA.stringToPublickey(publicKey)

            var enText = EncryptionRSA.encryptTest("test", pubKey)
            println(enText)
            var deText = EncryptionRSA.decrypt(enText)
            println(deText)
        }
    }

    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}


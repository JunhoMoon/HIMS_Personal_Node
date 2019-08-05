package com.hims.personal_node

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.text.HtmlCompat
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern
import com.hims.personal_node.Model.NodeIdentity
import com.hims.personal_node.key_manager.EncryptionRSA
import com.hims.personal_node.key_manager.EncryptionSHA

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

        MyFirebaseInstanceIDService.initToken(this)

        //add under line
        bt_otherWay.text = HtmlCompat.fromHtml("<u>" + bt_otherWay.text + "</u>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        //Retrofit init for server
        var retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.HIMS_Server_AP))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)

        //id Check
        user_id.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_EMAIL_ADDRESS_REGEX_C.matcher(user_id.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_id.setText(
                        user_id.text.toString().substring(
                            0,
                            user_id.text.toString().length - 1
                        )
                    )
                    user_id.setSelection(user_id.text.toString().length)
                }
            }
        })

        //check pw
        user_pw.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_PASSWOLD_REGEX_ALPHA_NUM_C.matcher(user_pw.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_pw.setText(user_pw.text.toString().substring(0, user_pw.text.toString().length - 1))
                    user_pw.setSelection(user_pw.text.toString().length)
                }
            }
        })

        //check login data form
        bt_login.setOnClickListener() {
            var id = user_id.text.toString()
            var pw = user_pw.text.toString()
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
                //all Test

                //pw encrypt by SHA
                pw = EncryptionSHA.Encryption(pw)

                //data to object
                var nodeIdentity = NodeIdentity(id, pw,null)

                //object to JSON
                var requestType = "loginCheck"
                var message = ParsingJSON.modelToJson(nodeIdentity)

                println(message)

                //init RSA key in keystore
                EncryptionRSA.init(this.applicationContext)

                //get Server PublicKey
                var serverPublicKeyString = "asdf"
                var serverPublicKey = EncryptionRSA.stringToPublickey(serverPublicKeyString)

                //data encrypt by Server PublicKey
                message = EncryptionRSA.encryptByOtherKey(message, serverPublicKey)

                //data encrypt by RSA
//                val message = EncryptionRSA.encryptTest(nodeIdentity_json, keyPair.public)

                //data decrypt by RSA
//                val reMessage = EncryptionRSA.decryptTest(message, keyPair.private)

                //String to Object
//                var nodeIdentity2 = gson.fromJson(reMessage, NodeIdentity::class.java)

               //login check on server by Retrofit

//                server?.postRequest(message)?.enqueue(object : Callback<String> {
//                    override fun onFailure(call: Call<String>?, t: Throwable?) {
                        MyFirebaseInstanceIDService.initToken(parent)
//
//                        messageToast(t.toString())
//                    }
//                    override fun onResponse(call: Call<String>?, response: Response<String>?) {
//                        messageToast(response?.body().toString())
//                    }
//                })
            }
        }
        bt_join.setOnClickListener(){
//            MyFirebaseInstanceIDService.initToken(this)
//            MyFirebaseInstanceIDService.initToken(this)

//            val publicKey = EncryptionRSA.getPublicKey()
//            val pubKey:PublicKey = EncryptionRSA.stringToPublickey(publicKey)
//
//            var enText = EncryptionRSA.encryptTest("test", pubKey)
//            println(enText)
//            var deText = EncryptionRSA.decrypt(enText)
//            println(deText)
            val intent:Intent = Intent(this, JoinPage::class.java)
            startActivity(intent)
        }
    }

    //notify by Toast
    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    companion object {
        private const val TAG = "Login Page"
    }
}


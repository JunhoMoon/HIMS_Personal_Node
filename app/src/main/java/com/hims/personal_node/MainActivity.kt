package com.hims.personal_node

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
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

class MainActivity : AppCompatActivity() {

    var server: RetrofitService? = null

    var VALID_EMAIL_ADDRESS_REGEX: Pattern =
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z.]{2,6}$", Pattern.CASE_INSENSITIVE)
    var VALID_EMAIL_ADDRESS_REGEX_C: Pattern = Pattern.compile("[^a-zA-Z0-9._%+@-]")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM: Pattern = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM_C: Pattern = Pattern.compile("[^a-zA-Z0-9!@.#\$%^&*?_~]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //밑줄 추가
        bt_otherWay.setText(Html.fromHtml("<u>" + bt_otherWay.getText().toString() + "</u>"))

        //Retrofit Rest 수신 / ID PW
        var retrofit = Retrofit.Builder()
            .baseUrl("http://220.149.87.125:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        server = retrofit.create(RetrofitService::class.java)

        //아이디 체크
        user_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (VALID_EMAIL_ADDRESS_REGEX_C.matcher(user_name.getText().toString()).find()) {
                    messageToast("Invalid character")
                    user_name.setText(
                        user_name.getText().toString().substring(
                            0,
                            user_name.getText().toString().length - 1
                        )
                    )
                    user_name.setSelection(user_name.getText().toString().length)
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
            if (user_name.getText().toString().equals("")) {
                messageToast("Please enter e-Mail")
                user_name.requestFocus()
            } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(user_name.getText().toString()).matches()) {
                messageToast("Please enter in e-Mail format")
                user_name.requestFocus()
            } else if (!VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(user_pw.getText().toString()).matches()) {
                messageToast("Please enter Password")
                user_pw.requestFocus()
            } else {
                //중앙 서버 인증
                server?.postRequest(user_name.getText().toString(),user_pw.getText().toString())?.enqueue(object : Callback<ResponseDTO> {
                    override fun onFailure(call: Call<ResponseDTO>?, t: Throwable?) {
                        messageToast(t.toString())
                    }

                    override fun onResponse(call: Call<ResponseDTO>?, response: Response<ResponseDTO>?) {
                        messageToast(response?.body().toString())
                    }
                })
            }
        }
    }

    fun messageToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}


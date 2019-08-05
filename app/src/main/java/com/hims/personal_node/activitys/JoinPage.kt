package com.hims.personal_node

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.text.HtmlCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_join_page.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern
import android.view.ViewGroup
import android.widget.*
import com.hims.personal_node.Model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JoinPage : AppCompatActivity() {
    var server: RetrofitService? = null

    var VALID_EMAIL_ADDRESS_REGEX: Pattern =
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z.]{2,6}$", Pattern.CASE_INSENSITIVE)
    var VALID_EMAIL_ADDRESS_REGEX_C: Pattern = Pattern.compile("[^a-zA-Z0-9._%+@-]")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM: Pattern = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$")
    var VALID_PASSWOLD_REGEX_ALPHA_NUM_C: Pattern = Pattern.compile("[^a-zA-Z0-9!@.#\$%^&*?_~]")

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_page)

        registration_title.text =
            HtmlCompat.fromHtml("<u>" + registration_title.text + "</u>", HtmlCompat.FROM_HTML_MODE_LEGACY)

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

        server?.getGender()?.enqueue(object : Callback<MutableList<Gender>> {
            override fun onFailure(call: Call<MutableList<Gender>>?, t: Throwable?) {
                Log.d("onFailure : ", t?.message)
                messageToast("onFailure : " + t?.message)

                finish()
            }

            override fun onResponse(call: Call<MutableList<Gender>>?, response: Response<MutableList<Gender>>?) {
                if (response!!.isSuccessful) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString())
                        var gender_list: MutableList<Gender>? = response.body()
                        gender_list?.add(Gender(0, null, "Select Your Gender"))
                        gender_list?.sortBy { gender -> gender.gender_key }

                        var adapt = object :
                            ArrayAdapter<Gender>(this@JoinPage, android.R.layout.simple_spinner_item, gender_list) {
                            override fun isEnabled(position: Int): Boolean {
                                return position != 0
                            }

                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                var view = super.getDropDownView(position, convertView, parent)
                                var tv = view as TextView
                                if (position == 0) {
                                    tv.setTextColor(Color.WHITE)
                                    tv.setBackgroundColor(Color.GRAY)
                                } else {
                                    tv.setTextColor(Color.BLACK)
                                    tv.setBackgroundColor(Color.WHITE)
                                }
                                return view
                            }
                        }
                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        gender_spinner.adapter = adapt
                    } else {
                        Log.i("onEmptyResponse", "Returned empty response")
                    }
                }
                Log.d("onResponse : ", response.body().toString())
            }
        })

        gender_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val gender = parent.selectedItem as Gender
                when (gender.gender_key) {
                    0 -> {
                    }
                    3 -> {
                        server?.getOtherGenders()?.enqueue(object : Callback<MutableList<Gender>> {
                            override fun onFailure(call: Call<MutableList<Gender>>?, t: Throwable?) {
                                Log.d("onFailure : ", t?.message)
                                messageToast("onFailure : " + t?.message)

                                finish()
                            }

                            override fun onResponse(
                                call: Call<MutableList<Gender>>?,
                                response: Response<MutableList<Gender>>?
                            ) {
                                if (response!!.isSuccessful) {
                                    if (response.body() != null) {
                                        Log.i("onSuccess", response.body().toString())

                                        var gender_list: MutableList<Gender>? = response.body()
                                        gender_list?.add(Gender(0, null, "Select Your Other Genders"))
                                        gender_list?.sortBy { gender -> gender.gender_key }

                                        var adapt = object : ArrayAdapter<Gender>(
                                            this@JoinPage,
                                            android.R.layout.simple_spinner_item,
                                            gender_list
                                        ) {
                                            override fun isEnabled(position: Int): Boolean {
                                                return position != 0
                                            }

                                            override fun getDropDownView(
                                                position: Int,
                                                convertView: View?,
                                                parent: ViewGroup
                                            ): View {
                                                var view = super.getDropDownView(position, convertView, parent)
                                                var tv = view as TextView
                                                if (position == 0) {
                                                    tv.setTextColor(Color.WHITE)
                                                    tv.setBackgroundColor(Color.GRAY)
                                                } else {
                                                    tv.setTextColor(Color.BLACK)
                                                    tv.setBackgroundColor(Color.WHITE)
                                                }
                                                return view
                                            }
                                        }
                                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        others_gender_spinner.adapter = adapt
                                        others_gender_spinner.visibility = View.VISIBLE
                                    } else {
                                        Log.i("onEmptyResponse", "Returned empty response")
                                    }
                                }
                                Log.d("onResponse : ", response.body().toString())
                            }
                        })
                    }
                    else -> {
                        others_gender_spinner.visibility = View.GONE
                        gender_text.visibility = View.GONE
                        gender_text.setText(gender.gender_name)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        others_gender_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val gender = parent.selectedItem as Gender
                when (gender.gender_key) {
                    0 -> {
                    }
                    99 -> {
                        gender_text.setText("")
                        gender_text.visibility = View.VISIBLE
                    }
                    else -> {
                        gender_text.setText(gender.gender_name)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        birth_year.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (birth_year.text.toString() != "") {
                    var year = birth_year.text.toString().toInt()
                    var nowYear = LocalDate.now().year
                    if (year > nowYear || year < 1880) {
                        messageToast("Invalid Year")
                        birth_year.setText("")
                    }
                }
            }
        }

        birth_month.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (birth_month.text.toString() != "") {
                    var month = birth_month.text.toString().toInt()
                    if (month > 12 || month < 1) {
                        messageToast("Invalid Month")
                        birth_month.setText("")
                    } else if (birth_year.text.toString() != "") {
                        var now = LocalDate.now()
                        var str: String = birth_year.text.toString() + "-" + birth_month.text.toString() + "-01"
                        var strdate: LocalDate = LocalDate.parse(
                            str.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            DateTimeFormatter.ISO_DATE
                        )
                        if (strdate > now) {
                            messageToast("Invalid Month")
                            birth_month.setText("")
                        }
                    }
                }
            }
        }

        server?.getRace()?.enqueue(object : Callback<MutableList<Race>> {
            override fun onFailure(call: Call<MutableList<Race>>?, t: Throwable?) {
                Log.d("onFailure : ", t?.message)
                messageToast("onFailure : " + t?.message)

                finish()
            }

            override fun onResponse(call: Call<MutableList<Race>>?, response: Response<MutableList<Race>>?) {
                if (response!!.isSuccessful) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString())

                        var race_list: MutableList<Race>? = mutableListOf<Race>()
                        race_list = response.body()
                        race_list?.add(Race(0, "Select Your Race"))
                        race_list?.sortBy { race -> race.race_key }

                        var adapt = object :
                            ArrayAdapter<Race>(this@JoinPage, android.R.layout.simple_spinner_item, race_list) {
                            override fun isEnabled(position: Int): Boolean {
                                return position != 0
                            }

                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                var view = super.getDropDownView(position, convertView, parent)
                                var tv = view as TextView
                                if (position == 0) {
                                    tv.setTextColor(Color.WHITE)
                                    tv.setBackgroundColor(Color.GRAY)
                                } else {
                                    tv.setTextColor(Color.BLACK)
                                    tv.setBackgroundColor(Color.WHITE)
                                }
                                return view
                            }
                        }
                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        race_spinner.adapter = adapt
                    } else {
                        Log.i("onEmptyResponse", "Returned empty response")
                    }
                }
                Log.d("onResponse : ", response.body().toString())
            }
        })

        race_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val race = parent.selectedItem as Race
                when (race.race_key) {
                    0 ->{}
                    99 -> {
                        race_text.setText("")
                        race_text.visibility = View.VISIBLE
                    }
                    else -> {
                        race_text.setText(race.race_name)
                        race_text.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        server?.getCountry()?.enqueue(object : Callback<MutableList<Country>> {
            override fun onFailure(call: Call<MutableList<Country>>?, t: Throwable?) {
                Log.d("onFailure : ", t?.message)
                messageToast("onFailure : " + t?.message)

                finish()
            }

            override fun onResponse(call: Call<MutableList<Country>>?, response: Response<MutableList<Country>>?) {
                if (response!!.isSuccessful) {
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString())
                        var country_list = response.body()
                        country_list?.add(Country(0, "Select Your Country"))
                        country_list?.sortBy { country -> country.country_no }

                        var adapt = object :
                            ArrayAdapter<Country>(this@JoinPage, android.R.layout.simple_spinner_item, country_list) {
                            override fun isEnabled(position: Int): Boolean {
                                return position != 0
                            }

                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                var view = super.getDropDownView(position, convertView, parent)
                                var tv = view as TextView
                                if (position == 0) {
                                    tv.setTextColor(Color.WHITE)
                                    tv.setBackgroundColor(Color.GRAY)
                                } else {
                                    tv.setTextColor(Color.BLACK)
                                    tv.setBackgroundColor(Color.WHITE)
                                }
                                return view
                            }
                        }
                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        country_spinner.adapter = adapt
                    } else {
                        Log.i("onEmptyResponse", "Returned empty response")
                    }
                }
                Log.d("onResponse : ", response.body().toString())
            }
        })

        country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val country = parent.selectedItem as Country
                when (country.country_no) {
                    0 ->{}
                    999 -> {
                        country_text.setText("")
                        country_text.visibility = View.VISIBLE
                        admin_spinner.visibility = View.GONE
                        admin_text.setText("")
                        admin_text.visibility = View.VISIBLE
                        city_spinner.visibility = View.GONE
                        city_text.setText("")
                        city_text.visibility = View.VISIBLE
                    }
                    else -> {
                        country_text.setText(country.country_name)
                        country_text.visibility = View.GONE
                        admin_spinner.visibility = View.GONE
                        admin_text.setText("")
                        admin_text.visibility = View.GONE
                        city_spinner.visibility = View.GONE
                        city_text.setText("")
                        city_text.visibility = View.GONE

                        server?.getAdmin(country.country_no)?.enqueue(object : Callback<MutableList<Admin>> {
                            override fun onFailure(call: Call<MutableList<Admin>>?, t: Throwable?) {
                                Log.d("onFailure : ", t?.message)
                                messageToast("onFailure : " + t?.message)

                                finish()
                            }

                            override fun onResponse(
                                call: Call<MutableList<Admin>>?,
                                response: Response<MutableList<Admin>>?
                            ) {
                                if (response!!.isSuccessful) {
                                    if (response.body() != null) {
                                        Log.i("onSuccess", response.body().toString())
                                        var admin_list = response.body()
                                        admin_list?.add(Admin(0, "Select Your Admin", country.country_no))
                                        admin_list?.sortBy { admin -> admin.admin_no }

                                        var adapt = object : ArrayAdapter<Admin>(
                                            this@JoinPage,
                                            android.R.layout.simple_spinner_item,
                                            admin_list
                                        ) {
                                            override fun isEnabled(position: Int): Boolean {
                                                return position != 0
                                            }

                                            override fun getDropDownView(
                                                position: Int,
                                                convertView: View?,
                                                parent: ViewGroup
                                            ): View {
                                                var view = super.getDropDownView(position, convertView, parent)
                                                var tv = view as TextView
                                                if (position == 0) {
                                                    tv.setTextColor(Color.WHITE)
                                                    tv.setBackgroundColor(Color.GRAY)
                                                } else {
                                                    tv.setTextColor(Color.BLACK)
                                                    tv.setBackgroundColor(Color.WHITE)
                                                }
                                                return view
                                            }
                                        }
                                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        admin_spinner.adapter = adapt
                                        if (admin_list?.size!! > 2){
                                            admin_spinner.visibility = View.VISIBLE
                                        }
                                    } else {
                                        Log.i("onEmptyResponse", "Returned empty response")
                                    }
                                }
                                Log.d("onResponse : ", response.body().toString())
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        admin_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val admin = parent.selectedItem as Admin
                when (admin.admin_no) {
                    0 ->{}
                    9999 -> {
                        admin_text.setText("")
                        admin_text.visibility = View.VISIBLE
                        city_spinner.visibility = View.GONE
                        city_text.setText("")
                        city_text.visibility = View.VISIBLE
                    }
                    else -> {
                        admin_text.setText(admin.admin_name)
                        admin_text.visibility = View.GONE
                        city_spinner.visibility = View.GONE
                        city_text.setText("")
                        city_text.visibility = View.GONE

                        server?.getCity(admin.admin_no)?.enqueue(object : Callback<MutableList<City>> {
                            override fun onFailure(call: Call<MutableList<City>>?, t: Throwable?) {
                                Log.d("onFailure : ", t?.message)
                                messageToast("onFailure : " + t?.message)

                                finish()
                            }

                            override fun onResponse(
                                call: Call<MutableList<City>>?,
                                response: Response<MutableList<City>>?
                            ) {
                                if (response!!.isSuccessful) {
                                    if (response.body() != null) {
                                        Log.i("onSuccess", response.body().toString())
                                        var city_list = response.body()
                                        city_list?.add(City(0, "Select Your City", admin.admin_no))
                                        city_list?.sortBy { city -> city.city_no }

                                        var adapt = object : ArrayAdapter<City>(
                                            this@JoinPage,
                                            android.R.layout.simple_spinner_item,
                                            city_list
                                        ) {
                                            override fun isEnabled(position: Int): Boolean {
                                                return position != 0
                                            }

                                            override fun getDropDownView(
                                                position: Int,
                                                convertView: View?,
                                                parent: ViewGroup
                                            ): View {
                                                var view = super.getDropDownView(position, convertView, parent)
                                                var tv = view as TextView
                                                if (position == 0) {
                                                    tv.setTextColor(Color.WHITE)
                                                    tv.setBackgroundColor(Color.GRAY)
                                                } else {
                                                    tv.setTextColor(Color.BLACK)
                                                    tv.setBackgroundColor(Color.WHITE)
                                                }
                                                return view
                                            }
                                        }
                                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        city_spinner.adapter = adapt
                                        if(city_list?.size!! > 2){
                                            city_spinner.visibility = View.VISIBLE
                                        }
                                    } else {
                                        Log.i("onEmptyResponse", "Returned empty response")
                                    }
                                }
                                Log.d("onResponse : ", response.body().toString())
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val city = parent.selectedItem as City
                when (city.city_no) {
                    0 ->{}
                    99999 -> {
                        city_text.setText("")
                        city_text.visibility = View.VISIBLE
                    }
                    else -> {
                        city_text.setText(city.city_name)
                        city_text.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        server?.getJob("first")?.enqueue(object : Callback<MutableList<Job>> {
            override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                Log.d("onFailure : ", t?.message)
                messageToast("onFailure : " + t?.message)

                finish()
            }

            override fun onResponse(call: Call<MutableList<Job>>?, response: Response<MutableList<Job>>?) {
                if (response!!.isSuccessful) {
                    println("test11111")
                    if (response.body() != null) {
                        Log.i("onSuccess", response.body().toString())
                        var job_list = response.body()
                        println(job_list?.size)
                        job_list?.add(Job("0", null, "Select Your Job Category 1st"))
                        job_list?.sortBy { job -> job.job_key }

                        var adapt = object :
                            ArrayAdapter<Job>(this@JoinPage, android.R.layout.simple_spinner_item, job_list) {
                            override fun isEnabled(position: Int): Boolean {
                                return position != 0
                            }

                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                var view = super.getDropDownView(position, convertView, parent)
                                var tv = view as TextView
                                if (position == 0) {
                                    tv.setTextColor(Color.WHITE)
                                    tv.setBackgroundColor(Color.GRAY)
                                } else {
                                    tv.setTextColor(Color.BLACK)
                                    tv.setBackgroundColor(Color.WHITE)
                                }
                                return view
                            }
                        }
                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        job1_spinner.adapter = adapt
                    } else {
                        Log.i("onEmptyResponse", "Returned empty response")
                    }
                }
                Log.d("onResponse : ", response.body().toString())
            }
        })

        job1_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val job = parent.selectedItem as Job
                when (job.job_key) {
                    "0" ->{}
                    "99-9999" -> {
                        job_text.setText("")
                        job_text.visibility = View.VISIBLE
                        job2_spinner.visibility = View.GONE
                        job3_spinner.visibility = View.GONE
                        job4_spinner.visibility = View.GONE
                    }
                    else -> {
                        job_text.setText(job.job_name)
                        job_text.visibility = View.GONE
                        job2_spinner.visibility = View.GONE
                        job3_spinner.visibility = View.GONE
                        job4_spinner.visibility = View.GONE

                        server?.getJob(job.job_key)?.enqueue(object : Callback<MutableList<Job>> {
                            override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                                Log.d("onFailure : ", t?.message)
                                messageToast("onFailure : " + t?.message)

                                finish()
                            }

                            override fun onResponse(call: Call<MutableList<Job>>?, response: Response<MutableList<Job>>?) {
                                if (response!!.isSuccessful) {
                                    if (response.body() != null) {
                                        Log.i("onSuccess", response.body().toString())
                                        var job_list = response.body()
                                        job_list?.add(Job("0", null, "Select Your Job Category 2nd"))
                                        job_list?.sortBy { job -> job.job_key }

                                        var adapt = object :
                                            ArrayAdapter<Job>(this@JoinPage, android.R.layout.simple_spinner_item, job_list) {
                                            override fun isEnabled(position: Int): Boolean {
                                                return position != 0
                                            }

                                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                                var view = super.getDropDownView(position, convertView, parent)
                                                var tv = view as TextView
                                                if (position == 0) {
                                                    tv.setTextColor(Color.WHITE)
                                                    tv.setBackgroundColor(Color.GRAY)
                                                } else {
                                                    tv.setTextColor(Color.BLACK)
                                                    tv.setBackgroundColor(Color.WHITE)
                                                }
                                                return view
                                            }
                                        }
                                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        job2_spinner.adapter = adapt
                                        if(job_list?.size!! > 3){
                                            job2_spinner.visibility = View.VISIBLE
                                        }
                                    } else {
                                        Log.i("onEmptyResponse", "Returned empty response")
                                    }
                                }
                                Log.d("onResponse : ", response.body().toString())
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        job2_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val job = parent.selectedItem as Job
                when (job.job_key) {
                    "0" ->{}
                    "99-9999" -> {
                        job_text.setText("")
                        job_text.visibility = View.VISIBLE
                        job3_spinner.visibility = View.GONE
                        job4_spinner.visibility = View.GONE
                    }
                    else -> {
                        job_text.setText(job.job_name)
                        job_text.visibility = View.GONE
                        job3_spinner.visibility = View.GONE
                        job4_spinner.visibility = View.GONE

                        server?.getJob(job.job_key)?.enqueue(object : Callback<MutableList<Job>> {
                            override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                                Log.d("onFailure : ", t?.message)
                                messageToast("onFailure : " + t?.message)

                                finish()
                            }

                            override fun onResponse(call: Call<MutableList<Job>>?, response: Response<MutableList<Job>>?) {
                                if (response!!.isSuccessful) {
                                    if (response.body() != null) {
                                        Log.i("onSuccess", response.body().toString())
                                        var job_list = response.body()
                                        job_list?.add(Job("0", null, "Select Your Job Category 3nd"))
                                        job_list?.sortBy { job -> job.job_key }

                                        var adapt = object :
                                            ArrayAdapter<Job>(this@JoinPage, android.R.layout.simple_spinner_item, job_list) {
                                            override fun isEnabled(position: Int): Boolean {
                                                return position != 0
                                            }

                                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                                var view = super.getDropDownView(position, convertView, parent)
                                                var tv = view as TextView
                                                if (position == 0) {
                                                    tv.setTextColor(Color.WHITE)
                                                    tv.setBackgroundColor(Color.GRAY)
                                                } else {
                                                    tv.setTextColor(Color.BLACK)
                                                    tv.setBackgroundColor(Color.WHITE)
                                                }
                                                return view
                                            }
                                        }
                                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        job3_spinner.adapter = adapt
                                        if(job_list?.size!! > 3){
                                            job3_spinner.visibility = View.VISIBLE
                                        }
                                    } else {
                                        Log.i("onEmptyResponse", "Returned empty response")
                                    }
                                }
                                Log.d("onResponse : ", response.body().toString())
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        job3_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val job = parent.selectedItem as Job
                when (job.job_key) {
                    "0" ->{}
                    "99-9999" -> {
                        job_text.setText("")
                        job_text.visibility = View.VISIBLE
                        job4_spinner.visibility = View.GONE
                    }
                    else -> {
                        job4_spinner.visibility = View.GONE

                        server?.getJob(job.job_key)?.enqueue(object : Callback<MutableList<Job>> {
                            override fun onFailure(call: Call<MutableList<Job>>?, t: Throwable?) {
                                Log.d("onFailure : ", t?.message)
                                messageToast("onFailure : " + t?.message)

                                finish()
                            }

                            override fun onResponse(call: Call<MutableList<Job>>?, response: Response<MutableList<Job>>?) {
                                if (response!!.isSuccessful) {
                                    if (response.body() != null) {
                                        Log.i("onSuccess", response.body().toString())
                                        var job_list = response.body()
                                        job_list?.add(Job("0", null, "Select Your Job"))
                                        job_list?.sortBy { job -> job.job_key }

                                        var adapt = object :
                                            ArrayAdapter<Job>(this@JoinPage, android.R.layout.simple_spinner_item, job_list) {
                                            override fun isEnabled(position: Int): Boolean {
                                                return position != 0
                                            }

                                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                                var view = super.getDropDownView(position, convertView, parent)
                                                var tv = view as TextView
                                                if (position == 0) {
                                                    tv.setTextColor(Color.WHITE)
                                                    tv.setBackgroundColor(Color.GRAY)
                                                } else {
                                                    tv.setTextColor(Color.BLACK)
                                                    tv.setBackgroundColor(Color.WHITE)
                                                }
                                                return view
                                            }
                                        }
                                        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        job4_spinner.adapter = adapt
                                        if(job_list?.size!! > 3){
                                            job4_spinner.visibility = View.VISIBLE
                                        }
                                    } else {
                                        Log.i("onEmptyResponse", "Returned empty response")
                                    }
                                }
                                Log.d("onResponse : ", response.body().toString())
                            }
                        })
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        job4_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val job = parent.selectedItem as Job
                when (job.job_key) {
                    "0" ->{}
                    "99-9999" -> {
                        job_text.setText("")
                        job_text.visibility = View.VISIBLE
                    }
                    else -> {
                        job_text.setText(job.job_name)
                        job_text.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun messageToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

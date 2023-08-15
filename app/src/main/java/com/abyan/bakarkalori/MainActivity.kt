package com.abyan.bakarkalori

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abyan.bakarkalori.databinding.ActivityMainBinding
import android.text.Editable
import android.text.TextWatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val myCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        // Menambahkan listener pada EditText
        binding.aktivitas.addTextChangedListener(textWatcher)

        // Menjalankan validasi awal
        validateButton()

        binding.submit.setOnClickListener() {
            if (binding.submit.isEnabled) {
                myCoroutineScope.launch {
                    burnedCaloriesHasil()
                }
            }

        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            validateButton()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private fun validateButton() {
        val inputText1 = binding.aktivitas.text.toString().trim()

        // Validasi apakah salah satu dari inputText kosong atau berisi "0"
        val isInputInvalid = inputText1.isEmpty()

        // Mengaktifkan atau menonaktifkan tombol berdasarkan validasi
        binding.submit.isEnabled = !isInputInvalid
    }

    private suspend fun burnedCaloriesHasil() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://calories-burned-by-api-ninjas.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader(
                                "X-RapidAPI-Key",
                                "ef85a8a7bdmsh24f73ed4dfdc948p1903b8jsnc2e087d0d97a"
                            )
                            .addHeader(
                                "X-RapidAPI-Host",
                                "calories-burned-by-api-ninjas.p.rapidapi.com"
                            )
                            .build()
                        chain.proceed(request)
                    }
                    .build())
            .build()

        val apiService = retrofit.create(apiService::class.java)


        val activity = binding.aktivitas.text

        val call = apiService.getExercises(activity)
        call.enqueue(object : Callback<List<ResponseModelBurnedCalories>> {
            override fun onResponse(
                call: Call<List<ResponseModelBurnedCalories>>,
                response: Response<List<ResponseModelBurnedCalories>>
            ) {
                if (response.isSuccessful) {
                    val exerciseList = response.body()
                    if (exerciseList != null) {
                        val caloriesPerHour = exerciseList[0].calories_per_hour
                        binding.hasil.text = "$caloriesPerHour Kalori/Jam"
                    }
                } else {
                    binding.hasil.text = "hasil tidak ada"
                }

            }

            override fun onFailure(call: Call<List<ResponseModelBurnedCalories>>, t: Throwable) {
                binding.hasil.text = "KACAU API NYA"
            }
        })
    }
}
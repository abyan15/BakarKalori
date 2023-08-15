package com.abyan.bakarkalori

import android.text.Editable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface apiService {

    @GET("v1/caloriesburned")
    fun getExercises(@Query("activity") activity: Editable): Call<List<ResponseModelBurnedCalories>>


}
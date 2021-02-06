package com.dmishra.meteorsonearth.networkApi

import com.dmishra.meteorsonearth.model.MeteorData
import retrofit2.Call

import retrofit2.http.GET

interface MeteorApiInterface {
    @GET("resource/y77d-th95.json?\$where=year>='1900-01-01T00:00:00.000'")//&\$limit=15")
    fun getMeteorsJsonData(): Call<List<MeteorData>>
}

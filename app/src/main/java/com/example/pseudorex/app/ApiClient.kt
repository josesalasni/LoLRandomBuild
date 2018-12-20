package com.example.pseudorex.app

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


public class ApiClient {
    public var BASE_URL: String = "https://ddragon.leagueoflegends.com"
    public var retrofit: Retrofit? = null

    public fun getApiLibrary(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofit
    }
}
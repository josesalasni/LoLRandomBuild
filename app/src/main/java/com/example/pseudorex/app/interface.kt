package com.example.pseudorex.app


import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface INetworkAPI {

    @GET ("/realms/na.json")
    fun getVersion(): Call<Version>

    //@GET("/cdn/6.24.1/data/en_US/champion/Aatrox.json")
    //fun getOneChampion(): Call<Champion>

    @GET("/cdn/{version}/data/en_US/champion.json")
    fun getAllChampions(@Path("version"  ) version : String ): Call<JsonObject>

    @GET("/cdn/{version}/data/en_US/item.json")
    fun getAllItems(@Path("version"  ) version : String ): Call<JsonObject>



}
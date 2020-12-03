package edu.syr.smalltalk.service.android.http

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SmallTalkAPI {
    @Multipart
    @POST("upload/{path}")
    fun uploadFile(
        @Path("path") path: String,
        @Part file: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): Call<Void>

    companion object {
        operator fun invoke(): SmallTalkAPI {
            return Retrofit.Builder()
                .baseUrl("http://192.168.1.224:8079/")
                // .baseUrl("https://smalltalknow.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SmallTalkAPI::class.java)
        }
    }
}

data class UploadResponse (
    val error: Boolean,
    val message: String,
    val image: String
)

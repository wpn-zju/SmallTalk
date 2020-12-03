package edu.syr.smalltalk.service.android.http

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SmallTalkHttpAPI {
    @Multipart
    @POST
    fun uploadFile(
        @Part image: MultipartBody.Part,
        @Part ("id_unified") idUnified: Int,
        @Part ("file_name") fileName: String,
        @Part ("mime_type") mimeType: String
    ): Call<UploadResponse>

    companion object {
        operator fun invoke(): SmallTalkHttpAPI {
            return Retrofit.Builder()
                .baseUrl("https://smalltalknow.com/Upload/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SmallTalkHttpAPI::class.java)
        }
    }
}

data class UploadResponse (
    val error: Boolean,
    val message: String,
    val image: String)

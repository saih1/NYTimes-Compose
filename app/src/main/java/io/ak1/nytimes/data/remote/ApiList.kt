package io.ak1.nytimes.data.remote


import io.ak1.nytimes.BuildConfig
import io.ak1.nytimes.model.StoriesResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by akshay on 14,November,2020
 * akshay2211@github.io
 */
interface ApiList {
    //https://api.nytimes.com/svc/topstories/v2/arts.json?api-key=yourkey

    @GET("{section}.json")
    suspend fun getStories(
        @Path("section") section: String = "home",
        @Query("api-key") key: String = BuildConfig.API_KEY
    ): Response<StoriesResponse>

    companion object {
        private const val BASE_PATH = "https://api.nytimes.com/svc/topstories/v2/"

        fun createApiList(interceptor: HttpLoggingInterceptor): Retrofit {
            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit
                .Builder()
                .baseUrl(BASE_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }
}

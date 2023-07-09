package io.ak1.nytimes.di

import android.content.Context
import io.ak1.nytimes.BuildConfig
import io.ak1.nytimes.data.local.AppDatabase
import io.ak1.nytimes.data.remote.ApiList
import kotlinx.coroutines.Dispatchers
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import kotlin.coroutines.CoroutineContext


/**
 * Created by akshay on 14,November,2020
 * akshay2211@github.io
 */

/**
 * definitions for dependency injection for all selected classes
 */
fun getLogInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
    .apply {
        if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
        else setLevel(HttpLoggingInterceptor.Level.NONE)
    }

fun returnRetrofit(interceptor: HttpLoggingInterceptor): Retrofit =
    ApiList.createApiList(interceptor)

fun getApi(retrofit: Retrofit): ApiList = retrofit.create(ApiList::class.java)


fun getDb(context: Context): AppDatabase = AppDatabase.createDb(context)

fun getCoroutineContext(): CoroutineContext = Dispatchers.IO

package cn.liibang.pinoko.http

import cn.liibang.pinoko.data.DataStore
import cn.liibang.pinoko.ui.support.gson
import coil.intercept.Interceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class AuthException(override val message: String) : RuntimeException()

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor {
        val token = DataStore.getToken()
        val reqBuilder = it.request().newBuilder()
        if (token != null) {
            reqBuilder.addHeader("Authorization", token)
        }
        return@addInterceptor it.proceed(reqBuilder.build())
    }
    .addInterceptor {
        val request = it.request()
        val response = it.proceed(request)
        CoroutineScope(Dispatchers.Main).launch {
            if (response.code == 401) {
                throw AuthException("身份凭证失效，请重新登录")
            }
        }
        // 在这里处理Response
        // 比如读取Response的内容、添加Header等操作
        return@addInterceptor response
    }
    .connectTimeout(2, java.util.concurrent.TimeUnit.SECONDS) // 连接超时时间
    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // 读取超时时间
    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)  // 写入超时时间
//    .connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
    .build()

var retrofit = Retrofit.Builder()
    .baseUrl("http://192.168.71.109:9092/app/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .client(okHttpClient)
    .build()

val appApi = retrofit.create(AppApi::class.java)

data class ResultVO<T>(
    val code: Int,
    val message: String,
    val data: T,
) {
    fun isSuccessful() = code == 0
}


package com.example.lpiem.rickandmortyapp.Presenter

import android.content.Context
import com.example.lpiem.rickandmortyapp.Data.RetrofitCallTypes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopManager private constructor(private val context: Context) {

    private val loginAppManager = LoginAppManager.getInstance(context)

    companion object : SingletonHolder<ShopManager, Context>(::ShopManager)

    private fun <T> callRetrofit(call: Call<T>, type: RetrofitCallTypes) {

        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    when (type) {
                        /**
                        RetrofitCallTypes.SIGN_IN -> {
                        val responseFromApi = response.body() as ResponseFromApi
                        val code = responseFromApi.code
                        val message = responseFromApi.message
                        if (code == 200) {
                        //TODO : do stuff here
                        } else {
                        Toast.makeText(context, "code : $code, message : $message", Toast.LENGTH_SHORT).show()
                        }
                        }
                        else -> Log.d(TAG, "error : type does not exist")
                         */
                    }

                } else {
                    /**
                    TODO : handle API error here
                    val responseError = response.errorBody() as ResponseBody
                    Log.d(TAG, "error: " + responseError.string() )
                     */
                }

            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                /**
                 * TODO : handle error for call here
                Log.d(TAG, "fail : $t")
                 */
            }
        })

    }

}
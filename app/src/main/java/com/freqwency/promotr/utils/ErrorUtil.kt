package com.freqwency.promotr.utils

import android.content.Context
import android.util.Log
import android.view.View
import com.developer.kalert.KAlertDialog
import com.freqwency.promotr.R
import com.freqwency.promotr.model.ErrorBean


import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorUtil {
    var error: String = ""

    fun handlerGeneralError(context: Context?, throwable: Throwable): String {
        // No need to pass the view. i'll remove later

        if (context == null) return error

        when (throwable) {
            //For Display
            is ConnectException -> errorDialogs(
                context,
                context.getString(R.string.unable_to_connect)
            )
            is SocketTimeoutException -> errorDialogs(
                context,
                context.getString(R.string.unable_to_connect)
            )
            is UnknownHostException -> errorDialogs(
                context,
                context.getString(R.string.unable_to_connect)
            )
            is InternalError -> errorDialogs(context, context.getString(R.string.server_error))

            is HttpException -> {
                try {
                    when (throwable.code()) {
                        403 -> displayError(context, throwable)
                        400 -> displayError(context, throwable)
                        else -> displayError(context, throwable)
                    }
                } catch (exception: Exception) {
                }
            }
            else -> {
                Log.e("Other Exception", "Maybe wrong response model is set" + throwable.message)
            }
        }
        return error
    }

    fun displayError(context: Context, exception: HttpException): String {
        val errorBody = getGsonInstance().fromJson(
            exception.response()?.errorBody()?.charStream(),
            ErrorBean::class.java
        )
        try {
            error = errorBody.message
            KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                .setContentText(error)
                .confirmButtonColor(R.color.secondary)
                .setConfirmClickListener(
                    context.getString(R.string.OK),
                    R.color.secondary_text_color,
                    null
                )
                .show()
        } catch (e: Exception) {
            Log.e("Error Utils", e.message + "")
        }
        return errorBody.message
    }

    private fun errorDialogs(context: Context, str: String) {
        KAlertDialog(context, KAlertDialog.ERROR_TYPE)
            .setContentText(str)
            .confirmButtonColor(R.color.secondary)
            .setConfirmClickListener(
                context.getString(R.string.OK),
                R.color.secondary_text_color,
                null
            )
            .show()
    }
}
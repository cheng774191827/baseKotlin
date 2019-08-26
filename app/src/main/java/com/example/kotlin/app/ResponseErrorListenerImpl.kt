package com.example.kotlin.app


import android.content.Context
import android.widget.Toast

import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.jess.arms.utils.ArmsUtils

import org.json.JSONException

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener
import retrofit2.HttpException
import timber.log.Timber

class ResponseErrorListenerImpl : ResponseErrorListener {
    override fun handleResponseError(context: Context, t: Throwable) {
        Timber.tag("Catch-Error").w(t.message)
        //这里不光只能打印错误, 还可以根据不同的错误做出不同的逻辑处理
        //这里只是对几个常用错误进行简单的处理, 展示这个类的用法, 在实际开发中请您自行对更多错误进行更严谨的处理
        var msg = when(t){
             is UnknownHostException->"网络不可用"
             is SocketTimeoutException-> "请求网络超时"
             is HttpException->  convertStatusCode(t)
            is JsonParseException, is ParseException, is JSONException, is JsonIOException -> "数据解析错误"
            else ->"未知错误"
        }
        ArmsUtils.snackbarText(msg)
    }

    private fun convertStatusCode(httpException: HttpException): String {
        return when(httpException.code()){
            500->"服务器发生错误"
            404->"请求地址不存在"
            401->"账号身份过期，请重新登录"
            403->"请求被服务器拒绝"
            307->"请求被重定向到其他页面"
            else -> httpException.message()
        }
    }

}

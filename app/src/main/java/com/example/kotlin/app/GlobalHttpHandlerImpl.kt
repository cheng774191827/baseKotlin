package com.example.kotlin.app


import android.content.Context

import com.jess.arms.http.GlobalHttpHandler

import io.reactivex.annotations.NonNull
import io.reactivex.annotations.Nullable
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class GlobalHttpHandlerImpl(private val context: Context) : GlobalHttpHandler {

    /**
     * 这里可以先客户端一步拿到每一次 Http 请求的结果, 可以先解析成 Json, 再做一些操作, 如检测到 token 过期后
     * 重新请求 token, 并重新执行请求
     *
     * @param httpResult 服务器返回的结果 (已被框架自动转换为字符串)
     * @param chain      [Interceptor.Chain]
     * @param response   [Response]
     * @return [Response]
     */
    @NonNull
    override fun onHttpResultResponse(@Nullable httpResult: String?, @NonNull chain: Interceptor.Chain, @NonNull response: Response): Response {
        //        if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body().contentType())) {
        //            try {
        //                List<User> list = ArmsUtils.obtainAppComponentFromContext(context).gson().fromJson(httpResult, new TypeToken<List<User>>() {
        //                }.getType());
        //                User user = list.get(0);
        //                Timber.w("Result ------> " + user.getLogin() + "    ||   Avatar_url------> " + user.getAvatarUrl());
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //                return response;
        //            }
        //        }

        /* 这里如果发现 token 过期, 可以先请求最新的 token, 然后在拿新的 token 放入 Request 里去重新请求
        注意在这个回调之前已经调用过 proceed(), 所以这里必须自己去建立网络请求, 如使用 Okhttp 使用新的 Request 去请求
        create a new request and modify it accordingly using the new token
        Request newRequest = chain.request().newBuilder().header("token", newToken)
                             .build();
        retry the request
        response.body().close();
        如果使用 Okhttp 将新的请求, 请求成功后, 再将 Okhttp 返回的 Response return 出去即可
        如果不需要返回新的结果, 则直接把参数 response 返回出去即可*/
        return response
    }


    /**
     * 这里可以在请求服务器之前拿到 [Request], 做一些操作比如给 [Request] 统一添加 token 或者 header 以及参数加密等操作
     *
     * @param chain   [Interceptor.Chain]
     * @param request [Request]
     * @return [Request]
     */
    @NonNull
    override fun onHttpRequestBefore(@NonNull chain: Interceptor.Chain, @NonNull request: Request): Request {
        /* 如果需要在请求服务器之前做一些操作, 则重新构建一个做过操作的 Request 并 return, 如增加 Header、Params 等请求信息, 不做操作则直接返回参数 request
        return chain.request().newBuilder().header("token", tokenId)
                              .build(); */
        val builder = chain.request().newBuilder()
        val accessToken = ""
        //        if (null == user){
        //            accessToken = "Basic dnVlOnZ1ZQ==";
        //            builder.addHeader("grant_type", "authorization_code");
        //        } else {
        //            accessToken = "Bearer " + user.getAccess_token();
        //        }
        builder.addHeader("Authorization", accessToken)
        builder.addHeader("Content-Type", "application/json")
        return builder.build()
    }
}
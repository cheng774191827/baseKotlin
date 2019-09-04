package com.base.application.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

import com.jess.arms.BuildConfig
import com.jess.arms.base.delegate.AppLifecycles
import com.jess.arms.integration.cache.IntelligentCache
import com.jess.arms.utils.ArmsUtils
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

import butterknife.ButterKnife
import com.example.kotlin.R
import io.reactivex.annotations.NonNull
import timber.log.Timber

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AppLifecyclesImpl : AppLifecycles {
    override fun attachBaseContext(@NonNull base: Context) {
        //          MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
        //初始化推送
        instance = base
    }

    override fun onCreate(@NonNull application: Application) {

        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        if (BuildConfig.DEBUG) {//Timber初始化
            //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
            //并且支持添加多个日志框架(打印策略),做到外部调用一次 Api,内部却可以做到同时使用多个策略
            //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
            Timber.plant(Timber.DebugTree())
            // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
            //                    Logger.addLogAdapter(new AndroidLogAdapter());
            //                    Timber.plant(new Timber.DebugTree() {
            //                        @Override
            //                        protected void log(int priority, String tag, String message, Throwable t) {
            //                            Logger.log(priority, tag, message, t);
            //                        }
            //                    });
            ButterKnife.setDebug(true)
        }
        //LeakCanary 内存泄露检查
        //使用 IntelligentCache.KEY_KEEP 作为 key 的前缀, 可以使储存的数据永久存储在内存中
        //否则存储在 LRU 算法的存储空间中, 前提是 extras 使用的是 IntelligentCache (框架默认使用)
        ArmsUtils.obtainAppComponentFromContext(application).extras()
            .put(IntelligentCache.getKeyOfKeep(RefWatcher::class.java.name), if (BuildConfig.DEBUG) LeakCanary.install(application) else RefWatcher.DISABLED)
    }

    override fun onTerminate(@NonNull application: Application) {

    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        var instance: Context? = null
            private set

        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setDisableContentWhenLoading(false)
                layout.setDisableContentWhenRefresh(false)
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white)//全局设置主题颜色
                ClassicsHeader(context)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }
}

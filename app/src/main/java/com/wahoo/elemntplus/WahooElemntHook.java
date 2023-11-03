package com.wahoo.elemntplus;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

// xposed读取主模块配置,这里无法实现，要重写
class XposedConfig
{
    public static boolean getSearchRepair()
    {
        XSharedPreferences sp = new XSharedPreferences("com.wahoo.elemntplus", "config");
        Log.d("WahooElemntXposed","try to makeWorldReadable");
        if(sp.makeWorldReadable())
        {
            Log.d("WahooElemntXposed","makeWorldReadable success!");
            return sp.getBoolean("searchRepair",false);
        }
        else
        {
            Log.e("WahooElemntXposed","makeWorldReadable fail!");
            return false;
        }
    }
}

public class WahooElemntHook implements IXposedHookLoadPackage
{
    String TAG = "WahooElemntXposed";
    MoudleConfig config;
    Context mContext;


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable
    {
        // TODO: 添加替换搜索源功能（这样可以搜索到更多的地点，需要测试地点保存功能能不能用，但是导能能力还是来自于mapbox）
        // TODO: 添加修改返回及显示功能
        // TODO: 添加修改路径规划源功能（原app中用的是mapbox，可以改成高德或百度）
        // TODO: 添加修改mapbox的token功能（之前出现过token挂了导致不能正常搜索导航的情况）
        // TODO: 可以添加一个分应用通知同步
        // 通过拦截修改搜索导航相关的请求返回值可以免去模拟蓝牙协议的麻烦

        // 这里获取的是宿主应用的context，不是模块的，所以拿不到内容
        this.mContext = AndroidAppHelper.currentApplication().getApplicationContext();



        Log.d(TAG,lpparam.packageName);
        // 不知道为啥是webview
        if ("com.wahoofitness.boltcompanion".equals(lpparam.packageName) || "com.google.android.webview".equals(lpparam.packageName))
        {
            final Class<?> targetClass = lpparam.classLoader.loadClass("java.net.URL");

            // Hook URL的构造函数，修改url
            XposedHelpers.findAndHookConstructor("java.net.URL",lpparam.classLoader,String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(TAG, "java.net.URL.$init called");
                    String oldUrl = (String)param.args[0];
                    Log.d(TAG,oldUrl);
                    Log.d(TAG, "URL: "+String.valueOf(oldUrl.startsWith("https://api.mapbox.com/geocoding/v5/mapbox.places")));
                    //Log.d(TAG,"SWITCH: "+String.valueOf(XposedConfig.getSearchRepair()));

                    // 这里要添加配置选项功能
                    if (oldUrl.startsWith("https://api.mapbox.com/geocoding/v5/mapbox.places"))
                    {
                        // 替换搜索类型，添加poi
                        oldUrl = oldUrl.replace("place%2Caddress%2Cpostcode","place%2Caddress%2Cpostcode%2Cpoi%2Cdistrict%2Clocality%2Cneighborhood");
                        // 添加搜索结果,没用，还是十个，可能是界面限制
                        // oldUrl = oldUrl.replace("&limit=10","&limit=25");
                        param.args[0] = oldUrl;
                        Log.d(TAG,"url replaced：" + oldUrl);
                    }

                };
            });


            // hook java.net.URLConnection.getInputStream函数，修改返回内容
            // 代码来自gpt4，需要验证是否可行
            XposedHelpers.findAndHookMethod("java.net.HttpURLConnection", lpparam.classLoader, "getInputStream", new XC_MethodHook() {


                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    InputStream original = (InputStream) param.getResult();

                    InputStream modified = new InputStream() {
                        @Override
                        public int read() throws IOException {
                            int data = original.read();
                            // 在这里修改data
                            return data;
                        }
                    };

                    param.setResult(modified);
                }
            });

        }


    }

}

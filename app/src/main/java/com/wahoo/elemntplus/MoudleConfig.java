package com.wahoo.elemntplus;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

public class MoudleConfig {
    private final String SPName = "config";
    private final Context mContext;



    public MoudleConfig(Context context)
    {
        this.mContext = context;
    }

    public void writeConfig(String configName,String value)
    {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(SPName, Context.MODE_WORLD_READABLE).edit();
            editor.putString(configName, value);
            editor.apply();

    }

    public void writeConfig(String configName,boolean value)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SPName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(configName, value);
        editor.apply();
    }

    public void writeStringSet(String configName, Set value)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SPName, Context.MODE_PRIVATE).edit();
        editor.putStringSet(configName,value);
        editor.apply();
    }

    public String readConfig(String configName, String defaultValue)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(SPName, Context.MODE_PRIVATE);
        return prefs.getString(configName, defaultValue);
    }

    public boolean readConfig(String configName, boolean defaultValue)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(SPName, Context.MODE_PRIVATE);
        return prefs.getBoolean(configName, defaultValue);
    }

    public void setSearchRepair(boolean flag)
    {
        this.writeConfig("searchRepair",flag);
    }

    public boolean getSearchRepair()
    {
        return this.readConfig("searchRepair",false);
    }


}

package com.wahoo.elemntplus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashSet;
import java.util.Set;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivity extends AppCompatActivity
{
    MoudleConfig config;
    Switch searchRepair;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = new MoudleConfig(getApplicationContext());

        // 初始化控件
        searchRepair = findViewById(R.id.searchRepairSwitch);

        // 设置默认状态
        this.setDetailConfig();

        // 监听状态修改，持久化存储
        searchRepair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                config.setSearchRepair(isChecked);
            }
        });


    }

    private void setDetailConfig()
    {
        // 设置默认搜索修复状态
        boolean searchRepair = this.config.getSearchRepair();
        this.searchRepair.setChecked(searchRepair);

    }

}
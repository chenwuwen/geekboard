package cn.kanyun.geekboard;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public static Application getInstance(){
        if(mInstance == null){
            mInstance = new MyApplication();
        }
        return mInstance;
    }
}

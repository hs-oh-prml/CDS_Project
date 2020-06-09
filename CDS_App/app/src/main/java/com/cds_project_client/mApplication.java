package com.cds_project_client;

import android.app.Application;
import android.content.Context;

import com.cds_project_client.util.CMClient;

public class mApplication extends Application {
    public CMClient cmClient;
    public void initCM(Context c){
        cmClient = new CMClient(c);
        cmClient.initCM();
    }
}

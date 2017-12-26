package com.sms.smart.azhar.bulksms.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sms.smart.azhar.bulksms.Utility.AppController;


/**
 * Created by Nanosoft-Android on 11/19/2017.
 */

public class NetworkConnectionReceiver extends BroadcastReceiver {

    public static ConnectivityRecieverListener connectivityRecieverListener;
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(connectivityRecieverListener !=null){
            connectivityRecieverListener.OnNetworkChange(isConnected);
        }

    }

    public static boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) AppController.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }


    public interface ConnectivityRecieverListener{
        public void OnNetworkChange(boolean inConnected);
    }
}

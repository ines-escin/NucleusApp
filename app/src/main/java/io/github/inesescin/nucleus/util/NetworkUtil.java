package io.github.inesescin.nucleus.util;

/**
 * Created by HERBERTT on 29/08/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public final class NetworkUtil extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        isNetworkAvailable(context);
    }


    public static boolean hasIntenetConnection(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connectionManager.getActiveNetworkInfo() != null && connectionManager.getActiveNetworkInfo().isAvailable() && connectionManager.getActiveNetworkInfo().isConnected());
    }

    public static boolean isNetworkAvailable(Context context) {
        String ssid = null;
        if(checkPermissions(context, "android.permission.INTERNET")){
            ConnectivityManager cManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();

            if (info != null && info.isAvailable()){

                final WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null) {
                    ssid = connectionInfo.getSSID().replace("\"","");
                    if (ssid.equals("CINGUESTS")||
                            ssid.equals("CINUFPE")||
                            ssid.equals("TesteArduino")||
                            ssid.equals("Cin-GUESTS2")){
                        Constants.setFiwareAddress("IN");
                    }else {
                        Constants.setFiwareAddress("OUT");
                    }
                }

                return true;
            }else{


                return false;
            }

        }else{


            return false;
        }


    }

    public static boolean checkPhoneState(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.checkPermission("android.permission.READ_PHONE_STATE", context
                .getPackageName()) != 0) {
            return false;
        }
        return true;
    }

    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }



}

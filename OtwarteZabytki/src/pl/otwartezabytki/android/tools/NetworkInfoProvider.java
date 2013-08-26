package pl.otwartezabytki.android.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkInfoProvider {

	private static NetworkInfoProvider instance = new NetworkInfoProvider();
    static Context context;
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;
    
    public static NetworkInfoProvider getInstance(Context ctx) {
        context = ctx;
        return instance;
    }

    public boolean isOnline(Context ctx) {
        try {
            connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);	
	        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	        connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
	        return connected;
        } catch (Exception e) {
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
    
    public boolean hasWifi(Context ctx){
    	connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);	
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo ni : networkInfos) {
            if (ni.getType()==ConnectivityManager.TYPE_WIFI)
                if (ni.isConnected())
                    return true;
        }
        return false;
    }
}

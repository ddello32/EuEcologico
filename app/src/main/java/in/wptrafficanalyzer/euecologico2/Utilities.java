package in.wptrafficanalyzer.euecologico2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.google.android.gms.maps.model.Marker;
import org.apache.http.message.BasicNameValuePair;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utilities {

    static String getMacAddress(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static boolean containsAnyTrues(boolean[] booleans) {
        for (boolean i : booleans) {
            if (i) {
                return true;
            }
        }
        return false;
    }

    public static double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static NameValuePairsList buildNameValuePairsList(int category, Position position, String id) {
        NameValuePairsList nameValuePairs = new NameValuePairsList();
        nameValuePairs.add(new BasicNameValuePair("category", Integer.toString(category)));
        nameValuePairs.add(new BasicNameValuePair("lat", position.getFirst()));
        nameValuePairs.add(new BasicNameValuePair("lng", position.getSecond()));
        nameValuePairs.add(new BasicNameValuePair("id", id));
        return nameValuePairs;
    }

    static int getCategory(Marker marker, Category[] categories) {
        int i = 0;
        while (!categories[i].getTitle().equals(marker.getTitle())) {
            i++;
        }
        return i;
    }
}

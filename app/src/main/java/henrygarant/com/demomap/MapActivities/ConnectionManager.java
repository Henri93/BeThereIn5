package henrygarant.com.demomap.MapActivities;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConnectionManager {

    private Context context;

    public ConnectionManager(Context context) {
        this.context = context;
    }

    public void setConnected(boolean connected) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isConnected", connected);
        editor.commit();
    }

    public boolean isConnected() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPref.getBoolean("isConnected", false);
    }
}

package henrygarant.com.demomap.GcmServices;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import henrygarant.com.demomap.Config;
import henrygarant.com.demomap.MainActivity;

public class GcmRegister{

    GoogleCloudMessaging gcm;
    String regId;

    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";


    public String registerGCM(Context context) {

        gcm = GoogleCloudMessaging.getInstance(context);
        regId = getRegistrationId(context);

        if (regId.isEmpty()) {

            registerInBackground(context);

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);

        } else {
            Toast.makeText(context,
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();
            shareRegId(context, regId);
        }
        return regId;
    }

    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }

    private void registerInBackground(final Context context) {
        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_NUMBER);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);
                    shareRegId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }


            protected void onPostExecute(String msg) {
                Toast.makeText(context,
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
            }
        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = context.getSharedPreferences(
                MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    private void shareRegId(final Context context, final String regId){
        //Share regId with server
        final ShareExternalServer appUtil = new ShareExternalServer();
        final AsyncTask shareRegidTask = new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String result = appUtil.shareRegIdWithAppServer(context, regId);
                return result;
            }
        };
        shareRegidTask.execute(null, null, null);
    }


}

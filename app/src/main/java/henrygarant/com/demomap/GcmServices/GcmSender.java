package henrygarant.com.demomap.GcmServices;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import henrygarant.com.demomap.Config;

public class GcmSender {

    private Context context;

    public GcmSender(Context context){
        this.context = context;
    }

    public void sendGcmMessage(String message){
        new SendGcmMessaage().execute(message);
    }

    private class SendGcmMessaage extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result){
            Toast.makeText(context.getApplicationContext(), "Sent Post Request", Toast.LENGTH_LONG).show();
        }

    }

    public void postData(String valueIWantToSend) {
        HttpClient httpclient = new DefaultHttpClient();
        // specify the URL you want to post to
        HttpPost httppost = new HttpPost(Config.APP_SERVER_MESSAGE);
        httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try {
            // create a list to store HTTP variables and their values
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            // add an HTTP variable and value pair
            nameValuePairs.add(new BasicNameValuePair("message", valueIWantToSend));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // send the variable and value, in other words post, to the URL
            HttpResponse response = httpclient.execute(httppost);
            Log.d("HTTP RESPONSE: ", EntityUtils.toString(response.getEntity()));
        } catch (ClientProtocolException e) {
            // process execption
        } catch (IOException e) {
            // process execption
        }
    }
}
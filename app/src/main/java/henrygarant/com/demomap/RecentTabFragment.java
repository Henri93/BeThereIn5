package henrygarant.com.demomap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class RecentTabFragment extends Fragment {

    EditText sendPhone;
    EditText sendText;
    Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendPhone = (EditText)container.findViewById(R.id.sendPhone);
        sendText = (EditText)container.findViewById(R.id.sendText);
        sendButton = (Button)container.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Send Message", Toast.LENGTH_SHORT).show();
                //TODO Connect to Message Page and Push the information into form and send
                new SendGcmMessaage().execute(sendText.getText().toString());
            }
        });

        return inflater.inflate(R.layout.recent, container, false);
    }

    private class SendGcmMessaage extends AsyncTask<String, Integer, Double> {
        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        protected void onPostExecute(Double result){
            Toast.makeText(getActivity().getApplicationContext(), "Sent Post Request", Toast.LENGTH_LONG).show();
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
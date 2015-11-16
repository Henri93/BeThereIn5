package henrygarant.com.demomap;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity{

    private Button searchButton;
    private AutoCompleteTextView addressBar;
    private ArrayList<String> addressList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String query;
    //private SearchAddress searchAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getActionBar().setHomeButtonEnabled(true);

        addressList.add("3030 Magee Ave");

        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Exo-Regular.otf");

        searchButton = (Button)findViewById(R.id.searchButton);
        addressBar = (AutoCompleteTextView)findViewById(R.id.addressBar);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, addressList);
        adapter.setNotifyOnChange(true);
        addressBar.setThreshold(2);
        addressBar.setAdapter(adapter);

        //searchAddress = new SearchAddress();
        /*addressBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((addressBar).isPerformingCompletion()) {return;}
                if (s.length() < 2) {
                    return;
                }else{
                    query = s.toString();
                    if (searchAddress.getStatus().equals(AsyncTask.Status.FINISHED)){
                        searchAddress = new SearchAddress();
                        searchAddress.execute(query);
                        Log.d("ASYNC", "FINISH GOOD");
                        Log.d("ASYNC", query);
                    }else{
                        Log.d("ASYNC", "CANCEL");
                        searchAddress.cancel(false);
                        searchAddress = new SearchAddress();
                        searchAddress.execute(query);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.notifyDataSetChanged();
            }
        });*/


        searchButton.setTypeface(tf, Typeface.BOLD);
        addressBar.setTypeface(tf);
    }


    public void calculateLocation(View view)
    {
        new AlertDialog.Builder(this)
                .setTitle("Set Notification")
                .setMessage("Do you want to be reminded when they are within 5 minutes?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //TODO SET REMINDER HERE
                        String address = addressBar.getText().toString().trim();
                        moveToActivity(MapsActivity.class, address);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void moveToActivity(Class newClass, String destination){
        Intent intent = new Intent(this, newClass);
        intent.putExtra("destination", destination);
        startActivity(intent);
    }



    /*private ArrayList<String> getAddressInfo(Context context, String locationName){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> a = geocoder.getFromLocationName(locationName, 5);

            if(a != null) {

                for (int i = 0; i < a.size(); i++) {
                    //String city = a.get(i).getLocality();
                    //String country = a.get(i).getCountryName();
                    Address address = a.get(i);
                    addressList.add(address.toString());
                }
            }
            return addressList;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class SearchAddress extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            addressList.clear();
            ArrayList<String> addressArray = getAddressInfo(getApplicationContext(), query);
            return addressArray;
        }

        @Override
        protected void onPostExecute(ArrayList<String> addressArray) {
            if(addressArray == null)
                Toast.makeText(getApplicationContext(), "No address obtained from server", Toast.LENGTH_SHORT).show();
            else{
                adapter.clear();
                for(String address : addressArray){
                    adapter.add(address);
                }
            }
        }

        @Override
        protected void onPreExecute() {}
        @Override
        protected void onProgressUpdate(Void... values) {}
    }
    */
}

package henrygarant.com.demomap;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainTabFragment extends Fragment {

    ExpandableListView contactList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_new, container,
                false);
        contactList = (ExpandableListView)rootview.findViewById(R.id.contactList);
        TextView selectTextView = (TextView) rootview.findViewById(R.id.selectTextView);
        Typeface tf = Typeface.createFromAsset(rootview.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
        selectTextView.setTypeface(tf);
        ContentResolver cr = getActivity().getContentResolver();
        ArrayList<Contact> contactsList = new ArrayList<Contact>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String contact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //TODO MAKE SURE THAT IMAGE IS CORRECT
            String image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            Bitmap bitmap = null;
            if (image_uri != null) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), Uri.parse(image_uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            contactsList.add(new Contact(contact, phoneNumber, bitmap));
        }
        BaseExpandableListAdapter contactAdapter = new ContactAdapter(getActivity().getApplicationContext(), contactsList);
        contactList.setAdapter(contactAdapter);
        Log.d("BREAKPOINT: ", "Made it 1");
        return rootview;
    }
}

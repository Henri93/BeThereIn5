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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainTabFragment extends Fragment implements
        SearchView.OnQueryTextListener {

    private ExpandableListView contactList;
    private SearchView searchView;
    private BaseExpandableListAdapter contactAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_new, container,
                false);
        contactList = (ExpandableListView)rootview.findViewById(R.id.contactList);
        searchView = (SearchView) rootview.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
        TextView selectTextView = (TextView) rootview.findViewById(R.id.selectTextView);
        Typeface tf = Typeface.createFromAsset(rootview.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
        selectTextView.setTypeface(tf);

        contactAdapter = new ContactAdapter(getActivity().getApplicationContext(), getContacts());
        contactList.setAdapter(contactAdapter);
        return rootview;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filter(query);
        return false;
    }

    public void filter(String query) {
        query = query.toLowerCase();
        if (!query.isEmpty()) {
            ArrayList<Contact> searchedContacts = new ArrayList<Contact>();
            for (Contact contact : getContacts()) {
                if (contact.getName().toLowerCase().contains(query)) {
                    searchedContacts.add(contact);
                }
            }
            contactAdapter = new ContactAdapter(getActivity().getApplicationContext(), searchedContacts);
        } else {
            contactAdapter = new ContactAdapter(getActivity().getApplicationContext(), getContacts());
        }

        contactList.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }

    private ArrayList<Contact> getContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        ArrayList<Contact> contactsList = new ArrayList<Contact>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String contact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            Bitmap bitmap = null;
            if (image_uri != null && !image_uri.equals("")) {
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
        phones.close();
        return contactsList;
    }
}

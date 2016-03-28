package henrygarant.com.demomap;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class NewTabFragment extends Fragment {

    ExpandableListView contactList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.tab_new, container,
                false);
        contactList = (ExpandableListView)rootview.findViewById(R.id.contactList);
        ContentResolver cr = getActivity().getContentResolver();
        ArrayList<Contact> contactsList = new ArrayList<Contact>();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {
            String contact = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactsList.add(new Contact(contact, phoneNumber));
        }
        ArrayAdapter<Contact> contactAdapter = new ContactAdapter(getActivity(), contactsList);
        contactList.setAdapter(contactAdapter);
        return rootview;
    }
}

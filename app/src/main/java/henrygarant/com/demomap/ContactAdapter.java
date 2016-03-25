package henrygarant.com.demomap;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0 , contacts);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact contact = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_adapter, parent, false);
        }

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/OpenSans-Light.ttf");
        // Lookup view for data population
        TextView contactName = (TextView) convertView.findViewById(R.id.contactName);
        TextView contactNumber = (TextView) convertView.findViewById(R.id.contactNumber);
        // Populate the data into the template view using the data object
        contactName.setTypeface(font);
        contactName.setTypeface(font);

        contactName.setText(contact.getName());
        contactNumber.setText(contact.getNumber());
        // Return the completed view to render on screen
        return convertView;
    }
}

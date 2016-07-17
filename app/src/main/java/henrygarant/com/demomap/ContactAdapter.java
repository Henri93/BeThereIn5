package henrygarant.com.demomap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import henrygarant.com.demomap.MapActivities.WaitingPage;

public class ContactAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<Contact> _listDataHeader; // header titles

    public ContactAdapter(Context context, ArrayList<Contact> listDataHeader) {
        this._context = context;
        this._listDataHeader = listDataHeader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expanded_item, null);
        }

        Button expandedButton= (Button) convertView
                .findViewById(R.id.expanded_list_button);

        expandedButton.setFocusable(false);

        expandedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = getGroup(groupPosition).toString().substring(getGroup(groupPosition).toString().lastIndexOf(':') + 1);
                number = number.substring(1);
                Log.d("EXPANDEDLISTVIEW: ", "Button Click at " + number);
                //Start The new activity here
                Intent intent = new Intent(_context , WaitingPage.class);
                intent.putExtra("phoneto", number);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(intent);


            }
        });
        //set onclick here
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Contact theContact = (Contact) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.contact_adapter, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.contactName);
        lblListHeader.setText(theContact.getName());

        TextView lblListSubHeader = (TextView) convertView
                .findViewById(R.id.contactNumber);
        lblListSubHeader.setText(theContact.getNumber());

        ImageView lblImage = (ImageView) convertView.findViewById(R.id.contactImage);
        lblImage.setImageBitmap(theContact.getImage());


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}

    /*public ContactAdapter(Context context, ArrayList<Contact> contacts) {
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
    }*/


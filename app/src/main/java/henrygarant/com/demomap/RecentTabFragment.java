package henrygarant.com.demomap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import henrygarant.com.demomap.GcmServices.GcmSender;

public class RecentTabFragment extends Fragment {

    EditText sendPhone;
    EditText sendText;
    Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send, container, false);
        sendPhone = (EditText)view.findViewById(R.id.sendPhone);
        sendText = (EditText)view.findViewById(R.id.sendText);
        sendButton = (Button)view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Send Message", Toast.LENGTH_SHORT).show();
                GcmSender gcmSender = new GcmSender(getActivity().getApplicationContext());
                gcmSender.sendGcmAccept(sendPhone.getText().toString(), sendText.getText().toString(), "1", "0");
                sendText.setText("");
            }
        });

        return view;
    }
}
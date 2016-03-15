package henrygarant.com.demomap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RecentTabFragment extends Fragment {

    EditText sendPhone;
    EditText sendText;
    Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sendPhone = (EditText)container.findViewById(R.id.sendPhone);
        sendText = (EditText)container.findViewById(R.id.sendText);
        sendButton = (Button)container.findViewById(R.id.sendButton);
        return inflater.inflate(R.layout.recent, container, false);
    }


}
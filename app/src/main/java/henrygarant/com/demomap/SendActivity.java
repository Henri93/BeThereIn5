package henrygarant.com.demomap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import henrygarant.com.demomap.MapActivities.WaitingPage;

public class SendActivity extends ActionBarActivity {

    EditText phoneEditText;
    LinearLayout sendLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);
        phoneEditText = (EditText) findViewById(R.id.sendPhone);
        phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        sendLayout = (LinearLayout) findViewById(R.id.sendLayout);
    }

    public void sendNewButtonClick(View v) {
        String phone = phoneEditText.getText().toString();
        if (!phone.isEmpty() && Patterns.PHONE.matcher(phone).matches() && phone.length() >= 7 && phone.length() <= 16) {
            phoneEditText.setText("");
            Intent intent = new Intent(this, WaitingPage.class);
            intent.putExtra("phoneto", phone);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        } else {
            //error shake
            Animation shake = AnimationUtils.loadAnimation(getBaseContext(), R.anim.shake);
            sendLayout.startAnimation(shake);
            Toast.makeText(getApplicationContext(),
                    "Please enter a valid phone number!", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
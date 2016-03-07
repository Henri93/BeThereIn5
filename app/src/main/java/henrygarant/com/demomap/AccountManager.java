package henrygarant.com.demomap;

public abstract class AccountManager {

    // Server user login url
    public static String URL_LOGIN = "http://betherein5.eu.pn/android/Login.php";

    // Server user register url
    public static String URL_REGISTER = "http://betherein5.eu.pn/android/Register.php";

    // Change this to the project id from your API project created at
    // code.google.com, as shown in the url of your project.
    public static final String SENDER_ID = null;
    // Change this to match your server.
    public static final String SERVER_URL = null;

    public static final String TAG = "henrygarant.com.demomap";

    public static final String ACTION_ON_REGISTERED
            = "henryrgarant.com.demomap.RegisterActivity";

    public static final String FIELD_REGISTRATION_ID = "registration_id";
    public static final String FIELD_MESSAGE = "msg";

}

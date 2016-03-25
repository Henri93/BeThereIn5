package henrygarant.com.demomap;

public interface Config {

    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "http://betherein5.byethost9.com/message.php?shareRegId=1";
    static final String APP_SERVER_MESSAGE = "http://betherein5.byethost9.com/message.php";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "betherein5-1231";
    static final String GOOGLE_PROJECT_NUMBER = "874756675313";
    static final String MESSAGE_KEY = "m";

    //TODO CHANGE BACKEND MYSQL TO NEW SERVER

    // Server user login url
    public static String URL_LOGIN = "http://betherein5.byethost9.com/android/Login.php";

    // Server user register url
    public static String URL_REGISTER = "http://betherein5.byethost9.com/android/Register.php";


    public static final String TAG = "henrygarant.com.demomap";

    public static final String ACTION_ON_REGISTERED
            = "henryrgarant.com.demomap.RegisterActivity";

    public static final String FIELD_REGISTRATION_ID = "registration_id";
    public static final String FIELD_MESSAGE = "msg";

}
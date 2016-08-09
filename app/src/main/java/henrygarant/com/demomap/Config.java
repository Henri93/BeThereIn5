package henrygarant.com.demomap;

public interface Config {

    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "http://www.betherein5.net/android/message.php?shareRegId=1";
    static final String APP_SERVER_MESSAGE = "http://www.betherein5.net/android/message.php";
    static final String APP_SERVER_ACCEPT = "http://www.betherein5.net/android/accept.php";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "betherein5-1231";
    static final String GOOGLE_PROJECT_NUMBER = "874756675313";
    static final String PHONEFROM_KEY = "phonefrom";
    static final String PHONETO_KEY = "phoneto";
    static final String SENDER_KEY = "sender";
    static final String MESSAGE_KEY = "m";
    static final String ERROR_KEY = "error";
    static final String ACCEPT_START_KEY = "as";
    static final String ACCEPT_END_KEY = "ae";


    // Server user login url
    public static String URL_LOGIN = "http://www.betherein5.net/android/Login.php";

    // Server user register url
    public static String URL_REGISTER = "http://www.betherein5.net/android/Register.php";

    public static final String TAG = "henrygarant.com.demomap";

    public static final String ACTION_ON_REGISTERED
            = "henryrgarant.com.demomap.RegisterActivity";

    public static final String ACTION_STOP
            = "henryrgarant.com.demomap.MyLocationService.stop";
    public static final String ACTION_START
            = "henryrgarant.com.demomap.MyLocationService.start";

    public static final String FIELD_REGISTRATION_ID = "registration_id";
    public static final String FIELD_MESSAGE = "msg";


}
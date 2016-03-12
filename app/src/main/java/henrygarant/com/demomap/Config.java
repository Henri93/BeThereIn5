package henrygarant.com.demomap;

public interface Config {

    // used to share GCM regId with application server - using php app server
    static final String APP_SERVER_URL = "http://www.betherein5.eu.pn/register.php?shareRegId=1";

    // GCM server using java
    // static final String APP_SERVER_URL =
    // "http://192.168.1.17:8080/GCM-App-Server/GCMNotification?shareRegId=1";

    // Google Project Number
    static final String GOOGLE_PROJECT_ID = "betherein5-1231";
    static final String GOOGLE_PROJECT_NUMBER = "874756675313";
    static final String MESSAGE_KEY = "message";

}
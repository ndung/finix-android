package id.co.icg.reload.util;

public class Static {

    public final static String MyPref = "ireload_pref";

    public final static String PORT = ":7000";
    public final static String IP = "172.100.6.196"+PORT;
    public final static String APPS = "/api/v1/";
    public final static String BASE_URL = "http://"+IP+APPS;

    public final static String DIALOG_PLEASEWAIT_TITLE = "Loading...";
    public final static String REQUIRED = "  Can't Empty !";
    public final static String SOMETHING_WRONG = "Failed request, check your connection";

    public final static String READ_INBOX = "READ_INBOX";
    public final static String LOGIN_KEY = "LOGIN_KEY";
    public final static String TOKEN = "TOKEN_KEY";
    public final static String PUBLIC_KEY = "PUBLIC_KEY";
    public final static String USER_DATA = "USER_DATA";

    // flag to identify whether to show single line
    // or multi line test push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
}

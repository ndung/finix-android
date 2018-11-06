package id.co.icg.reload.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Global {
    public static final String FINGERPRINT_KEYSTORE = "AndroidKeyStore";
    public static final String FINGERPRINT_KEY = "iReload_fingerprint_key";
    public static final String BASE_URL = "https://mob.icg.co.id/mtg/";
    public static final String API_URL = "https://mob.icg.co.id/mtg/api";
    public static final String SERVER_URL = BASE_URL+"corereceiver";
    public static final String IMAGE_URL = "https://mob.icg.co.id/img/";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static final String appLinkUrl="https://fb.me/350567611961933";
    public static final String previewImageUrl="https://mob.icg.co.id/img/iReload.jpg";
    public static final String FILE_UPLOAD_URL = BASE_URL+"upload";
    public static final String NEWS_FEED_URL = BASE_URL+"newsFeed";
    public static final String SURVEY_URL = BASE_URL+"survey";
    public static final String CHART_URL = BASE_URL+"chart";
    public static final String REDEEM_URL = BASE_URL+"redeem";
    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "iReload";
    public static final String REFEREAL_URL = "https://ireload.icg.co.id/invite.html?referral=";
    public static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";

    public static String LAST_PAGE_TYPE = "";
    public static String LAST_TRANS_ID = "";
    public static String LAST_KEY = "";
    public static String LAST_CODE = "";
    public static String PLATFORM_ID = "";
    public static String USER_ID = "";
    public static String USER_NAME = "";
    public static String BALANCE = "";
    public static String MARQUEE = "";
    public static String ERROR = "";
    public static String APP_VERSION = "";
    public static String LOGIN_USER = "";
    public static Boolean FINGERPRINT_ACCESS = false;

    public static List<String> tselPrefixes = Arrays.asList("811", "812", "813", "821", "822", "823", "851", "852", "853");
    public static List<String> isatPrefixes = Arrays.asList("855", "856", "857", "858", "814", "815", "816");
    public static List<String> xlPrefixes = Arrays.asList("817", "818", "819", "859", "877", "878", "831", "832", "833", "838");
    public static List<String> axisPrefixes = Arrays.asList("831", "832", "833", "838");
    public static List<String> threePrefixes = Arrays.asList("895", "896", "897", "898", "899");
    public static List<String> smartfrenPrefixes = Arrays.asList("881", "882", "883", "884", "885", "886", "887", "888", "889");
    public static List<String> boltPrefixes = Arrays.asList("099", "998", "999");

}

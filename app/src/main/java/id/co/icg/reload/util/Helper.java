package id.co.icg.reload.util;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import id.co.icg.reload.App;

public class Helper {
    public static Map<String,String> translateParameters(final Map<String, String> params, boolean urlEncoded) {
        Map<String,String> newParams = new HashMap<String,String>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            try {
                if (key!=null && entry.getValue()!=null) {

                    String encryptedKey = Base64.encodeToString(entry.getKey().getBytes(), Base64.NO_WRAP);
                    String encryptedValue = Helper.encrypt(entry.getValue());

                    if (urlEncoded){
                        encryptedKey = URLEncoder.encode(encryptedKey, "UTF-8");
                        encryptedValue = URLEncoder.encode(encryptedValue, "UTF-8");
                    }
                    newParams.put(encryptedKey, encryptedValue);
                }
            } catch (Exception e) {
                Log.e("encrypt", entry.getKey() + " -> " + entry.getValue(), e);
            }
        }
        return newParams;
    }

    public static String translateParamsToQuery(final Map<String, String> params) {
        StringBuilder res = new StringBuilder();
        Map<String,String> newParams = translateParameters(params, true);
        for (Map.Entry<String, String> entry : newParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                if (key!=null && value!=null) {
                    res.append("&").append(key).append("=").append(value);
                }
            } catch (Exception e) {
                Log.e("encrypt", entry.getKey() + " -> " + entry.getValue(), e);
            }
        }
        return res.toString().substring(1);
    }

    public static String encrypt(String text) {

        String res = "-";
        try {
            byte[] key = App.getInstance().getPreferencesManager().getEncryptionKey().getBytes("UTF-8");// 32 bytes
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

            byte[] iv = "86ipndw356yj79rf".getBytes(); // 16 bytes
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();

            byte[] dataBytes = text.getBytes("UTF-8");
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            res = Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            res = e.getMessage();
        } catch (NoSuchAlgorithmException e) {
            res = e.getMessage();
        } catch (NoSuchPaddingException e) {
            res = e.getMessage();
        } catch (BadPaddingException e) {
            res = e.getMessage();
        } catch (IllegalBlockSizeException e) {
            res = e.getMessage();
        } catch (InvalidKeyException e) {
            res = e.getMessage();
        } catch (InvalidAlgorithmParameterException e) {
            res = e.getMessage();
        }
        return res;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

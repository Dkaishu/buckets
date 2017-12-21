package com.dkaishu.bucketsofgoogle.main.main;

import android.util.Base64;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dks on 2017/12/20.
 */

public class BucketUtils {


    public static String getURL(String baseUrl) {
//        String baseUrl = "http://p18i0dv0b.bkt.clouddn.com/info/new%201.txt";

        long expires = 3000;

        long deadline = System.currentTimeMillis() / 1000 + expires;

        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        int pos = baseUrl.indexOf("?");
        if (pos > 0) {
            url.append("&e=");
        } else {
            url.append("?e=");
        }
        url.append(deadline);
        String token = sign(utf8Bytes(url.toString()));
        url.append("&token=");
        url.append(token);
        return url.toString();
    }


    private static String mSecretKey = "RkHWcgrcQN8Bz-b14vcF0pHPPFRHeMUOylLhaR_w";//
    private static String mAccessKey = "bK06U0czlZu2blt5LFzI_Fu5_f3teekl2iGMdCl8";

    private static String sign(byte[] data) {
        Mac mac = createMac(mSecretKey);
        String encodedSign = //UrlSafeBase64.encodeToString(mac.doFinal(data));
        Base64.encodeToString(mac.doFinal(data), Base64.URL_SAFE | Base64.NO_WRAP);
        return mAccessKey + ":" + encodedSign;
    }


    private static Mac createMac(String secretKey) {
        byte[] sk = utf8Bytes(secretKey);
        SecretKeySpec secretKeySpec = new SecretKeySpec(sk, "HmacSHA1");


        Mac mac;
        try {
            mac = javax.crypto.Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return mac;
    }

    public static byte[] utf8Bytes(String data) {
        return data.getBytes(Charset.forName("UTF-8"));
    }
}

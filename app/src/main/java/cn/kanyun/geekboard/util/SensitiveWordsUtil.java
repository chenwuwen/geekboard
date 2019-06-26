package cn.kanyun.geekboard.util;

import com.google.common.base.Joiner;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.utils.URIBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SensitiveWordsUtil {

    public static final String url = "http://domain-intl.aliyuncs.com/";
    public static final String ENCODING = "UTF-8";
    public static final String AccessKeySecret = "OJ12wsP6lS4WZpdVbLEXugKbPeDsXB";


    public static void check(String text) {

        try {
//            HttpClient httpClient = HttpClients.createDefault();
            Map map = new LinkedHashMap();
            map.put("Action", "FuzzyMatchDomainSensitiveWord");
            map.put("Keyword", text);
            map.put("Format", "JSON");
            map.put("Version", "2018-11-16");
            map.put("AccessKeyId", "LTAIo9jnP4WB6KJD");
//            map.put("Signature", );
            map.put("SignatureMethod", "HMAC-SHA1");
            map.put("Timestamp", LocalDateTime.now().toString());
            map.put("SignatureVersion", "1.0");
            map.put("SignatureNonce", String.valueOf(UUID.randomUUID()));


            String middleStringToSign = mapToFormData(map,false);
            System.out.println(middleStringToSign);
            String stringToSign = "GET"+"&"+percentEncode("/") + "&" +percentEncode(middleStringToSign);
            System.out.println(stringToSign);
            String signature = hmacSha1(stringToSign, AccessKeySecret+"&");
            signature = Base64.getEncoder().encodeToString(signature.getBytes());
            System.out.println(signature);

            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.addParameter("Action", "FuzzyMatchDomainSensitiveWord");
            uriBuilder.addParameter("Keyword", text);
            uriBuilder.addParameter("Format", "JSON");
            uriBuilder.addParameter("Version", "2018-11-16");
            uriBuilder.addParameter("AccessKeyId", "LTAIo9jnP4WB6KJD");
            uriBuilder.addParameter("Signature", signature);
            uriBuilder.addParameter("SignatureMethod", "HMAC-SHA1");
            uriBuilder.addParameter("Timestamp", LocalDateTime.now().toString());
            uriBuilder.addParameter("SignatureVersion", "1.0");
            uriBuilder.addParameter("SignatureNonce", String.valueOf(UUID.randomUUID()));

            URI uri = uriBuilder.build();
            System.out.println(uri);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") : null;
    }

    public static String mapToFormData(Map<String, String> map, boolean isURLEncoder) throws UnsupportedEncodingException {
        String formData = "";
        if (map != null && map.size() > 0) {
            formData = Joiner.on("&").withKeyValueSeparator("=").join(map);
            if (isURLEncoder) {
                formData = URLEncoder.encode(formData, "UTF-8");
            }
        }
        return formData;
    }

    public static String hmacSha1(String src, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(src.getBytes("utf-8"));
            return Hex.encodeHexString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        System.out.println(String.valueOf(LocalDateTime.now()));
        check("sss");
//
//        String signature = hmacSha1("123", AccessKeySecret);
//        System.out.println(Base64.getEncoder().encodeToString(signature.getBytes()));
    }
}

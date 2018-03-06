package eu.z3r0byteapps.posterroaster.Http;

import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RequestSigner {
    private static final String TAG = "RequestSigner";
    private static String CLIENT_SECRET = "5hZpI3S6v2QLsC3gNLuSDcTzBt4QCaKa";

    public String sign(String body) {
        try {
            byte[] ivBytes = new byte[16];
            new SecureRandom().nextBytes(ivBytes);
            SecretKeySpec key = new SecretKeySpec(Base64.decode(CLIENT_SECRET, 0), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(1, key, new IvParameterSpec(ivBytes));
            String cipherBase64 = Base64.encodeToString(cipher.doFinal(sha1(Base64.decode(body, 0))), 2);
            return (cipherBase64 + "." + Base64.encodeToString(ivBytes, 2));
        } catch (Throwable exception) {
            return null;
        }
    }

    protected byte[] sha1(byte[] body) throws SecurityException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(body);
        return md.digest();
    }
}

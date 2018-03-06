package eu.z3r0byteapps.posterroaster.Http;

import android.util.Base64;

import java.io.IOException;
import java.util.Collections;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.TlsVersion;

/**
 * Created by basva on 26-2-2018.
 */

public class PostRequest {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String send(String url, String body, String bearer, Boolean sign)throws IOException{
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .build();



        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.header("x-pt-clientid", "PosterToaster-App");
        if (body != null) {
            RequestBody postDataBody = RequestBody.create(JSON, body);
            requestBuilder.post(postDataBody);
        }
        if (bearer!=null) requestBuilder.header("authorization", "Bearer " + bearer);

        if (sign){
            RequestSigner requestSigner = new RequestSigner();
            String signature = requestSigner.sign(Base64.encodeToString(body.getBytes(), 0));
            requestBuilder.header("x-pt-signature", signature);
        }

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}

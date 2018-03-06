package eu.z3r0byteapps.posterroaster.Http;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by basva on 26-2-2018.
 */

public class GetRequest {
    public static String send(String url, String bearer) throws IOException{
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.header("x-pt-clientid", "PosterToaster-App");
        if (bearer!=null) requestBuilder.header("authorization", bearer);

        Request request = requestBuilder.build();


        Response response = client.newCall(request).execute();

        String responseStr = response.body().string();
        return responseStr;
    }
}

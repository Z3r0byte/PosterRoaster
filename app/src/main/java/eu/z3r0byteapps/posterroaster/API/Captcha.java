package eu.z3r0byteapps.posterroaster.API;


import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class Captcha {
    public String id;
    public String solution;
    @SerializedName("uri")
    public String imageBase64;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}

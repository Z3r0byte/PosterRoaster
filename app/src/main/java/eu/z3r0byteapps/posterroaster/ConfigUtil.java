package eu.z3r0byteapps.posterroaster;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by basva on 28-2-2018.
 */

public class ConfigUtil {
    Context context;
    SharedPreferences sharedPreferences;

    public ConfigUtil(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public void removePreferencesValue(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(value);
        editor.apply();
    }

    public Boolean getBoolean(String name) {
        return sharedPreferences.getBoolean(name, false);
    }

    public int getInteger(String name) {
        return sharedPreferences.getInt(name, 0);
    }

    public int getInteger(String name, Integer nullValue) {
        return sharedPreferences.getInt(name, nullValue);
    }

    public String getString(String name) {
        return sharedPreferences.getString(name, "");
    }

    public void setInteger(String name, Integer integer) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, integer);
        editor.apply();
    }

    public void setBoolean(String name, Boolean bol) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, bol);
        editor.apply();
    }

    public void setString(String name, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }
}

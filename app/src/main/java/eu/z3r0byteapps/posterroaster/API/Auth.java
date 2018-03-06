package eu.z3r0byteapps.posterroaster.API;

/**
 * Created by basva on 26-2-2018.
 */

public class Auth {
    private String deviceId;
    private String email;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "{" +
                "\"deviceID\":\"" + deviceId + "\"," +
                "\"email\":\"" + email + "\"" +
                "}";
    }
}

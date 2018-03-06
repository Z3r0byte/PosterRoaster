package eu.z3r0byteapps.posterroaster.API;

/**
 * Created by basva on 26-2-2018.
 */

public class Register {
    Captcha captcha;
    Auth auth;
    String municipality;

    public Captcha getCaptcha() {
        return captcha;
    }

    public void setCaptcha(Captcha captcha) {
        this.captcha = captcha;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    @Override
    public String toString() {
        return "{\"user\":{\"email\":\"" + auth.getEmail() + "\",\"municipality\":\"" + getMunicipality() + "\",\"deviceID\":\"" + auth.getDeviceId() +
                "\"},\"captcha\":{\"id\":\"" + captcha.getId() + "\",\"plaintext\":\"" + captcha.getSolution() + "\"}}\n";
    }
}

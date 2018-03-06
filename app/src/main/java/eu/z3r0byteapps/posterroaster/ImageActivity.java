package eu.z3r0byteapps.posterroaster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import eu.z3r0byteapps.posterroaster.API.Auth;
import eu.z3r0byteapps.posterroaster.API.Captcha;
import eu.z3r0byteapps.posterroaster.API.Poster;
import eu.z3r0byteapps.posterroaster.API.Register;
import eu.z3r0byteapps.posterroaster.API.Registration;
import eu.z3r0byteapps.posterroaster.Http.GetRequest;
import eu.z3r0byteapps.posterroaster.Http.PostRequest;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = "ImageActivity";

    private static final Integer SELECT_IMAGE = 123;

    ConfigUtil configUtil;
    Auth auth;
    Register register;
    Registration registration;
    Captcha captcha;
    Poster poster;
    String municipality;

    RelativeLayout emailView;
    RelativeLayout captchaView;
    RelativeLayout posterView;
    RelativeLayout uploadView;

    String imageBase64;

    ImageView captchaImageView;
    ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        toolbar.setTitle("PosterRoaster");
        setSupportActionBar(toolbar);

        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, "image");
        navigationDrawer.SetupNavigationDrawer();

        captchaView = findViewById(R.id.captcha_layout);
        emailView = findViewById(R.id.email_layout);
        posterView = findViewById(R.id.image_layout);
        uploadView = findViewById(R.id.upload_layout);

        captchaImageView =  findViewById(R.id.captcha);

        configUtil = new ConfigUtil(this);
        if (configUtil.getString("registration") != ""){
            registration = new Gson().fromJson(configUtil.getString("registration"), Registration.class);
            emailView.setVisibility(View.GONE);
            if (configUtil.getInteger("id") != 0) {
                posterView.setVisibility(View.VISIBLE);
                getPoster();
            } else {
                uploadView.setVisibility(View.VISIBLE);
            }
        }

        final EditText emailEditText = findViewById(R.id.email);
        final EditText municipalityEditText = findViewById(R.id.municipality);
        Button emailButton = findViewById(R.id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email;
                if (emailEditText.getText().toString().isEmpty()) {
                    email = randomString().substring(0, 11) + "@gmail.com";
                } else {
                    email = emailEditText.getText().toString();
                }
                if (municipalityEditText.getText().toString().isEmpty()){
                    municipality = "Gemert-Bakel";
                } else {
                    municipality = municipalityEditText.getText().toString();
                }
                auth(email);
            }
        });

        final EditText capcthaEditText = findViewById(R.id.captcha_solution);
        Button captchaButton = findViewById(R.id.captcha_button);
        captchaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(capcthaEditText.getText().toString());
            }
        });

        final EditText nameEditText = findViewById(R.id.name);
        final EditText sloganEditText = findViewById(R.id.slogan);
        Button uploadButton = findViewById(R.id.upload);
        Button chooseFile = findViewById(R.id.choose_picture);
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Kies afbeelding"), SELECT_IMAGE);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String slogan = sloganEditText.getText().toString();
                if (name.isEmpty()) name = randomString().substring(0,20);
                if (slogan.isEmpty()) slogan = randomString().substring(0,16);
                uploadPoster(name,slogan);
            }
        });

        posterImageView = findViewById(R.id.poster);


        //emailView.setVisibility(View.GONE);
        //uploadView.setVisibility(View.VISIBLE);
    }

    private void auth(final String email){
        new Thread(new Runnable() {
            @Override
            public void run() {
                auth = new Auth();
                auth.setDeviceId("android:"+randomString().substring(0, 16));
                auth.setEmail(email);
                try {
                    String resp = PostRequest.send("https://api.postertoaster.nl/auth", auth.toString(), null, true);
                    if (resp.contains("user")){
                        getCaptcha();
                    } else {
                        Log.d(TAG, "auth: something went wrong");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getCaptcha(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    captcha = new Gson().fromJson(GetRequest.send("https://api.postertoaster.nl/captcha", ""), Captcha.class);
                    byte[] decodedBytes = Base64.decode(captcha.getImageBase64().split(",")[1], Base64.DEFAULT);
                    final Bitmap captchaBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            captchaImageView.setImageBitmap(captchaBitmap);
                            captchaView.setVisibility(View.VISIBLE);
                            emailView.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void register(final String captchaSolution){
        new Thread(new Runnable() {
            @Override
            public void run() {
                captcha.setSolution(captchaSolution);
                register = new Register();
                register.setAuth(auth);
                register.setCaptcha(captcha);
                register.setMunicipality(municipality);
                try {
                    String resp = PostRequest.send("https://api.postertoaster.nl/register", register.toString(), null, true);
                    registration = new Gson().fromJson(resp, Registration.class);
                    if (resp.contains("status")){
                        getCaptcha();
                        return;
                    }
                    configUtil.setString("registration", new Gson().toJson(registration));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadView.setVisibility(View.VISIBLE);
                            captchaView.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException e){
                    getCaptcha();
                }
            }
        }).start();
    }

    private void getPoster(){

    }

    private void uploadPoster(final String name, final String slogan){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String code;
                try {
                    String resp = GetRequest.send("https://api.postertoaster.nl/code", registration.getUser().getToken());
                    if (resp.contains("code")){
                        code = resp.substring(9,14);
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                String postData = "{\"poster\":{\"name\":\"" + name + "\",\"slogan\":\"" + slogan + "\",\"color\":\"#731caa\",\"code\":\"" + code + "\",\"base64\":\"" + imageBase64 + "\",\"contentType\":\"image/jpeg\"}}";
                try {
                    String resp = PostRequest.send("https://api.postertoaster.nl/poster", postData, registration.getUser().getToken(), true);
                    if (resp.contains("status")){
                        Poster poster = new Gson().fromJson(resp, Poster.class);
                        configUtil.setString("id", poster.getId());
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String randomString(){
        return UUID.randomUUID().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SELECT_IMAGE) {
            if (data == null) {
                Toast.makeText(this, "Er is een fout opgetreden", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] dataBytes = new byte[16384000];

                while ((nRead = inputStream.read(dataBytes, 0, dataBytes.length)) != -1) {
                    buffer.write(dataBytes, 0, nRead);
                }
                buffer.flush();
                imageBase64 = Base64.encodeToString(buffer.toByteArray(), Base64.DEFAULT).replaceAll("\n", "");
            } catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}

package eu.z3r0byteapps.posterroaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.UUID;

import eu.z3r0byteapps.posterroaster.API.Auth;
import eu.z3r0byteapps.posterroaster.API.Captcha;
import eu.z3r0byteapps.posterroaster.API.Poster;
import eu.z3r0byteapps.posterroaster.API.Register;
import eu.z3r0byteapps.posterroaster.API.Registration;
import eu.z3r0byteapps.posterroaster.API.Vote;
import eu.z3r0byteapps.posterroaster.Http.GetRequest;
import eu.z3r0byteapps.posterroaster.Http.PostRequest;

public class VoteActivity extends AppCompatActivity {
    private static final String TAG = "VoteActivity";

    Auth auth;
    Auth auth2;
    Captcha captcha;
    Captcha captcha2;
    Register register;
    Registration registration;
    String posterId;
    String posterCode;
    Vote vote;

    private ImageView captchaImageView;
    private EditText posterCodeEditText;
    private EditText captchaEditText;
    private TextView amountOfVotes;
    private Bitmap captchaBitmap;
    private Bitmap captchaBitmap2;

    RelativeLayout startLayout;
    RelativeLayout captchaLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        toolbar.setTitle("PosterRoaster");
        setSupportActionBar(toolbar);

        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, "vote");
        navigationDrawer.SetupNavigationDrawer();

        posterCodeEditText = findViewById(R.id.poster_id);

        Button voteButton = findViewById(R.id.vote);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posterCode = posterCodeEditText.getText().toString();
                startLayout.setVisibility(View.GONE);
                captchaLayout.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        auth = new Auth();
                        auth.setDeviceId("android:"+randomString().substring(0, 16));
                        auth.setEmail(randomString().substring(0,10) + "@gmail.com");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    String resp = PostRequest.send("https://api.postertoaster.nl/auth", auth.toString(), null, true);
                                    if (resp.contains("user")){
                                        captcha = new Gson().fromJson(GetRequest.send("https://api.postertoaster.nl/captcha", ""), Captcha.class);
                                        byte[] decodedBytes = Base64.decode(captcha.getImageBase64().split(",")[1], Base64.DEFAULT);
                                        captchaBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                    } else {
                                        Log.d(TAG, "Access Denied");
                                    }
                                } catch (IOException e){
                                    e.printStackTrace();
                                }
                                startVoting();
                            }
                        }).start();
                    }
                }).start();
            }
        });

        Button stopButton =  findViewById(R.id.stop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLayout.setVisibility(View.VISIBLE);
                captchaLayout.setVisibility(View.GONE);
            }
        });

        amountOfVotes = findViewById(R.id.vote_amount);

        captchaEditText = findViewById(R.id.captcha_solution);
        captchaEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                captcha.setSolution(captchaEditText.getText().toString());
                captchaEditText.setText("");
                finishVoting();
                return true;
            }
        });

        captchaImageView = findViewById(R.id.captcha);
        startLayout = findViewById(R.id.start_layout);
        captchaLayout = findViewById(R.id.captcha_layout);
    }

    private void startVoting(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                captchaImageView.setImageBitmap(captchaBitmap);
            }
        });
        auth2 = new Auth();
        auth2.setDeviceId("android:"+randomString().substring(0, 16));
        auth2.setEmail(randomString().substring(0,10) + "@gmail.com");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String resp = PostRequest.send("https://api.postertoaster.nl/auth", auth2.toString(), null, true);
                    if (resp.contains("user")){
                        captcha2 = new Gson().fromJson(GetRequest.send("https://api.postertoaster.nl/captcha", ""), Captcha.class);
                        byte[] decodedBytes = Base64.decode(captcha2.getImageBase64().split(",")[1], Base64.DEFAULT);
                        captchaBitmap2 = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    } else {
                        Log.d(TAG, "Access Denied");
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void finishVoting(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                register = new Register();
                register.setMunicipality("Gemert-Bakel");
                register.setAuth(auth);
                register.setCaptcha(captcha);
                auth = auth2;
                captcha = captcha2;
                captchaBitmap = captchaBitmap2;
                startVoting();
                try {
                    String resp = PostRequest.send("https://api.postertoaster.nl/register", register.toString(), null, true);
                    registration = new Gson().fromJson(resp, Registration.class);
                    if (posterId == null && !resp.contains("status")){
                        resp = GetRequest.send("https://api.postertoaster.nl/posters/" + posterCode, registration.getUser().getToken());
                        Poster poster = new Gson().fromJson(resp, Poster.class);
                        posterId = poster.getPoster().getId();
                        if (posterId != null){
                            resp = PostRequest.send("https://api.postertoaster.nl/posters/" + posterId + "/vote", "", registration.getUser().getToken(), false);
                            vote = new Gson().fromJson(resp, Vote.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    amountOfVotes.setText("Aantal stemmen: " + vote.getVote().getVotes());
                                }
                            });
                        }
                    } else if (!resp.contains("status")){
                        resp = PostRequest.send("https://api.postertoaster.nl/posters/" + posterId + "/vote", "", registration.getUser().getToken(), false);
                        vote = new Gson().fromJson(resp, Vote.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                amountOfVotes.setText("Aantal stemmen: " + vote.getVote().getVotes());
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                amountOfVotes.setText(amountOfVotes.getText() + " (Ongeldige captcha)");
                            }
                        });
                    }
                } catch (IOException e){
                    e.printStackTrace();
                } catch (NullPointerException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VoteActivity.this, "Is het poster-id wel goed?", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private String randomString(){
        return UUID.randomUUID().toString();
    }
}

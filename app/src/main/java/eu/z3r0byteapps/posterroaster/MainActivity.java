package eu.z3r0byteapps.posterroaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import eu.z3r0byteapps.posterroaster.Http.RequestSigner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.Toolbar);
        toolbar.setTitle("PosterRoaster");
        setSupportActionBar(toolbar);

        NavigationDrawer navigationDrawer = new NavigationDrawer(this, toolbar, "home");
        navigationDrawer.SetupNavigationDrawer();
    }
}

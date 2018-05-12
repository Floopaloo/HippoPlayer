package nguyen.zylin.hippoplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import nguyen.zylin.hippoplayer.R;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_SCREEN_DURATION = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        scheduleSplashScreen();
    }

    private void scheduleSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                processAfterDelay();
            }
        }, SPLASH_SCREEN_DURATION);
    }

    private void processAfterDelay() {

        //TODO: Check & grand permission here
        if (checkAndRequestPermissions()) {
            routeToAppropriatePage(MainActivity.class);
        } else {
            Toast.makeText(this, "I can't load song from your library 😢", Toast.LENGTH_LONG).show();
        }

    }

    private void routeToAppropriatePage(Class cls) {
        startActivity(new Intent(this, cls));
        finish();
    }




    private static final int MY_PERMISSIONS_REQUEST = 1;
    private boolean checkAndRequestPermissions() {

        int storageReadPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int storageWritePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storageReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storageWritePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);

            return false;
        }

        return true;
    }
}

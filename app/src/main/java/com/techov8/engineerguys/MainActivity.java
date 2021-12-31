package com.techov8.engineerguys;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.techov8.cec.ebook.EbookActivity;

import java.util.Objects;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private int REQUEST_CODE = 11;
    private Dialog referDialog;
    public static boolean isTestAd=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.frame_layout);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        //////////////////////////////////// IN APP UPDATE FEATURES


        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        result.isUpdateTypeAllowed(FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, FLEXIBLE, MainActivity.this, REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("weather")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                    }
                });

        /////////////////////////////////


        SharedPreferences prefs = getSharedPreferences("Refer_data",
                0);
        String referId = prefs.getString("id",
                "no");

        referDialog = new Dialog(MainActivity.this);
        referDialog.setContentView(R.layout.refer_dialog);
        referDialog.setCancelable(true);
        referDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button shareBtn = referDialog.findViewById(R.id.share_btn);
        TextView referText = referDialog.findViewById(R.id.refer_text);

        referText.setText("Your Refer Id :- " + referId);
        shareBtn.setOnClickListener(view -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");

                String shareMessage = "\nMy Refer Id: "+referId+"\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.techov8.p_droid" + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_developer:
                startActivity(new Intent(this, About_Us.class));
                break;
            case R.id.navigation_rateUs:

                Uri uri = Uri.parse("market://details?id=com.techov8.p_droid");

                Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);

                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                }

                try {
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techov8.p_droid")));
                }
                break;


            case R.id.navigation_privacy:
                // startActivity(new Intent(this, About_Us.class));

                String url = "https://super-cadence.github.io/Cadence.github.io/#services";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                break;

            case R.id.navigation_share:

                // Toast.makeText(this, "Oops not available in play store", Toast.LENGTH_SHORT).show();

                referDialog.show();

                break;

            case R.id.navigation_logout:
                // startActivity(new Intent(this, com.techov8.cec.Results.class));


                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else

            super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            Toast.makeText(this, "Start Download", Toast.LENGTH_SHORT).show();

            if (resultCode != RESULT_OK) {
                Log.d("UpdateTest", "Update flow failed" + resultCode);
            }
        }
    }

}
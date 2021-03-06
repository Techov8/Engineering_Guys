package com.techov8.engineerguys;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;

import java.util.Objects;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;


    public static AlertDialog.Builder passwordResetDialog;

    public static Boolean isVerified = false;


    TextView fullname, email;

    private final int REQUEST_CODE = 11;
    private Dialog referDialog;
    public static boolean isTestAd = false;
    private InterstitialAd mInterstitialAd;
    private String AD_UNIT_ID;

    // private String referId;
    public static long noOfCoins;
    public static String refered_by;
    public static String referIdd;
    public static boolean isTaskDone;
    public static ActionBar mActionBar;
    public static TextView mTitleTextView;
    public static View mCustomView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ///// for showing coin to user
        mActionBar = getSupportActionBar();
        Objects.requireNonNull(mActionBar).setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(MainActivity.this);

        mCustomView = mInflater.inflate(R.layout.custom_actionbar,null);
        mTitleTextView = mCustomView.findViewById(R.id.title_text);


        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(task -> {

                    noOfCoins = task.getResult().getLong("no_of_coins");
                    refered_by = task.getResult().getString("referal");
                    referIdd = task.getResult().getString("refer_id");
                    isTaskDone = task.getResult().getBoolean("is_task_done");
                    mTitleTextView.setText(String.valueOf(noOfCoins));
                    changeCoin();
                    transferCoin();
                });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.frame_layout);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        View header = navigationView.getHeaderView(0);

        fullname = header.findViewById(R.id.headerName);
        email = header.findViewById(R.id.headerEmail);


        FirebaseUser usero = FirebaseAuth.getInstance().getCurrentUser();
        email.setText(Objects.requireNonNull(usero).getEmail());

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        if (MainActivity.isTestAd) {
            AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
        } else {
            AD_UNIT_ID = "ca-app-pub-3197714952509994/6590958965";
        }

        if (HomeFragment.isAdActive) {
            loadAd();
        }
//////////////////////////////////// IN APP UPDATE FEATURES
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    result.isUpdateTypeAllowed(FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(result, FLEXIBLE, MainActivity.this, REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        //////////////////////for cloud notification
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {

                });

/////////////////////////////////



///// for showing coin to user till here


////////////////////////////////////////////////////////////////////////////////for email verification

        passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Email not verified");
        // passwordResetDialog.setMessage("Enter Your Email To Receive Reset link ");
        // passwordResetDialog.setView(resetMail);


        passwordResetDialog.setPositiveButton("Verify now", (dialog, which) -> Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnSuccessListener(unused -> {

            Toast.makeText(MainActivity.this, "Verification link Sent To Your Mail", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();

        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Verification link Not Sent To Your Mail", Toast.LENGTH_SHORT).show()));

        if (!Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {


            passwordResetDialog.show();
            passwordResetDialog.setCancelable(false);
            isVerified = false;


        } else {
            isVerified = true;

        }


///////////////////////////////////////////////////////////////////////////////////////////////////for adding coin to the rfrar





////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//////////for share


        referDialog = new Dialog(MainActivity.this);
        referDialog.setContentView(R.layout.refer_dialog);
        referDialog.setCancelable(true);
        referDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button shareBtn = referDialog.findViewById(R.id.share_btn);
        TextView referText = referDialog.findViewById(R.id.refer_text);

        referText.setText(String.format("Your Refer Id :- %s", referIdd));
        shareBtn.setOnClickListener(view -> {
            try {
                referDialog.dismiss();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download Engineering Guyz App");

                String shareMessage = "\nMy Refer Id: " + referIdd + "\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=com.techov8.engineerguys" + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }
        });


    }


    public void loadAd() {
        MobileAds.initialize(this, initializationStatus -> {
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("MainActivity", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("MainActivity", loadAdError.getMessage());
                        mInterstitialAd = null;
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
                String url = "https://engineering-guyz.github.io/#about";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.navigation_rateUs:

                Uri uri = Uri.parse("market://details?id=com.techov8.engineerguys");

                Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);

                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                }

                try {
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techov8.engineerguys")));
                }
                break;


            case R.id.navigation_privacy:
                // startActivity(new Intent(this, About_Us.class));

                String urll = "https://engineering-guyz.github.io/#services";
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setData(Uri.parse(urll));
                startActivity(in);

                break;

            case R.id.navigation_share:

                // Toast.makeText(this, "Oops not available in play store", Toast.LENGTH_SHORT).show();

                referDialog.show();

                break;

            case R.id.navigation_logout:
                // startActivity(new Intent(this, com.techov8.cec.Results.class));


                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mInterstitialAd != null && HomeFragment.isAdActive) {
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                    loadAd();

                }
//
//                @Override
//                public void onAdFailedToShowFullScreenContent(AdError adError) {
//                    // Called when fullscreen content failed to show.
//                    Log.d("TAG", "The ad failed to show.");
//                    loadAd();
//                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    loadAd();
                    Log.d("TAG", "The ad was shown.");
                }
            });
        }


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {


            drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();
        }
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

    public static void changeCoin() {
        mTitleTextView.setText(String.valueOf(noOfCoins));

        mActionBar.setCustomView(mCustomView, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.END
        ));
        mActionBar.setDisplayShowCustomEnabled(true);
    }
    private void transferCoin(){
        if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified() && !isTaskDone && !refered_by.equals(" ")) {
            firebaseFirestore.collection("USERS").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.getString("refer_id").equals(refered_by)) {
                                long noOfCoins = documentSnapshot.getLong("no_of_coins");
                                String id = documentSnapshot.getString("id");
                                firebaseFirestore.collection("USERS").document(id).update("no_of_coins", noOfCoins + 5)
                                        .addOnCompleteListener(task -> firebaseFirestore.collection("USERS").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).update("is_task_done", true));
                            }
                        }
                    }).addOnFailureListener(e -> firebaseFirestore.collection("USERS").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).update("is_task_done", true));
        }
    }
}
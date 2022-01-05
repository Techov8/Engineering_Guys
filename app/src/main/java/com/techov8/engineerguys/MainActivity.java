package com.techov8.engineerguys;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;
import com.techov8.engineerguys.ui.Profile.User;

import java.util.Objects;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.techov8.engineerguys.RegisterActivity.isfromRegister;
import static com.techov8.engineerguys.RegisterActivity.referedId;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;


    public static AlertDialog.Builder passwordResetDialog;

    public static Boolean isVerified=false;


    TextView fullname,email;

    private int REQUEST_CODE = 11;
    private Dialog referDialog;
    public static boolean isTestAd = true;
    private InterstitialAd mInterstitialAd;
    private String AD_UNIT_ID, coin, username;

    private String isFromRegister = "No";
   // private String referId;
    public  static String noOfCoins = "0";
    public  static String refered_by = "0";
    public  static String refer_counterr = "0";
    public  static String referIdd ;


    private DatabaseReference mref, mRootRef, muser;

    private FirebaseAuth mAuth;


    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mref = FirebaseDatabase.getInstance().getReference();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        muser = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();






        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navController = Navigation.findNavController(this, R.id.frame_layout);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);


        View header = navigationView.getHeaderView(0);

        fullname=header.findViewById(R.id.headerName);
        email=header.findViewById(R.id.headerEmail);


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

        loadAd();
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

        //////////////////////for cloud notification
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

                    }
                });

/////////////////////////////////



        ///// for showing coin to user
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);


        mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot .getChildren()) {

                    //noOfCoins = snapshot.getValue().toString();
                    noOfCoins= snapshot.child("no_of_coins").getValue().toString();
                    refered_by= snapshot.child("refer_by").getValue().toString();
                    refer_counterr=snapshot.child("refer_counter").getValue().toString();
                    referIdd=snapshot.child("refer_id").getValue().toString();

                    mTitleTextView.setText(noOfCoins);


                    mActionBar.setCustomView(mCustomView, new ActionBar.LayoutParams(
                            ActionBar.LayoutParams.WRAP_CONTENT,
                            ActionBar.LayoutParams.MATCH_PARENT,
                            Gravity.RIGHT
                    ));
                    mActionBar.setDisplayShowCustomEnabled(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


///// for showing coin to user till here


////////////////////////////////////////////////////////////////////////////////for email verification

         passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Email not verified");
        // passwordResetDialog.setMessage("Enter Your Email To Receive Reset link ");
        // passwordResetDialog.setView(resetMail);


        passwordResetDialog.setPositiveButton("Verify now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {

                        Toast.makeText(MainActivity.this, "Verification link Sent To Your Mail", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MainActivity.this, "Verification link Not Sent To Your Mail", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });





        if (!mAuth.getCurrentUser().isEmailVerified()) {




          /*  passwordResetDialog.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

           */

          passwordResetDialog.show();
          passwordResetDialog.setCancelable(false);
          isVerified=false;


        }else {
            Toast.makeText(this, "Verified", Toast.LENGTH_SHORT).show();

            isVerified=true;

        }


///////////////////////////////////////////////////////////////////////////////////////////////////for adding coin to the rfrar

        Intent intent = getIntent();

       // String referal = intent.getStringExtra("referal");
        //isFromRegister = intent.getStringExtra("isFromRegister");


        if(Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified() && refer_counterr.equals("1")&& !refered_by.equals(" "))

        //if (referal != null && isFromRegister != null)
            {

            if (counter < 1) {

                FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User post = snapshot.getValue(User.class);


                            if (post.getRefer_id().equals(refered_by)) {


                                Log.e("dimag khrab", "ho gya" + post.getRefer_id());
                                Log.e("dimag khrab", "ho gya" + post.getId());
                                Log.e("dimag khrab", "ho gya" + post.getNo_of_coins());

                                if (counter < 1) {

                                    mref.child("Users").child(post.getId()).child("no_of_coins").setValue(String.valueOf(Integer.parseInt(post.getNo_of_coins()) + 5));
                                    muser.child("Users").child(mAuth.getCurrentUser().getUid()).child("refer_counter").setValue("0");



                                    counter++;
                                }

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        SharedPreferences prefs = getSharedPreferences("Refer_data",
                0);
       String referId = prefs.getString("id",
                "no");




//////////for share


        referDialog = new Dialog(MainActivity.this);
        referDialog.setContentView(R.layout.refer_dialog);
        referDialog.setCancelable(true);
        referDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button shareBtn = referDialog.findViewById(R.id.share_btn);
        TextView referText = referDialog.findViewById(R.id.refer_text);

        referText.setText("Your Refer Id :- " + referId);
        shareBtn.setOnClickListener(view -> {
            try {
                referDialog.dismiss();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");

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
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
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

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                    loadAd();
                }

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

}
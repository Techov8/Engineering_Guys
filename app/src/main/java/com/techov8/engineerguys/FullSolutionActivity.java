package com.techov8.engineerguys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.squareup.picasso.Picasso;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;

public class FullSolutionActivity extends AppCompatActivity {

    private TextView question, solution;
    private ImageView image;
    private InterstitialAd mInterstitialAd;
    private String AD_UNIT_ID,NATIVE_AD_ID;
    private  TemplateView nativeTemplateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_full_solution);

        question = findViewById(R.id.fullquestiontxt);
        solution = findViewById(R.id.fullSolutiontxt);
        image = findViewById(R.id.solutionImage);
        nativeTemplateView = findViewById(R.id.full_solution_native);
        if (MainActivity.isTestAd) {
            AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
            NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110";
        } else {
            AD_UNIT_ID = "ca-app-pub-3197714952509994/4132316584";
            NATIVE_AD_ID = "ca-app-pub-3197714952509994/3328880116";
        }

        if(HomeFragment.isAdActive) {
            loadAd();
        }

        if (HomeFragment.isAdActive) {
            nativeTemplateView.setVisibility(View.VISIBLE);
            MobileAds.initialize(this);
            AdLoader adLoader = new AdLoader.Builder(this, NATIVE_AD_ID)
                    .forNativeAd(nativeAd -> {
                        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(this, R.color.white));
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();

                        nativeTemplateView.setStyles(styles);
                        nativeTemplateView.setNativeAd(nativeAd);
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else {
            nativeTemplateView.setVisibility(View.GONE);
        }
        Intent intent = getIntent();

        String img = intent.getStringExtra("image");
        String ques = intent.getStringExtra("question");
        String sol = intent.getStringExtra("solution");
        solution.setText(sol);
        question.setText(ques);
        try {
            if (img != null)
                Picasso.get().load(img).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null && HomeFragment.isAdActive) {
                    mInterstitialAd.show(FullSolutionActivity.this);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            Log.d("TAG", "The ad was dismissed.");
                            loadAd();
                            Intent intent = new Intent(FullSolutionActivity.this, FullImageView.class);
                            intent.putExtra("image", img);
                            startActivity(intent);
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

                }else{
                    Intent intent = new Intent(FullSolutionActivity.this, FullImageView.class);
                    intent.putExtra("image", img);
                    startActivity(intent);
                }

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
    public void onBackPressed() {
        if (mInterstitialAd != null && HomeFragment.isAdActive) {
            mInterstitialAd.show(FullSolutionActivity.this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");

                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    finish();
                    Log.d("TAG", "The ad was shown.");
                }
            });
        }else{
           finish();
        }


        super.onBackPressed();

    }
}
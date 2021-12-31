package com.techov8.engineerguys.ui.AskQuestion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.techov8.engineerguys.MainActivity;
import com.techov8.engineerguys.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;



public class HomeFragment extends Fragment {

    private Button sendquestion, earnbtn;

    private AdView mAdView;
    private RewardedAd rewardedAdMain;
    private static  String VIDEO_AD_UNIT_ID ;
    boolean isLoading;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sendquestion = view.findViewById(R.id.sendbtn);
        earnbtn = view.findViewById(R.id.earnbtn);

        if(MainActivity.isTestAd){
            VIDEO_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
        }else{
            VIDEO_AD_UNIT_ID = "ca-app-pub-4594073781530728/7481048002";
        }
        loadRewardedAd();

        earnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedVideo();
            }
        });

        /////ads

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {

                mAdView.loadAd(adRequest);
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

        ///








        sendquestion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"fake@fake.edu"});
                i.putExtra(Intent.EXTRA_SUBJECT, "On The Job");
                i.putExtra(Intent.EXTRA_TEXT, "Type your question or attach photo");


                startActivity(Intent.createChooser(i, "Share you on the jobing"));
            }
        });


        return view;
    }

    private void loadRewardedAd() {
        if (rewardedAdMain == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    getContext(),
                    VIDEO_AD_UNIT_ID,
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d("HomeFragment", loadAdError.getMessage());
                            rewardedAdMain = null;
                            isLoading = false;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            rewardedAdMain = rewardedAd;
                            Log.d("HomeFragment", "onAdLoaded");
                            isLoading = false;
                        }
                    });
        }
    }

    private void showRewardedVideo() {

        if (rewardedAdMain == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }

        rewardedAdMain.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d("TAG", "onAdShowedFullScreenContent");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d("HomeFragment", "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAdMain = null;

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAdMain = null;
                        Log.d("TAG", "onAdDismissedFullScreenContent");

                        // Preload the next rewarded ad.
                        loadRewardedAd();
                    }
                });
        rewardedAdMain.show(
                getActivity(),
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();


                        // Supriyo do coin work
                    }
                });
    }


}

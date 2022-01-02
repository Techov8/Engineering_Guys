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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techov8.engineerguys.MainActivity;
import com.techov8.engineerguys.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.techov8.engineerguys.MainActivity.noOfCoins;


public class HomeFragment extends Fragment {

    private Button sendquestion, earnbtn;

    private DatabaseReference mref;
    private FirebaseAuth mAuth;

    private AdView mAdView;
    private RewardedAd rewardedAdMain;
    private static String VIDEO_AD_UNIT_ID;
    boolean isLoading;
    private FirebaseFirestore firebaseFirestore;
    ImageSlider imageSlider;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sendquestion = view.findViewById(R.id.sendbtn);
        earnbtn = view.findViewById(R.id.earnbtn);

        mref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


///////////////////////////////////////////////////////////////////////////Banner slider


        firebaseFirestore = FirebaseFirestore.getInstance();
        imageSlider = view.findViewById(R.id.image_slider);

        final List<SlideModel> imageSliderList = new ArrayList<SlideModel>();


        ///////
        firebaseFirestore.collection("Newsfeed").

                get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {


                                long no_of_banners = (long) documentSnapshots.get("no_of_banners");
                                for (long x = 1; x < no_of_banners + 1; x++) {
                                    imageSliderList.add(new SlideModel(documentSnapshots.get("banner_" + x + "_url").toString()
                                            , documentSnapshots.get("banner_" + x + "_title").toString(), ScaleTypes.FIT));
                                }


                                imageSlider.setImageList(imageSliderList,ScaleTypes.FIT);

                                imageSlider.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onItemSelected(int i) {

                                    }
                                });

                            }


                        } else {
                            // Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        //home page data code end!


        ///////

///////////////////////////////////////////////////////////////////////////Banner slider till here


        if (MainActivity.isTestAd) {
            VIDEO_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
        } else {
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


                try {

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("plain/text");
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fake@edu"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Question");
                    intent.putExtra(Intent.EXTRA_TEXT, "Write your question or attach photo");
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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



                      mref.child("Users").child(mAuth.getCurrentUser().getUid()).child("no_of_coins").setValue(String.valueOf(rewardAmount + Integer.parseInt(noOfCoins)));



                        Toast.makeText(getContext(), rewardAmount+" coin added", Toast.LENGTH_SHORT).show();
                        // Supriyo do coin work
                    }
                });
    }


}

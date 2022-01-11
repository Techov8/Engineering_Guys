package com.techov8.engineerguys.ui.AskQuestion;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techov8.engineerguys.MainActivity;
import com.techov8.engineerguys.R;
import com.techov8.engineerguys.ui.Solution.SolutionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.techov8.engineerguys.MainActivity.changeCoin;
import static com.techov8.engineerguys.MainActivity.isVerified;
import static com.techov8.engineerguys.MainActivity.noOfCoins;
import static com.techov8.engineerguys.MainActivity.passwordResetDialog;


public class HomeFragment extends Fragment {

    private ClipboardManager myClipboard;
    private ClipData myClip;



    private AdView mAdView;
    private RewardedAd rewardedAdMain;
    private static String VIDEO_AD_UNIT_ID;
    boolean isLoading;
    private FirebaseFirestore firebaseFirestore;
    ImageSlider imageSlider;
    public static boolean isAdActive, isRatingCoin;
    public static ArrayList<SolutionData> list = new ArrayList<>();
    private AdRequest adRequest;
    private List<SlideModel> imageSliderList;
    private Button sendquestion, earnbtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sendquestion = view.findViewById(R.id.sendbtn);
        earnbtn = view.findViewById(R.id.earnbtn);

        LinearLayout linearLayout=view.findViewById(R.id.watch_ad_container);
        if(isAdActive){
            linearLayout.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.GONE);
        }

        myClipboard = (ClipboardManager) requireContext().getSystemService(CLIPBOARD_SERVICE);
        firebaseFirestore=FirebaseFirestore.getInstance();

        sendquestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {


                    //Toast.makeText(getContext(),"clicked",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    // intent.setType("plain/text");
                    // intent.setDataAndType(Uri.parse("mailto:"), "plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"engineeringguyzinfo@gmail.com"});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Question by " + MainActivity.referIdd);
                    intent.putExtra(Intent.EXTRA_TEXT, "Write your question or attach photo");
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivity(intent);


                    } else {
                        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.layout), "Please mail us your question on engineeringguyzinfo@gmail.com from your registered mail", Snackbar.LENGTH_LONG);
                        snackbar.setAction("Copy mail ", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //String text = textView.getText().toString();
                                myClip = ClipData.newPlainText("text", "engineeringguyzinfo@gmail.com");
                                myClipboard.setPrimaryClip(myClip);
                                Toast.makeText(getContext(), "Mail Copied",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                        snackbar.show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                    Snackbar snackbarr = Snackbar.make(view.findViewById(R.id.layout), "Please mail us your question on engineeringguyzinfo@gmail.com from your registered mail", Snackbar.LENGTH_LONG);
                    snackbarr.setAction("Copy mail ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //String text = textView.getText().toString();
                            myClip = ClipData.newPlainText("text", "engineeringguyzinfo@gmail.com");
                            myClipboard.setPrimaryClip(myClip);
                            Toast.makeText(getContext(), "Mail Copied",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                    snackbarr.show();
                }

            }
        });
        //////


///////////////////////////////////////////////////////////////////////////Banner slider

        imageSlider = view.findViewById(R.id.image_slider);

        imageSliderList = new ArrayList<>();

        //if (imageSliderList.size() == 0) {
        readData();
        // }


        //home page data code end!


        ///////

///////////////////////////////////////////////////////////////////////////Banner slider till here


        if (MainActivity.isTestAd) {
            VIDEO_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
            // BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111";
        } else {
            VIDEO_AD_UNIT_ID = "ca-app-pub-3197714952509994/6884981747";
            //BANNER_AD_UNIT = "ca-app-pub-3197714952509994/9724388763";
        }


        earnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isVerified) {
                    showRewardedVideo();
                }else{
                    passwordResetDialog.show();
                    passwordResetDialog.setCancelable(false);
                }
            }
        });

        /////ads
        mAdView = view.findViewById(R.id.adView);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adRequest = new AdRequest.Builder().build();

        if (isAdActive) {
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
            loadRewardedAd();
        } else {
            mAdView.setVisibility(View.GONE);
        }


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


        return view;
    }

    private void readData() {
        ///////
        firebaseFirestore.collection("HOME").document("Basic Data")

                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        isAdActive = task.getResult().getBoolean("is_ad_active");
                        isRatingCoin = task.getResult().getBoolean("is_rating_coin");
                        long no_of_banners = task.getResult().getLong("no_of_banners");
                        for (long x = 0; x < no_of_banners; x++) {
                            imageSliderList.add(new SlideModel(task.getResult().getString("banner_" + x + "_url")
                                    , ScaleTypes.FIT));

                         //   String k = task.getResult().getString("bannerpromo_" + x + "_link").toString();


                            imageSlider.setImageList(imageSliderList, ScaleTypes.FIT);


                            imageSlider.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onItemSelected(int i) {

                                   //Log.e("urllll", "ye hai  " + imageSliderList.get(i).getTitle().toString());

                                  //  String n = imageSliderList.get(i).getImageUrl().toString();
                                    //task.getResult().getString("bannerpromo_"+ x +"_link").toString();
                                    String k = task.getResult().getString("bannerpromo_" + i + "_link").toString();
                                    Intent in = new Intent(Intent.ACTION_VIEW);
                                    in.setData(Uri.parse(k));
                                    startActivity(in);

                                }
                            });

                        }


                    }
                });

    }

    private void loadRewardedAd() {
        if (rewardedAdMain == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    requireContext(),
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
                requireActivity(),
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();


                        firebaseFirestore.collection("USERS").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).update("no_of_coins",noOfCoins+rewardAmount);
                        noOfCoins=noOfCoins+rewardAmount;
                        changeCoin();

                        Toast.makeText(getContext(), rewardAmount + " coins added!", Toast.LENGTH_SHORT).show();
                        // Supriyo do coin work
                    }
                });
    }


}

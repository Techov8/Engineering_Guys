package com.techov8.engineerguys.ui.Solution;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.techov8.engineerguys.FullImageView;
import com.techov8.engineerguys.FullSolutionActivity;
import com.techov8.engineerguys.MainActivity;
import com.techov8.engineerguys.R;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;


import java.util.ArrayList;

import static com.techov8.engineerguys.MainActivity.changeCoin;
import static com.techov8.engineerguys.MainActivity.isVerified;
import static com.techov8.engineerguys.MainActivity.noOfCoins;
import static com.techov8.engineerguys.MainActivity.passwordResetDialog;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.NoticeViewAdapter> {

    private Context context;
    private ArrayList<SolutionData> list;

    public SolutionAdapter(Context context, ArrayList<SolutionData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {

        final SolutionData currentItem = list.get(position);
        holder.deleteNoticeTitle.setText(currentItem.getTitle());
        holder.date.setText(currentItem.getData());
        holder.solutiontxt.setText(currentItem.getTime());
        holder.companyName.setText(currentItem.getCompanyName());

        try {
            if (currentItem.getImage() != null)
                Picasso.get().load(currentItem.getImage()).into(holder.deleteNoticeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (currentItem.getCompanyImage() != null)
                Picasso.get().load(currentItem.getCompanyImage()).into(holder.companyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        holder.fullsolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (noOfCoins >= 5) {

                    if (isVerified) {
                        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).update("no_of_coins", noOfCoins-5)
                                .addOnCompleteListener(task -> {
                                    noOfCoins=noOfCoins-5;
                                    changeCoin();
                                    Toast.makeText(holder.itemView.getContext(), 5 + " coins redeemed!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, FullSolutionActivity.class);
                                    intent.putExtra("image", currentItem.getImage());
                                    intent.putExtra("question", currentItem.getTitle());
                                    intent.putExtra("solution", currentItem.getTime());
                                    context.startActivity(intent);
                                });


                    } else {
                        passwordResetDialog.show();
                        passwordResetDialog.setCancelable(false);
                    }

                } else {
                    Toast.makeText(context, "Insufficient coins ! please earn.", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {

        private TextView deleteNoticeTitle, date, solutiontxt,companyName;
        private ImageView deleteNoticeImage,companyImage;
        private final TemplateView nativeTemplateView;
        private String NATIVE_AD_ID;
        private LinearLayout fullsolution;
        private static final String TAG = "MyActivity";

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteNoticeTitle = itemView.findViewById(R.id.deleteNoticeTitle);
            deleteNoticeImage = itemView.findViewById(R.id.deleteNoticeImage);
            date = itemView.findViewById(R.id.date);
            fullsolution = itemView.findViewById(R.id.viewolutiontxt);
            solutiontxt = itemView.findViewById(R.id.Solutiontxt);
            nativeTemplateView = itemView.findViewById(R.id.nativeTemplateView);
            companyImage=itemView.findViewById(R.id.company_image);
            companyName=itemView.findViewById(R.id.company_name);

            if (MainActivity.isTestAd) {
                NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110";
            } else {
                NATIVE_AD_ID = "ca-app-pub-3197714952509994/9289274499";
            }

            if (HomeFragment.isAdActive) {
                nativeTemplateView.setVisibility(View.VISIBLE);
                MobileAds.initialize(itemView.getContext());
                AdLoader adLoader = new AdLoader.Builder(itemView.getContext(), NATIVE_AD_ID)
                        .forNativeAd(nativeAd -> {
                            ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(itemView.getContext(), R.color.white));
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
        }


    }


}

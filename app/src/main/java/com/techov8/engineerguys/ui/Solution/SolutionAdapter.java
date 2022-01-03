package com.techov8.engineerguys.ui.Solution;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;
import com.techov8.engineerguys.FullImageView;
import com.techov8.engineerguys.MainActivity;
import com.techov8.engineerguys.R;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;


import java.util.ArrayList;

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

        try {
            if (currentItem.getImage() != null)
                Picasso.get().load(currentItem.getImage()).into(holder.deleteNoticeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.deleteNoticeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullImageView.class);
                intent.putExtra("image", currentItem.getImage());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {

        private TextView deleteNoticeTitle, date;
        private ImageView deleteNoticeImage;
        private final TemplateView nativeTemplateView;
        private String NATIVE_AD_ID;
        private static final String TAG = "MyActivity";

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteNoticeTitle = itemView.findViewById(R.id.deleteNoticeTitle);
            deleteNoticeImage = itemView.findViewById(R.id.deleteNoticeImage);
            date = itemView.findViewById(R.id.date);
            nativeTemplateView = itemView.findViewById(R.id.nativeTemplateView);

            if (MainActivity.isTestAd) {
                NATIVE_AD_ID = "ca-app-pub-3940256099942544/2247696110";
            } else {
                NATIVE_AD_ID = "ca-app-pub-4594073781530728/7289476317";
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

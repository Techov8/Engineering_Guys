package com.techov8.engineerguys.ui.Jobs;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techov8.engineerguys.R;


import java.util.ArrayList;
import java.util.List;

public class JobFragment extends Fragment {


    private InterstitialAd mInterstitialAd;

    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String TAG = "MyActivity";

    private RecyclerView csDepartment;
    private LinearLayout csNoData;
    private List<JobData> list1 = new ArrayList<>();
    private JobAdapter adapter;

    private FirebaseFirestore db;

   // private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faculty, container, false);
        csDepartment = view.findViewById(R.id.csDepartment);
       // progressBar = view.findViewById(R.id.progressBar2);


        csNoData = view.findViewById(R.id.csNoData);


        db = FirebaseFirestore.getInstance();

        //progressBar.setVisibility(View.VISIBLE);

        csDepartment();


        ///////////////////////adsss
       // loadAd();


        //////////////////////


        return view;

    }


    private void csDepartment() {

        db.collection("Jobs").
                get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {


                                list1.add(new JobData(documentSnapshots.get("jobtitle").toString(), documentSnapshots.get("salary").toString(),
                                        documentSnapshots.get("post").toString(),
                                        documentSnapshots.get("image").toString(), documentSnapshots.get("additionalinfo").toString(),
                                        documentSnapshots.getId().toString(), documentSnapshots.get("link").toString()));

                            }

                           // progressBar.setVisibility(View.GONE);
                            csDepartment.setHasFixedSize(true);
                            csDepartment.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new JobAdapter(list1, getContext());
                            csDepartment.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }








}

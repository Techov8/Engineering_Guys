package com.techov8.engineerguys.ui.Solution;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SolutionFragment extends Fragment {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;
    private ArrayList<SolutionData> list= new ArrayList<>();;
    private SolutionAdapter adapter;

    private FirebaseFirestore db;
    private TextView nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        deleteNoticeRecycler = view.findViewById(R.id.deleteNoticeRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        nodata = view.findViewById(R.id.nodata);


        db = FirebaseFirestore.getInstance();

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deleteNoticeRecycler.setHasFixedSize(true);

        getNotice();

        return view;
    }

    private void getNotice() {

        db.collection("Newsfeed").
                get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()) {


                                list.add(0,new SolutionData(documentSnapshots.get("title").toString(), documentSnapshots.get("image").toString(),
                                        documentSnapshots.get("date").toString(),
                                        documentSnapshots.get("time").toString(), documentSnapshots.getId().toString()));

                            }


                            if (list.size() != 0) {
                                adapter = new SolutionAdapter(getContext(), list);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);

                                deleteNoticeRecycler.setAdapter(adapter);
                            } else {
                                progressBar.setVisibility(View.GONE);

                                nodata.setVisibility(View.VISIBLE);


                            }
                        } else {
                            Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                        }

                    }
                });




        ///////////////

    }

}

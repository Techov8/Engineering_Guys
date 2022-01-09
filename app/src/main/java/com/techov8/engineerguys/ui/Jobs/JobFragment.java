package com.techov8.engineerguys.ui.Jobs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.techov8.engineerguys.R;


import java.util.ArrayList;
import java.util.List;

public class JobFragment extends Fragment {


    private RecyclerView csDepartment;
    private final List<JobData> list1 = new ArrayList<>();
    private JobAdapter adapter;

    private FirebaseFirestore db;

    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faculty, container, false);
        csDepartment = view.findViewById(R.id.csDepartment);
        progressBar = view.findViewById(R.id.progressBar2);

        db = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.VISIBLE);

        csDepartment();


        return view;

    }


    private void csDepartment() {

        db.collection("Jobs").orderBy("server_time").get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            list1.add(new JobData(
                                    documentSnapshot.getString("title"),
                                    documentSnapshot.getString("salary"),
                                    documentSnapshot.getString("post"),
                                    documentSnapshot.getString("image"),
                                    documentSnapshot.getString("info"),
                                    documentSnapshot.getId(),
                                    documentSnapshot.getString("link"),
                                    documentSnapshot.getString("last_date")));


                        }
                        progressBar.setVisibility(View.GONE);
                        csDepartment.setHasFixedSize(true);
                        csDepartment.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new JobAdapter(list1, getContext());
                        csDepartment.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}

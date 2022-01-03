package com.techov8.engineerguys.ui.Solution;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techov8.engineerguys.R;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;

import java.util.ArrayList;


public class SolutionFragment extends Fragment {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;

    private SolutionAdapter adapter;
    private ArrayList<SolutionData> list = new ArrayList<>();
    private TextView nodata;
    private static int DATA = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        deleteNoticeRecycler = view.findViewById(R.id.deleteNoticeRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        nodata = view.findViewById(R.id.nodata);

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // deleteNoticeRecycler.setHasFixedSize(true);

        readData();

        return view;
    }

    private void readData() {

        FirebaseFirestore.getInstance().collection("Newsfeed").document("RJJp67t64krBQ1HQMe1u")

                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {


                        long no_of_answer = task.getResult().getLong("no_of_answer");

                        for (long y = 1; y < no_of_answer + 1; y++) {

                            list.add(new SolutionData(task.getResult().getString("question_details_" + y),
                                    task.getResult().getString("answer_image_" + y),
                                    task.getResult().getString("answer_time_" + y),
                                    task.getResult().getString("answer_text_" + y),
                                    task.getResult().getId()));

                        }

                        if (list.size() != 0) {
                            //adapter.notifyItemRangeRemoved(0,HomeFragment.list.size()-1);
                            adapter = new SolutionAdapter(getContext(), list);
                            deleteNoticeRecycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            progressBar.setVisibility(View.GONE);


                        } else {

                            progressBar.setVisibility(View.GONE);

                            nodata.setVisibility(View.VISIBLE);


                        }


                    }


                });
    }

}

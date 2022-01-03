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


import com.techov8.engineerguys.R;
import com.techov8.engineerguys.ui.AskQuestion.HomeFragment;


public class SolutionFragment extends Fragment {

    private RecyclerView deleteNoticeRecycler;
    private ProgressBar progressBar;

    private SolutionAdapter adapter;

    private TextView nodata;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        deleteNoticeRecycler = view.findViewById(R.id.deleteNoticeRecycler);
        progressBar = view.findViewById(R.id.progressBar);
        nodata = view.findViewById(R.id.nodata);

        deleteNoticeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        deleteNoticeRecycler.setHasFixedSize(true);

        if (HomeFragment.list.size() != 0) {
            adapter = new SolutionAdapter(getContext(), HomeFragment.list);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

            deleteNoticeRecycler.setAdapter(adapter);
        } else {
            progressBar.setVisibility(View.GONE);

            nodata.setVisibility(View.VISIBLE);


        }

        return view;
    }

}

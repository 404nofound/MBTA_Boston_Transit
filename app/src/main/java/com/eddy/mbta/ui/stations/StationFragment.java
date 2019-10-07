package com.eddy.mbta.ui.stations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.mbta.R;

import java.util.ArrayList;
import java.util.List;

public class StationFragment extends Fragment {

    private TrainAdapter adapter;
    private List<Integer> trainList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_train, container, false);

        init();



        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        adapter = new TrainAdapter(trainList);
        recyclerView.setAdapter(adapter);

        return root;
    }

    public void init() {
        for (int i = 0; i < 8; i++) {
            trainList.add(i);
        }
    }
}
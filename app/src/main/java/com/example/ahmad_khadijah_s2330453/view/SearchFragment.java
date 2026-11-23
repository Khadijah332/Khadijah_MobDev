package com.example.ahmad_khadijah_s2330453.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;

import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.ahmad_khadijah_s2330453.R;
import com.example.ahmad_khadijah_s2330453.model.Situation;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //spinner for filtering between unplanned event or roadworks
        Spinner spinner = view.findViewById(R.id.typeSpinner);
        SearchView searchView = view.findViewById(R.id.searchView);

        String[] options = {"All Updates", "Incidents", "Roadworks"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                options
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        //find recyler view
        RecyclerView rv = view.findViewById(R.id.recView);

        //get downloaded situations list
        MainActivity mainActivity = (MainActivity) requireActivity();
        ArrayList<Situation> data = mainActivity.getAllSituations();
        int roadworksCount = 0;

        for (Situation situation : data) {
            if ("Roadworks".equals(situation.getFeedType())) {
                roadworksCount++;
            }
        }

        android.util.Log.d("Traffic", "Roadworks found: " + roadworksCount);

        //create adapter
        SituationAdapter myAdapter = new SituationAdapter(data);

        //set up layout manager
        LinearLayoutManager llm = new LinearLayoutManager(requireContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(llm);
        rv.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        ArrayList<Situation> filtered = new ArrayList<>();

                        for (Situation situation : data) {

                            if (position == 0) {
                                //all updates
                                filtered.add(situation);

                            } else if (position == 1 &&
                                    "Unplanned Events".equals(situation.getFeedType())) {
                                //incidents
                                filtered.add(situation);

                            } else if (position == 2 &&
                                    "Roadworks".equals(situation.getFeedType())) {
                                //roadworks
                                filtered.add(situation);
                            }
                        }

                        rv.setAdapter(new SituationAdapter(filtered));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Situation> filtered = new ArrayList<>();

                for (Situation situation : data) {
                    String roadName = situation.getRoadName();
                    String description = situation.getDescription();
                    //search for roadname and also matches words in description
                    if ((roadName != null &&
                            roadName.toLowerCase().contains(newText.toLowerCase()))
                            ||
                            (description != null &&
                                    description.toLowerCase().contains(newText.toLowerCase()))) {

                        filtered.add(situation);
                    }
                }

                rv.setAdapter(new SituationAdapter(filtered));
                return true;
            }

        });
        return view;
    }

}

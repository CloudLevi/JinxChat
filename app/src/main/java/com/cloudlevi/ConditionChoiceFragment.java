package com.cloudlevi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


public class ConditionChoiceFragment extends Fragment {

    public ConditionChoiceFragment() {
        // Required empty public constructor
    }

    private ListView conditionList;
    private ArrayAdapter<String> adapter;
    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_condition_choice, container, false);

        ArrayList<String> conditions = new ArrayList<>();

        conditions.add("New with tags");
        conditions.add("New without tags");
        conditions.add("Almost new");
        conditions.add("In good condition");
        conditions.add("In worn condition");
        conditions.add("In bad condition");


        adapter = new ArrayAdapter(getContext(), R.layout.listview_item_layout, conditions);
        conditionList.setAdapter(adapter);


        conditionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navController = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putString("condition_choiceArgument", adapter.getItem(position));

                navController.navigate(R.id.action_conditionChoiceFragment_to_addFragment, bundle);


            }
        });

        return v;
    }
}

package com.JinxMarket;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ConditionChoiceFragment extends Fragment {

    public ConditionChoiceFragment() {
        // Required empty public constructor
    }

    private ListView conditionList;
    private ArrayAdapter<String> adapter;
    private NavController navController;
    private String initFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_condition_choice, container, false);

        if (getArguments() != null) {
            initFragment = getArguments().getString("Initial_Fragment");
        }

        conditionList = v.findViewById(R.id.conditionList);

        ArrayList<String> conditions = new ArrayList<>();

        conditions.add("New with tags");
        conditions.add("New, no tags");
        conditions.add("Almost new");
        conditions.add("Good condition");
        conditions.add("Worn condition");
        conditions.add("Bad condition");



        adapter = new ArrayAdapter(getContext(), R.layout.listview_item_layout, conditions);
        conditionList.setAdapter(adapter);


        conditionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navController = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putString("condition_choiceArgument", adapter.getItem(position));

                switch (initFragment){
                    case "Add_Fragment":
                        navController.navigate(R.id.action_conditionChoiceFragment_to_addFragment, bundle);
                        break;
                    case "Edit_Fragment":
                        navController.navigate(R.id.action_conditionChoiceFragment_to_marketItemEditFragment, bundle);
                }


            }
        });

        return v;
    }
}

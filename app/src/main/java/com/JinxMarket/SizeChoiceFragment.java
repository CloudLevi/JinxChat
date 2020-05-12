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


public class SizeChoiceFragment extends Fragment {

    public SizeChoiceFragment() {
        // Required empty public constructor
    }

    private ListView priceList;
    private ArrayAdapter<String> adapter;
    private NavController navController;
    private String initFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_size_choice, container, false);

        if (getArguments() != null) {
            initFragment = getArguments().getString("Initial_Fragment");
        }

        priceList = v.findViewById(R.id.sizeList);

        ArrayList<String> sizes = new ArrayList<>();

        sizes.add("XXL");
        sizes.add("XL");
        sizes.add("L");
        sizes.add("M");
        sizes.add("S");
        sizes.add("XS");

        sizes.add("36");
        sizes.add("36,5");
        sizes.add("37");
        sizes.add("37,5");
        sizes.add("38");
        sizes.add("38,5");
        sizes.add("39");
        sizes.add("39,5");
        sizes.add("40");
        sizes.add("40,5");
        sizes.add("41");
        sizes.add("41,5");
        sizes.add("42");
        sizes.add("42,5");
        sizes.add("43");
        sizes.add("43,5");
        sizes.add("44");
        sizes.add("44,5");
        sizes.add("45");
        sizes.add("45,5");
        sizes.add("46");


        adapter = new ArrayAdapter(getContext(), R.layout.listview_item_layout, sizes);
        priceList.setAdapter(adapter);


        priceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navController = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putString("size_choiceArgument", adapter.getItem(position));

                switch (initFragment){
                    case "Add_Fragment":
                        navController.navigate(R.id.action_sizeChoiceFragment_to_addFragment, bundle);
                        break;
                    case "Edit_Fragment":
                        navController.navigate(R.id.action_sizeChoiceFragment_to_marketItemEditFragment, bundle);
                }


            }
        });

        return v;
    }
}

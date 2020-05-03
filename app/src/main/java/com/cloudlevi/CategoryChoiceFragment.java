package com.cloudlevi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CategoryChoiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private NavController navController;
    private Bundle bundle = new Bundle();

    private ListView categoriesList;

    private EditText search_category;

    private ArrayAdapter<String> adapter;



    public CategoryChoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_category_choice, container, false);

        categoriesList = v.findViewById(R.id.categoriesList);
        search_category = v.findViewById(R.id.search_category);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Accessories");
        categories.add("Hats");
        categories.add("Sunglasses");
        categories.add("Ties");
        categories.add("Scarves");
        categories.add("Belts");
        categories.add("Socks");

        categories.add("Outwear");
        categories.add("Jackets");
        categories.add("Coats");
        categories.add("Trench Coats");
        categories.add("Vests");
        categories.add("Suits");
        categories.add("Blazers");

        categories.add("Top");
        categories.add("Sweaters");
        categories.add("Hoodies");
        categories.add("Tank tops");
        categories.add("T-Shirts");
        categories.add("Shirts");

        categories.add("Bottom");
        categories.add("Jeans");
        categories.add("Cargo pants");
        categories.add("Shorts");
        categories.add("Dress pants");
        categories.add("Sweatpants");
        categories.add("Skirts");
        categories.add("Dresses");

        categories.add("Shoes");
        categories.add("Sandals");
        categories.add("Sneakers");
        categories.add("Flats");
        categories.add("Heels");
        categories.add("Slippers");
        categories.add("Boots");
        categories.add("Rain boots");



        adapter = new ArrayAdapter(getContext(), R.layout.listview_item_layout, categories);
        categoriesList.setAdapter(adapter);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       navController = Navigation.findNavController(view);

        search_category.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                navController = Navigation.findNavController(view);
                bundle.putString("category_choiceArgument", adapter.getItem(position));

                navController.navigate(R.id.action_categoryChoiceFragment_to_addFragment, bundle);


            }
        });
    }
}

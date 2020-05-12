package com.JinxMarket;

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
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class BrandChoiceFragment extends Fragment {

    public static final String PAGE_TITLE = "Tab2";
    private ListView brandlist;
    private EditText search;
    private ArrayAdapter adapter;
    private NavController navController;

    private String initFragment;

    public BrandChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_brand_choice, container, false);

        if (getArguments() != null) {
            initFragment = getArguments().getString("Initial_Fragment");
        }

        brandlist = v.findViewById(R.id.brandlist);
        search = v.findViewById(R.id.search_brand);

        ArrayList<String> brands = new ArrayList<>();
        brands.add("Other");
        brands.add("Adidas");
        brands.add("A.P.C.");
        brands.add("Acne Studios");
        brands.add("Alexander Wang");
        brands.add("Asos");
        brands.add("Asics");
        brands.add("Balenciaga");
        brands.add("Ben Sherman");
        brands.add("Bershka");
        brands.add("Calvin Klein");
        brands.add("Comme Des Garcons");
        brands.add("DC");
        brands.add("Dolce & Gabbana");
        brands.add("Dsquaured2");
        brands.add("Etro");
        brands.add("Stone Island");
        brands.add("Gucci");
        brands.add("Louis Vuitton");
        brands.add("Yves Saint Laurent");

        adapter = new ArrayAdapter(getContext(), R.layout.listview_item_layout, brands);
        brandlist.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {
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

        brandlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                navController = Navigation.findNavController(view);

                Bundle bundle = new Bundle();
                bundle.putString("brand_choiceArgument", adapter.getItem(position).toString());

                switch (initFragment){
                    case "Add_Fragment":
                        navController.navigate(R.id.action_brandChoiceFragment_to_addFragment, bundle);
                        break;
                    case "Edit_Fragment":
                        navController.navigate(R.id.action_brandChoiceFragment_to_marketItemEditFragment, bundle);
                }



            }
        });

        return v;
    }
}

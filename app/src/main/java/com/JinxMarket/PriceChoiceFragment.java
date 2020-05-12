package com.JinxMarket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class PriceChoiceFragment extends Fragment {

    private NavController navController;

    private EditText price_editText;
    private TextView price_calcTV;
    private CardView confirmBTN;

    private String initFragment;

    private Bundle bundle = new Bundle();

    public PriceChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_price_choice, container, false);


        price_editText = v.findViewById(R.id.priceEditText);
        price_calcTV = v.findViewById(R.id.price_choiceCalcTV);
        confirmBTN = v.findViewById(R.id.confirm_priceBtn);

        if (getArguments() != null) {

            String price_choiceArgument;
            price_choiceArgument = getArguments().getString("price_choiceArgument");
            initFragment = getArguments().getString("Initial_Fragment");
            price_editText.setText(price_choiceArgument);
            calculatePrice();
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        price_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculatePrice();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String priceText = price_editText.getText().toString();

                bundle.putString("price_choiceArgument", priceText);

                switch (initFragment){
                    case "Add_Fragment":
                        navController.navigate(R.id.action_priceChoiceFragment_to_addFragment, bundle);
                        break;
                    case "Edit_Fragment":
                        navController.navigate(R.id.action_priceChoiceFragment_to_marketItemEditFragment, bundle);
                }
            }
        });

    }

    private void calculatePrice() {
        CharSequence s = price_editText.getText();
        if(!s.toString().isEmpty()){

            float price = Float.parseFloat(s.toString());

            price = Math.round((price*0.9));
            String finalPrice = String.valueOf(price).substring(0, String.valueOf(price).length() - 2);
            String finalPriceWithDollar = finalPrice + " $";
            price_calcTV.setText(finalPriceWithDollar);
        } else{
            price_calcTV.setText("0$");
        }

    }
}

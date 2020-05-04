package com.cloudlevi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class UserAboutMeEditFragment extends Fragment {

    private EditText mBioEditText;
    private Spinner mGenderSpinner;
    private CardView mBirthDayButton;
    private TextView mBirthDayTextView;
    private Calendar calendar = Calendar.getInstance();
    private EditText mCountryEditText;
    private EditText mCityEditText;
    private CardView mUpload;

    private String mUserBio;
    private String mUserGender;
    private String mUserBirthday;
    private String mUserCountry;
    private String mUserCity;

    private DatabaseReference mDataBaseRef;
    private String userID;
    private UserAboutMeModel oldUserInfo;

    public UserAboutMeEditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_about_me_edit, container, false);

        mBioEditText = v.findViewById(R.id.userInfo_bio_ed);
        mGenderSpinner = v.findViewById(R.id.gender_choice);
        mBirthDayButton = v.findViewById(R.id.birthdayBTN);
        mBirthDayTextView = v.findViewById(R.id.userInfo_bday_tv);
        mCountryEditText = v.findViewById(R.id.country_choice_ed);
        mCityEditText = v.findViewById(R.id.city_choice_ed);
        mUpload = v.findViewById(R.id.applyChangesBTN);

        if(getArguments() != null){
            userID = getArguments().getString("userID");
        }

        mDataBaseRef = FirebaseDatabase.getInstance().getReference("Users/" + userID + "/UserDetails");

        mDataBaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    oldUserInfo = dataSnapshot.getValue(UserAboutMeModel.class);
                    if(oldUserInfo.getBioModel() != null){mBioEditText.setText(oldUserInfo.getBioModel());}
                    if(oldUserInfo.getBirthdayModel() != null){mBirthDayTextView.setText(oldUserInfo.getBirthdayModel());}
                    if(oldUserInfo.getCountryModel() != null){mCountryEditText.setText(oldUserInfo.getCountryModel());}
                    if(oldUserInfo.getCityModel() != null){mCityEditText.setText(oldUserInfo.getCityModel());}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUserBio = s.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mUserGender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mUserGender = "Not selected";
            }
        });

        mBirthDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        setBirthDayString();
                    }
                };
                DatePickerDialog dateDialog = new DatePickerDialog(getContext(), dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                dateDialog.show();
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAboutMeModel newInfo = new UserAboutMeModel(
                        mUserBio,
                        mUserGender,
                        mBirthDayTextView.getText().toString().trim(),
                        mCountryEditText.getText().toString().trim(),
                        mCityEditText.getText().toString().trim()
                );
                mDataBaseRef.setValue(newInfo);
                Toast.makeText(getContext(), "Info updated", Toast.LENGTH_SHORT).show();
                final NavController navController = Navigation.findNavController(v);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        navController.navigate(R.id.action_userAboutMeEditFragment_to_profileFragment);
                    }
                }, 500);
            }
        });
    }

    private void setBirthDayString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
        Date d = new Date(calendar.getTimeInMillis());
        mUserBirthday = sdf.format(d);
        mBirthDayTextView.setText(mUserBirthday);

    }


}

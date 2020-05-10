package com.cloudlevi;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    private CardView mButtonChooseFile;
    private CardView mButtonUpload;

    private ScrollView addScroll;

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDataBaseRef;
    private DatabaseReference mDataBaseRefUser;

    private RelativeLayout cat_choiceLayout;
    private RelativeLayout brand_choiceLayout;
    private RelativeLayout size_choiceLayout;
    private RelativeLayout condition_choiceLayout;
    private RelativeLayout price_choiceLayout;

    private StorageTask mUploadTask;

    private  NavController navController;

    private TextView category_choiceTV;
    private TextView brand_choiceTV;
    private TextView size_choiceTV;
    private TextView condition_choiceTV;
    private TextView price_choiceTV;

    private Bundle bundle = new Bundle();

    private AddFragmentViewModel viewModel;

    private DatabaseReference userNameReference;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private String fireBaseUserId;
    private String fireBaseUserName;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_add, container, false);

        bundle.putString("Initial_Fragment", "Add_Fragment");

        mButtonChooseFile = v.findViewById(R.id.choosebtn);
        mButtonUpload = v.findViewById(R.id.uploadbtn);


        mEditTextTitle = v.findViewById(R.id.title_ed);
        mEditTextDescription = v.findViewById(R.id.descr_ed);

        mImageView = v.findViewById(R.id.image);
        mProgressBar = v.findViewById(R.id.progress_bar);
        fireBaseUserId = firebaseUser.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        mDataBaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mDataBaseRefUser = FirebaseDatabase.getInstance().getReference("Users/" + fireBaseUserId + "/UserUploads");

        cat_choiceLayout = v.findViewById(R.id.category_choice);
        brand_choiceLayout = v.findViewById(R.id.brand_choice);
        size_choiceLayout = v.findViewById(R.id.size_choice);
        condition_choiceLayout = v.findViewById(R.id.condition_choice);
        price_choiceLayout = v.findViewById(R.id.price_choice);

        category_choiceTV = v.findViewById(R.id.category_choiceTV);
        brand_choiceTV = v.findViewById(R.id.brand_choiceTV);
        size_choiceTV = v.findViewById(R.id.size_choiceTV);
        condition_choiceTV = v.findViewById(R.id.condition_choiceTV);
        price_choiceTV = v.findViewById(R.id.price_choiceTV);

        addScroll = v.findViewById(R.id.add_scroll);

        userNameReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fireBaseUserName = dataSnapshot.child(fireBaseUserId).child("username").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()){
                    Toast.makeText(getContext(), "An upload is in progress", Toast.LENGTH_SHORT).show();
                } else {
                    if(
                            mEditTextTitle.getText().toString().equals("") ||
                                    mEditTextDescription.getText().toString().equals("") ||
                                    category_choiceTV.getText().toString().equals("") ||
                                    brand_choiceTV.getText().toString().equals("") ||
                                    size_choiceTV.getText().toString().equals("") ||
                                    condition_choiceTV.getText().toString().equals("") ||
                                    price_choiceTV.getText().toString().equals("")){
                        Toast.makeText(getContext(), "Fill in all the fields", Toast.LENGTH_SHORT).show();
                    } else{
                        uploadFile();
                    }

                }
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AddFragmentViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);



        if(viewModel.getData().getValue() != null){

            AddFragmentModel addFragmentModel = viewModel.getData().getValue();


            Bitmap image_bitmap = ((BitmapDrawable)addFragmentModel.getImageModel().getDrawable()).getBitmap();

            final int scrollPositionY = addFragmentModel.getScrollPositionY();

            addScroll.postDelayed(new Runnable() {
                @Override public void run()
                { addScroll.scrollTo(0, scrollPositionY);
                } }, 0);


            mImageView.setImageBitmap(image_bitmap);
            if(addFragmentModel.getImageUri() != null){
                mImageUri = addFragmentModel.getImageUri();
            }

            mEditTextTitle.setText(addFragmentModel.getTitleModel());
            mEditTextDescription.setText(addFragmentModel.getDescriptionModel());

            if (getArguments() != null) {

                if (getArguments().getString("category_choiceArgument") != null) {
                    category_choiceTV.setText(getArguments().getString("category_choiceArgument"));
                } else {
                    category_choiceTV.setText(addFragmentModel.getCategoryModel());
                }
                if (getArguments().getString("brand_choiceArgument") != null) {
                    brand_choiceTV.setText(getArguments().getString("brand_choiceArgument"));
                } else {
                    brand_choiceTV.setText(addFragmentModel.getBrandModel());
                }
                if (getArguments().getString("size_choiceArgument") != null) {
                    size_choiceTV.setText(getArguments().getString("size_choiceArgument"));
                } else {
                    size_choiceTV.setText(addFragmentModel.getSizeModel());
                }
                if (getArguments().getString("condition_choiceArgument") != null) {
                    condition_choiceTV.setText(getArguments().getString("condition_choiceArgument"));
                } else {
                    condition_choiceTV.setText(addFragmentModel.getConditionModel());
                }
                if (getArguments().getString("price_choiceArgument") != null) {
                    price_choiceTV.setText(addDollarSign(getArguments().getString("price_choiceArgument")));
                } else {
                    price_choiceTV.setText(addDollarSign(addFragmentModel.getPriceModel()));
                }
            }else{
                category_choiceTV.setText(addFragmentModel.getCategoryModel());
                brand_choiceTV.setText(addFragmentModel.getBrandModel());
                size_choiceTV.setText(addFragmentModel.getSizeModel());
                condition_choiceTV.setText(addFragmentModel.getConditionModel());
                price_choiceTV.setText(addDollarSign(addFragmentModel.getPriceModel()));
            }

        }

        cat_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category_choiceTV.getText().toString().isEmpty()) {
                    navController.navigate(R.id.action_addFragment_to_categoryChoiceFragment, bundle);
                }
                else{
                    bundle.putString("category_choiceArgument", category_choiceTV.getText().toString());
                    navController.navigate(R.id.action_addFragment_to_categoryChoiceFragment,
                            bundle);
                }
            }
        });

        brand_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addFragment_to_brandChoiceFragment, bundle);
            }
        });

        size_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addFragment_to_sizeChoiceFragment, bundle);
            }
        });

        condition_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addFragment_to_conditionChoiceFragment, bundle);
            }
        });

        price_choiceLayout.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if(price_choiceTV.getText().toString().isEmpty()) {
                    navController.navigate(R.id.action_addFragment_to_priceChoiceFragment, bundle);
                }
                else{
                    bundle.putString("price_choiceArgument", removeOneCharacter(price_choiceTV.getText().toString()));
                    navController.navigate(R.id.action_addFragment_to_priceChoiceFragment,
                            bundle);
                }
            }
        });


    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();

            Picasso.get()
                    .load(mImageUri)
                    .into(mImageView);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null){

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() +
                    "." + getFileExtension(mImageUri));


            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            final String uploadID = mDataBaseRef.push().getKey();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    AddFragmentModel upload = new AddFragmentModel(mEditTextTitle.getText().toString().trim(),
                                            uri.toString(),
                                            mEditTextDescription.getText().toString().trim(),
                                            category_choiceTV.getText().toString().trim(),
                                            brand_choiceTV.getText().toString().trim(),
                                            size_choiceTV.getText().toString().trim(),
                                            condition_choiceTV.getText().toString().trim(),
                                            price_choiceTV.getText().toString().trim(),
                                            fireBaseUserName,
                                            fireBaseUserId,
                                            uploadID);


                                    mDataBaseRef.child(uploadID).setValue(upload);
                                    mDataBaseRefUser.child(uploadID).child("uploadID").setValue(uploadID);

                                    Bundle privateBundle = new Bundle();
                                    privateBundle.putParcelable("item", upload);
                                    navController.navigate(R.id.action_addFragment_to_marketItemFragment, privateBundle);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }
        else{
            Toast.makeText(getContext(), "No File Selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AddFragmentModel addFragmentModel = new AddFragmentModel();

        if(mEditTextTitle.getText().toString().equals("")){
            addFragmentModel.setTitleModel("");
        }else{addFragmentModel.setTitleModel(mEditTextTitle.getText().toString());}

        if(mEditTextDescription.getText().toString().equals("")){
            addFragmentModel.setDescriptionModel("");
        }else{addFragmentModel.setDescriptionModel(mEditTextDescription.getText().toString());}

        if(category_choiceTV.getText().equals("")){
            addFragmentModel.setCategoryModel("");
        }else{addFragmentModel.setCategoryModel(category_choiceTV.getText().toString());}

        if(brand_choiceTV.getText().equals("")){
            addFragmentModel.setBrandModel("");
        }else{addFragmentModel.setBrandModel(brand_choiceTV.getText().toString());}

        if(size_choiceTV.getText().equals("")){
            addFragmentModel.setSizeModel("");
        }else{addFragmentModel.setSizeModel(size_choiceTV.getText().toString());}

        if(condition_choiceTV.getText().equals("")){
            addFragmentModel.setConditionModel("");
        }else{addFragmentModel.setConditionModel(condition_choiceTV.getText().toString());}

        if(mImageView == null){
            addFragmentModel.setImageModel(null);
        }else{addFragmentModel.setImageModel(mImageView);}

        if(price_choiceTV.getText().equals("")){
            addFragmentModel.setPriceModel("");
        }else{addFragmentModel.setPriceModel(removeOneCharacter(price_choiceTV.getText().toString()));}

        if(mImageUri != null){
            addFragmentModel.setImageUri(mImageUri);
        }

        addFragmentModel.setScrollPositionY(addScroll.getScrollY());


        viewModel.setData(addFragmentModel);
    }

    private String addDollarSign(String s){
        String sUpdated;
        if(s.isEmpty()){
            sUpdated = s;
        }else{
            sUpdated = s + "$";
        }
        return sUpdated;
    }

    private String removeOneCharacter(String s){
        return s.substring(0, s.length()-1);
    }


}

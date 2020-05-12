package com.JinxMarket;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
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

public class MarketItemEditFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int RESULT_OK = -1;

    private Uri mImageUri;

    private CardView mButtonChooseFile;
    private CardView mButtonApply;


    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private ImageView mImageView;
    private ProgressBar mProgressBar;


    private RelativeLayout cat_choiceLayout;
    private RelativeLayout brand_choiceLayout;
    private RelativeLayout size_choiceLayout;
    private RelativeLayout condition_choiceLayout;
    private RelativeLayout price_choiceLayout;


    private NavController navController;
    private Bundle bundle = new Bundle();

    private TextView category_choiceTV;
    private TextView brand_choiceTV;
    private TextView size_choiceTV;
    private TextView condition_choiceTV;
    private TextView price_choiceTV;

    private AddFragmentModel initialFragmentModel;
    private MarketItemEditFragmentViewModel viewModel;

    private DatabaseReference mDataBaseReference;
    private StorageReference mStorageReference;
    private FirebaseStorage mFireBaseStorage;
    private StorageTask mUploadTask;

    private String mOldDownloadURL;
    private String uploadID;
    private String fireBaseUserName;
    private String fireBaseUserId;

    private AddFragmentModel uploadObject;

    public MarketItemEditFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MarketItemEditFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market_item_edit, container, false);
        bundle.putString("Initial_Fragment", "Edit_Fragment");

        mButtonChooseFile = v.findViewById(R.id.market_edit_choosebtn);
        mButtonApply = v.findViewById(R.id.market_edit_ApplyBtn);

        mEditTextTitle = v.findViewById(R.id.market_edit_title_ed);
        mEditTextDescription = v.findViewById(R.id.market_edit_descr_ed);

        mImageView = v.findViewById(R.id.market_edit_image);
        mProgressBar = v.findViewById(R.id.market_edit_progress_bar);

        cat_choiceLayout = v.findViewById(R.id.market_edit_category_choice);
        brand_choiceLayout = v.findViewById(R.id.market_edit_brand_choice);
        size_choiceLayout = v.findViewById(R.id.size_choice);
        condition_choiceLayout = v.findViewById(R.id.market_edit_condition_choice);
        price_choiceLayout = v.findViewById(R.id.market_edit_price_choice);

        category_choiceTV = v.findViewById(R.id.market_edit_category_choiceTV);
        brand_choiceTV = v.findViewById(R.id.market_edit_brand_choiceTV);
        size_choiceTV = v.findViewById(R.id.size_choiceTV);
        condition_choiceTV = v.findViewById(R.id.market_edit_condition_choiceTV);
        price_choiceTV = v.findViewById(R.id.market_edit_price_choiceTV);

        mFireBaseStorage = FirebaseStorage.getInstance();

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");

        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        if(getArguments() != null && getArguments().getParcelable("item") != null){
            initialFragmentModel = getArguments().getParcelable("item");

            uploadID = initialFragmentModel.getUploadIDModel();
            mOldDownloadURL = initialFragmentModel.getImageURL();
            fireBaseUserName = initialFragmentModel.getUsernameModel();
            fireBaseUserId = initialFragmentModel.getUserIdModel();

            mDataBaseReference = FirebaseDatabase.getInstance().getReference("uploads/" + uploadID);

            Picasso.get()
                    .load(initialFragmentModel.getImageURL())
                    .into(mImageView);

            mEditTextTitle.setText(initialFragmentModel.getTitleModel());
            mEditTextDescription.setText(initialFragmentModel.getDescriptionModel());

            category_choiceTV.setText(initialFragmentModel.getCategoryModel());
            brand_choiceTV.setText(initialFragmentModel.getBrandModel());
            size_choiceTV.setText(initialFragmentModel.getSizeModel());
            condition_choiceTV.setText(initialFragmentModel.getConditionModel());
            price_choiceTV.setText(initialFragmentModel.getPriceModel());
        }
        else {
            if (viewModel.getData().getValue() != null) {

                AddFragmentModel editFragmentModel = viewModel.getData().getValue();

                Bitmap image_bitmap = ((BitmapDrawable) editFragmentModel.getImageModel().getDrawable()).getBitmap();

                mOldDownloadURL = editFragmentModel.getImageURL();
                uploadID = editFragmentModel.getUploadIDModel();
                fireBaseUserName = editFragmentModel.getUsernameModel();
                fireBaseUserId = editFragmentModel.getUserIdModel();

                mDataBaseReference = FirebaseDatabase.getInstance().getReference("uploads/" + uploadID);

                mImageView.setImageBitmap(image_bitmap);
                if (editFragmentModel.getImageUri() != null) {
                    mImageUri = editFragmentModel.getImageUri();
                }

                mEditTextTitle.setText(editFragmentModel.getTitleModel());
                mEditTextDescription.setText(editFragmentModel.getDescriptionModel());

                if (getArguments() != null) {


                    if (getArguments().getString("category_choiceArgument") != null) {
                        category_choiceTV.setText(getArguments().getString("category_choiceArgument"));
                    } else {
                        category_choiceTV.setText(editFragmentModel.getCategoryModel());
                    }
                    if (getArguments().getString("brand_choiceArgument") != null) {
                        brand_choiceTV.setText(getArguments().getString("brand_choiceArgument"));
                    } else {
                        brand_choiceTV.setText(editFragmentModel.getBrandModel());
                    }
                    if (getArguments().getString("size_choiceArgument") != null) {
                        size_choiceTV.setText(getArguments().getString("size_choiceArgument"));
                    } else {
                        size_choiceTV.setText(editFragmentModel.getSizeModel());
                    }
                    if (getArguments().getString("condition_choiceArgument") != null) {
                        condition_choiceTV.setText(getArguments().getString("condition_choiceArgument"));
                    } else {
                        condition_choiceTV.setText(editFragmentModel.getConditionModel());
                    }
                    if (getArguments().getString("price_choiceArgument") != null) {
                        price_choiceTV.setText(addDollarSign(getArguments().getString("price_choiceArgument")));
                    } else {
                        price_choiceTV.setText(addDollarSign(editFragmentModel.getPriceModel()));
                    }
                } else {
                    category_choiceTV.setText(editFragmentModel.getCategoryModel());
                    brand_choiceTV.setText(editFragmentModel.getBrandModel());
                    size_choiceTV.setText(editFragmentModel.getSizeModel());
                    condition_choiceTV.setText(editFragmentModel.getConditionModel());
                    price_choiceTV.setText(addDollarSign(editFragmentModel.getPriceModel()));
                }
            }
        }

        cat_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_marketItemEditFragment_to_categoryChoiceFragment, bundle);
            }
        });
        brand_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_marketItemEditFragment_to_brandChoiceFragment, bundle);
            }
        });
        size_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_marketItemEditFragment_to_sizeChoiceFragment, bundle);
            }
        });
        condition_choiceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_marketItemEditFragment_to_conditionChoiceFragment, bundle);
            }
        });
        price_choiceLayout.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if(price_choiceTV.getText().toString().isEmpty()) {
                    navController.navigate(R.id.action_marketItemEditFragment_to_priceChoiceFragment, bundle);
                }
                else{
                    bundle.putString("price_choiceArgument", removeOneCharacter(price_choiceTV.getText().toString()));
                    navController.navigate(R.id.action_marketItemEditFragment_to_priceChoiceFragment,
                            bundle);
                }
            }
        });

        mButtonApply.setOnClickListener(new View.OnClickListener() {
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
                        mProgressBar.setVisibility(View.VISIBLE);
                        uploadFile();
                    }}

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AddFragmentModel editFragmentModel = new AddFragmentModel();

        if(mEditTextTitle.getText().toString().equals("")){
            editFragmentModel.setTitleModel("");
        }else{editFragmentModel.setTitleModel(mEditTextTitle.getText().toString());}

        if(mEditTextDescription.getText().toString().equals("")){
            editFragmentModel.setDescriptionModel("");
        }else{editFragmentModel.setDescriptionModel(mEditTextDescription.getText().toString());}

        if(category_choiceTV.getText().equals("")){
            editFragmentModel.setCategoryModel("");
        }else{editFragmentModel.setCategoryModel(category_choiceTV.getText().toString());}

        if(brand_choiceTV.getText().equals("")){
            editFragmentModel.setBrandModel("");
        }else{editFragmentModel.setBrandModel(brand_choiceTV.getText().toString());}

        if(size_choiceTV.getText().equals("")){
            editFragmentModel.setSizeModel("");
        }else{editFragmentModel.setSizeModel(size_choiceTV.getText().toString());}

        if(condition_choiceTV.getText().equals("")){
            editFragmentModel.setConditionModel("");
        }else{editFragmentModel.setConditionModel(condition_choiceTV.getText().toString());}

        if(mImageView == null){
            editFragmentModel.setImageModel(null);
        }else{editFragmentModel.setImageModel(mImageView);}

        if(price_choiceTV.getText().equals("")){
            editFragmentModel.setPriceModel("");
        }else{editFragmentModel.setPriceModel(removeOneCharacter(price_choiceTV.getText().toString()));}

        if(mImageUri != null){
            editFragmentModel.setImageUri(mImageUri);
        }

        editFragmentModel.setImageURL(mOldDownloadURL);
        editFragmentModel.setUploadIDModel(uploadID);
        editFragmentModel.setUsernameModel(fireBaseUserName);
        editFragmentModel.setUserIdModel(fireBaseUserId);

        viewModel.setData(editFragmentModel);
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

    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null){

            final StorageReference fileReference = mStorageReference.child(System.currentTimeMillis() +
                    "." + getFileExtension(mImageUri));


            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    StorageReference mPhotoRef = mFireBaseStorage.getReferenceFromUrl(mOldDownloadURL);
                                    mPhotoRef.delete();
                                    uploadObject = new AddFragmentModel(mEditTextTitle.getText().toString().trim(),
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
                                    mDataBaseReference.setValue(uploadObject);

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("item", uploadObject);
                                    navController.navigate(R.id.action_marketItemEditFragment_to_marketItemFragment, bundle);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            uploadObject = new AddFragmentModel(mEditTextTitle.getText().toString().trim(),
                    mOldDownloadURL,
                    mEditTextDescription.getText().toString().trim(),
                    category_choiceTV.getText().toString().trim(),
                    brand_choiceTV.getText().toString().trim(),
                    size_choiceTV.getText().toString().trim(),
                    condition_choiceTV.getText().toString().trim(),
                    price_choiceTV.getText().toString().trim(),
                    fireBaseUserName,
                    fireBaseUserId,
                    uploadID);
            mDataBaseReference.setValue(uploadObject);

            Bundle bundle = new Bundle();
            bundle.putParcelable("item", uploadObject);
            navController.navigate(R.id.action_marketItemEditFragment_to_marketItemFragment, bundle);
        }

    }
}

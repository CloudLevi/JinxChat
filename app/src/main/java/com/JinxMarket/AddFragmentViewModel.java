package com.JinxMarket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddFragmentViewModel extends ViewModel {

    private MutableLiveData<AddFragmentModel> addFragmentModel = new MutableLiveData<>();

    public void setData(AddFragmentModel model){
        addFragmentModel.setValue(model);
    }

    public LiveData<AddFragmentModel> getData(){
        return addFragmentModel;
    }


}

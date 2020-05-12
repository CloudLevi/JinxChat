package com.JinxMarket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MarketItemEditFragmentViewModel extends ViewModel {

    private MutableLiveData<AddFragmentModel> editFragmentModel = new MutableLiveData<>();

    public void setData(AddFragmentModel model){
        editFragmentModel.setValue(model);
    }

    public LiveData<AddFragmentModel> getData(){
        return editFragmentModel;
    }
}

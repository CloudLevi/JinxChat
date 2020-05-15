package com.JinxMarket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeletingViewModel extends ViewModel {

    private MutableLiveData<Boolean> deleteState = new MutableLiveData<>();

    public void setData(Boolean model){
        deleteState.setValue(model);
    }

    public LiveData<Boolean> getData(){
        return deleteState;
    }
}

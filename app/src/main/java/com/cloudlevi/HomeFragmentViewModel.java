package com.cloudlevi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeFragmentViewModel extends ViewModel {

    private MutableLiveData<HomeFragmentModel> homeFragmentModel = new MutableLiveData<>();

    public void setData(HomeFragmentModel model){homeFragmentModel.setValue(model);}

    public LiveData<HomeFragmentModel> getData(){
        return homeFragmentModel;
    }

}

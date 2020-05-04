package com.cloudlevi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
public class UserPagerAdapterFragment extends Fragment {

    public static final String PAGE_TITLE = "User";
    public static String userID;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    public UserPagerAdapterFragment() {
        // Required empty public constructor
    }

    public static UserPagerAdapterFragment newInstance() {
        UserPagerAdapterFragment fragment = new UserPagerAdapterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userpageradapter, container, false);

        if (getArguments() != null){
            userID = getArguments().getString("userID");
        }

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        return view;
    }


    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 2;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return new UserPageFragment(userID);
            }
            else {
                return new UserAboutMeFragment(userID);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0){
                return UserPagerAdapterFragment.PAGE_TITLE;
            }
            else {
                return UserAboutMeFragment.PAGE_TITLE;
            }
        }
    }
}

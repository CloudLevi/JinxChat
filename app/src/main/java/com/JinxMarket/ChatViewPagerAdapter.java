package com.JinxMarket;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
public class ChatViewPagerAdapter extends Fragment {

    public static final String PAGE_TITLE = "User";
    public static String userID;

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    public ChatViewPagerAdapter() {
        // Required empty public constructor
    }

    public static ChatViewPagerAdapter newInstance() {
        ChatViewPagerAdapter fragment = new ChatViewPagerAdapter();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_view_pager_adapter, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewpagerChats);
        viewPagerAdapter = new ViewPagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        return view;
    }

    public ViewPagerAdapter getViewPagerAdapter(){
        return viewPagerAdapter;
    }


    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private static final int NUM_ITEMS = 2;

        private ChatListFragment chatListFragment;

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
                chatListFragment = new ChatListFragment();
                return chatListFragment;
            }
            else {
                UserListFragment userListFragment = new UserListFragment();
                return userListFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position == 0){
                return ChatListFragment.PAGE_TITLE;
            }
            else {
                return UserListFragment.PAGE_TITLE;
            }
        }

        public void deleteChatListItem(){
            if(chatListFragment != null){chatListFragment.deleteChat();}
            else{
                Log.d("Exception", "ChatListFragment is NULL");
            }
        }

    }
}

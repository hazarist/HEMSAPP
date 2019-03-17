package com.example.hemsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionPageAdapter extends FragmentPagerAdapter {


    public SectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ChatFragment chatFragment = new ChatFragment();
        FrendsFragment friendsFragment = new FrendsFragment();
        RequestFragment requestFragment = new RequestFragment();

        switch (position){
            case 0:
                return chatFragment;
            case 1:
                return friendsFragment;
            case 2:
                return  requestFragment;
            default:
                return chatFragment;
        }
    }

    @Override
    public int getCount() { return 3; }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "CHATS";
            case 1:
                return "FRIENDS";
            case 2:
                return "REQUESTS";
            default:
                return "CHATS";
        }
    }
}

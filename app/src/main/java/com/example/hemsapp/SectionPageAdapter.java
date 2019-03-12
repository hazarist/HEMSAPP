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

        switch (position){
            case 0:
                FrendsFragment friendsFragment = new FrendsFragment();
                return friendsFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;
            default:
                    return null;
        }
    }

    @Override
    public int getCount() { return 3; }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "FRIENDS";
            case 1:
                return "CHATS";
            case 2:
                return "REQUESTS";
            default:
                return null;
        }
    }
}

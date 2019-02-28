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
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;
            case 1:
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 2:
                FrendsFragment frendsFragment = new FrendsFragment();
                return frendsFragment;
            default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

package com.example.hemsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionPageAdapterManager extends FragmentPagerAdapter {

    public SectionPageAdapterManager (FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        ChatFragment chatFragment = new ChatFragment();
        FriendsFragment friendsFragment = new FriendsFragment();
        TasksFragment tasksFragment = new TasksFragment();

        switch (position){
            case 0:
                return chatFragment;
            case 1:
                return friendsFragment;
            case 2:
                return  tasksFragment;
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
                return "TASKS";
            default:
                return "CHATS";
        }
    }
}

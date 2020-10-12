package com.example.android.miwok;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    //public static final int BEHAVIOR_SET_USER_VISIBLE_HINT = 0;
   // private String[] tabTitles = new String[] { "NUMBERS", "COLORS", "FAMILY", "PHRASES" };
    private Context mContext;

    public SimpleFragmentPagerAdapter(@NonNull FragmentManager fm,Context context) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NumbersFragment();

            case 1:
                return new FamilyFragment();

            case 2:
                return new ColorsFragment();

            default :
                return new PhrasesFragment();
        }
    }

    @Override
    public int getCount(){
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
       /* // Generate title based on item position
        return tabTitles[position];*/
       switch(position){
           case 0: return mContext.getString(R.string.category_numbers);

           case 1: return mContext.getString(R.string.category_family);

           case 2: return mContext.getString(R.string.category_colors);

           default: return mContext.getString(R.string.category_phrases);
       }
    }
}

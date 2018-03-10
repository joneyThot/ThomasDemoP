package com.rogi.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.rogi.Activity.PlanActivity;
import com.rogi.Fragment.PlanFragment;
import com.rogi.Model.PlanModel;
import com.rogi.R;
import com.rogi.View.PlanLinearLayout;

import java.util.ArrayList;

/**
 * Created by "Mayuri" on 10/8/17.
 */

public class PlanViewPagerAdapter extends FragmentPagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private PlanLinearLayout cur = null;
    private PlanLinearLayout next = null;
    private PlanActivity context;
    private FragmentManager fm;
    private float scale;
    ArrayList<PlanModel> planModel;

    public PlanViewPagerAdapter(PlanActivity context, FragmentManager fm, ArrayList<PlanModel> planModel) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.planModel = planModel;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == PlanActivity.FIRST_PAGE)
            scale = BIG_SCALE;
        else
            scale = SMALL_SCALE;

        position = position % planModel.size();
        return PlanFragment.newInstance(context, position, scale, planModel);
    }

    @Override
    public int getCount() {
        return planModel.size() * PlanActivity.LOOPS;
    }

    @Override
    public void transformPage(View page, float position) {
        PlanLinearLayout myLinearLayout = (PlanLinearLayout) page.findViewById(R.id.root);
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;
        myLinearLayout.setScaleBoth(scale);
    }
}

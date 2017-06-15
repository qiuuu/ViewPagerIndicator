package com.pangge.learnpageindicatorview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PageIndicatorView pageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateIndicator();
    }

    @SuppressWarnings("ConstantConditions")
    private void initViews() {

        final HomeAdapter adapter = new HomeAdapter();
        adapter.setData(createPageList());

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(adapter);

        pageIndicatorView = (PageIndicatorView) findViewById(R.id.pageIndicatorView);
        pageIndicatorView.setViewPager(pager);
    }

    @NonNull
    private List<View> createPageList() {
        List<View> pageList = new ArrayList<>();
        pageList.add(createPageView(R.color.google_red));
        pageList.add(createPageView(R.color.google_blue));
        pageList.add(createPageView(R.color.google_yellow));
        pageList.add(createPageView(R.color.google_green));
        pageList.add(createPageView(R.color.google_purple));

        return pageList;
    }
    @NonNull
    private View createPageView(int color) {
        View view = new View(this);
        view.setBackgroundColor(getResources().getColor(color));

        return view;
    }

    private void updateIndicator() {
        /*if (customization == null) {
            return;
        }*/

        //pageIndicatorView.setAnimationType(NONE);
        //pageIndicatorView.setOrientation(customization.getOrientation());
        //pageIndicatorView.setRtlMode(customization.getRtlMode());
        //pageIndicatorView.setInteractiveAnimation(customization.isInteractiveAnimation());
        //pageIndicatorView.setAutoVisibility(customization.isAutoVisibility());
    }
}

package com.pangge.learnpageindicatorview;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;

import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.pangge.learnpageindicatorview.draw.data.Indicator;


import com.pangge.learnpageindicatorview.utils.CoordinatesUtils;

/**
 * Created by iuuu on 17/6/6.
 */

public class PageIndicatorView extends View implements ViewPager.OnPageChangeListener, IndicatorManager.Listener{

    private IndicatorManager manager;

    private ViewPager viewPager;
    private DataSetObserver setObserver;


    //private Indicator indicator;
   /* private Paint paint;

    private BasicDrawer basicDrawer;
    private ColorDrawer colorDrawer;


    private int position;
    private int coordinateX;
    private int coordinateY;*/

    public PageIndicatorView(Context context) {
        super(context);
        //init(null);
    }


    public PageIndicatorView(Context context, AttributeSet attrs){
        super(context,attrs);

        init(context,attrs);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        findViewPager();
    }

   /* private void initPaints(){


        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        basicDrawer = new BasicDrawer(paint, manager.indicator());
        colorDrawer = new ColorDrawer(paint, manager.indicator());




    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //indicator = new Indicator();


        Pair<Integer, Integer> pair = manager.drawer().measureViewSize(widthMeasureSpec,heightMeasureSpec);


        setMeasuredDimension(pair.first,pair.second);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        manager.drawer().draw(canvas);


    }

    @Override
    public void onIndicatorUpdated() {
        invalidate();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        selectInteractiveIndicator(position, positionOffset);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        //selectIndicator(position);
        setSelection(position);

    }



    private void selectInteractiveIndicator(int position, float positionOffset) {
        Indicator indicator = manager.indicator();
        //AnimationType animationType = indicator.getAnimationType();
        boolean interactiveAnimation = indicator.isInteractiveAnimation();
     /*   boolean canSelectIndicator = isViewMeasured() && interactiveAnimation && animationType != AnimationType.NONE;

        if (!canSelectIndicator) {
            return;
        }*/

        Pair<Integer, Float> progressPair = CoordinatesUtils.getProgress(indicator, position, positionOffset, isRtl());
        int selectingPosition = progressPair.first;
        float selectingProgress = progressPair.second;
        setProgress(selectingPosition, selectingProgress);
    }

    private boolean isRtl() {
        switch (manager.indicator().getRtlMode()) {
            case On:
                return true;

            case Off:
                return false;

            case Auto:
                return TextUtilsCompat.getLayoutDirectionFromLocale(getContext().getResources().getConfiguration().locale) == ViewCompat.LAYOUT_DIRECTION_RTL;
        }

        return false;
    }



    /**
     * Set progress value in range [0 - 1] to specify state of animation while selecting new circle indicator.
     * (Won't affect on anything unless {@link #setInteractiveAnimation(boolean isInteractive)} is true).
     *
     * @param selectingPosition selecting position with specific progress value.
     * @param progress          float value of progress.
     */
    public void setProgress(int selectingPosition, float progress) {
        Indicator indicator = manager.indicator();
        if (!indicator.isInteractiveAnimation()) {
            return;
        }

        int count = indicator.getCount();
        if (count <= 0 || selectingPosition < 0) {
            selectingPosition = 0;

        } else if (selectingPosition > count - 1) {
            selectingPosition = count - 1;
        }

        if (progress < 0) {
            progress = 0;

        } else if (progress > 1) {
            progress = 1;
        }

        if (progress == 1) {
            indicator.setLastSelectedPosition(indicator.getSelectedPosition());
            indicator.setSelectedPosition(selectingPosition);
        }

        indicator.setSelectingPosition(selectingPosition);
        manager.animate().interactive(progress);
    }


    /**
     * Interactive animation will animate indicator smoothly
     * from position to position based on user's current swipe progress.
     * (Won't affect on anything unless {@link #setViewPager(ViewPager)} is specified).
     *
     * @param isInteractive value of animation to be interactive or not.
     */
    public void setInteractiveAnimation(boolean isInteractive) {
        manager.indicator().setInteractiveAnimation(isInteractive);
    }



    private int getViewPagerCount() {
        if (viewPager != null && viewPager.getAdapter() != null) {
            return viewPager.getAdapter().getCount();
        } else {
            return manager.indicator().getCount();
        }
    }

    /**
     *
     *
     *
    private void selectIndicator(int position) {

        Indicator indicator = manager.indicator();
        int count = indicator.getCount();

       // AnimationType animationType = indicator.getAnimationType();
        boolean interactiveAnimation = indicator.isInteractiveAnimation();
        //boolean canSelectIndicator = isViewMeasured() && (!interactiveAnimation || animationType == AnimationType.NONE);

       /* if (canSelectIndicator) {
            if (isRtl()) {
                position = (count - 1) - position;
            }

            setSelection(position);
        }*
       ---setSelection(position);
    }   */

    /**
     * Dynamic count will automatically update number of circle indicators
     * if {@link ViewPager} page count updates on run-time. If new count will be bigger than current count,
     * selected circle will stay as it is, otherwise it will be set to last one.
     * Note: works if {@link ViewPager} set and already have it's adapter. See {@link #setViewPager(ViewPager)}.
     *
     * @param dynamicCount boolean value to add/remove indicators dynamically.
     */
    public void setDynamicCount(boolean dynamicCount) {
        manager.indicator().setDynamicCount(dynamicCount);

        if (dynamicCount) {
            registerSetObserver();
        } else {
            unRegisterSetObserver();
        }
    }

    /**
     * Set static number of circle indicators to be displayed.
     *
     * @param count total count of indicators.
     */
    public void setCount(int count) {
        if (count >= 0 && manager.indicator().getCount() != count) {
            manager.indicator().setCount(count);
            updateVisibility();

            requestLayout();
        }
    }

    private void updateVisibility() {
        if (manager.indicator().isAutoVisibility()) {
            return;
        }

        int count = manager.indicator().getCount();
        int visibility = getVisibility();

        if (visibility != VISIBLE && count > Indicator.MIN_COUNT) {
            setVisibility(VISIBLE);

        } else if (visibility != INVISIBLE && count <= Indicator.MIN_COUNT) {
            setVisibility(View.INVISIBLE);
        }
    }

    private void registerSetObserver() {
        if (setObserver != null || viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        setObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                updateCount();
            }
        };

        try {
            viewPager.getAdapter().registerDataSetObserver(setObserver);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void updateCount() {
        if (viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        int newCount = viewPager.getAdapter().getCount();
        int currItem = viewPager.getCurrentItem();

        manager.indicator().setSelectedPosition(currItem);
        manager.indicator().setSelectingPosition(currItem);
        manager.indicator().setLastSelectedPosition(currItem);
       // manager.animate().end();

        setCount(newCount);
    }

    private void unRegisterSetObserver() {
        if (setObserver == null || viewPager == null || viewPager.getAdapter() == null) {
            return;
        }

        try {
            viewPager.getAdapter().unregisterDataSetObserver(setObserver);
            setObserver = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        manager = new IndicatorManager(this);
        manager.drawer().initAttributes(context,attrs);


        Indicator indicator = manager.indicator();
        indicator.setPaddingLeft(getPaddingLeft());
        indicator.setPaddingTop(getPaddingTop());
        indicator.setPaddingRight(getPaddingRight());
        indicator.setPaddingBottom(getPaddingBottom());
        /**
         * AttributeController
         *
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView, 0, 0);
        initCountAttribute(typedArray);
        initColorAttribute(typedArray);
        //initAnimationAttribute(typedArray);
        initSizeAttribute(typedArray);
        typedArray.recycle();  */
    }


    /**
     *         木有用到
      * @param typedArray
     *
    private void initCountAttribute(@NonNull TypedArray typedArray) {
        int viewPagerId = typedArray.getResourceId(R.styleable.PageIndicatorView_piv_viewPager, View.NO_ID);
        boolean autoVisibility = typedArray.getBoolean(R.styleable.PageIndicatorView_piv_autoVisibility, true);
        boolean dynamicCount = typedArray.getBoolean(R.styleable.PageIndicatorView_piv_dynamicCount, false);
        int count = typedArray.getInt(R.styleable.PageIndicatorView_piv_count, Indicator.COUNT_NONE);

        if (count == Indicator.COUNT_NONE) {
            count = Indicator.DEFAULT_COUNT;
        }

        int position = typedArray.getInt(R.styleable.PageIndicatorView_piv_select, 0);
        if (position < 0) {
            position = 0;
        } else if (count > 0 && position > count - 1) {
            position = count - 1;
        }

        manager.indicator().setViewPagerId(viewPagerId);
        manager.indicator().setAutoVisibility(autoVisibility);
        manager.indicator().setDynamicCount(dynamicCount);
        manager.indicator().setCount(count);

        manager.indicator().setSelectedPosition(position);
        manager.indicator().setSelectingPosition(position);
        manager.indicator().setLastSelectedPosition(position);
    }*/


    /**
     * 没有调用－－m
     * private void initColorAttribute(@NonNull TypedArray typedArray) {
        int unselectedColor = typedArray.getColor(R.styleable.PageIndicatorView_piv_unselectedColor, Color.parseColor(ColorAnimation.DEFAULT_UNSELECTED_COLOR));
        int selectedColor = typedArray.getColor(R.styleable.PageIndicatorView_piv_selectedColor, Color.parseColor(ColorAnimation.DEFAULT_SELECTED_COLOR));

        manager.indicator().setUnselectedColor(unselectedColor);
        manager.indicator().setSelectedColor(selectedColor);
    }

    private void initSizeAttribute(@NonNull TypedArray typedArray) {
        //int orientationIndex = typedArray.getInt(R.styleable.PageIndicatorView_piv_orientation, Orientation.HORIZONTAL.ordinal());

        int radius = (int) typedArray.getDimension(R.styleable.PageIndicatorView_piv_radius, DensityUtils.dpToPx(Indicator.DEFAULT_RADIUS_DP));
        if (radius < 0) {
            radius = 0;
        }

        int padding = (int) typedArray.getDimension(R.styleable.PageIndicatorView_piv_padding, DensityUtils.dpToPx(Indicator.DEFAULT_PADDING_DP));
        if (padding < 0) {
            padding = 0;
        }



        manager.indicator().setRadius(radius);
        manager.indicator().setPadding(padding);

    }    */

    /**
     * Set specific circle indicator position to be selected. If position < or > total count,
     * accordingly first or last circle indicator will be selected.
     * (Won't affect on anything unless {@link #setInteractiveAnimation(boolean isInteractive)} is false).
     *
     * @param position position of indicator to select.
     */
    public void setSelection(int position) {
        Indicator indicator = manager.indicator();
       /* if (indicator.isInteractiveAnimation() && indicator.getAnimationType() != AnimationType.NONE) {
            return;
        }*/

        int selectedPosition = indicator.getSelectedPosition();
        int count = indicator.getCount();
        int lastPosition = count - 1;

        if (position < 0) {
            position = 0;
        } else if (position > lastPosition) {
            position = lastPosition;
        }

        if (selectedPosition == position) {
            return;
        }

        indicator.setLastSelectedPosition(indicator.getSelectedPosition());
        indicator.setSelectedPosition(position);
        manager.animate().basic();
    }

    private void findViewPager() {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            int viewPagerId = manager.indicator().getViewPagerId();

            View view = activity.findViewById(viewPagerId);
            if (view != null && view instanceof ViewPager) {
                setViewPager((ViewPager) view);
            }
        }
    }

    /**
     * Set {@link ViewPager} to add {@link ViewPager.OnPageChangeListener} and automatically
     * handle selecting new indicators (and interactive animation effect if it is enabled).
     *
     * @param pager instance of {@link ViewPager} to work with
     */

    public void setViewPager(@Nullable ViewPager pager) {
        releaseViewPager();
        if (pager == null) {
            return;
        }

        viewPager = pager;
        viewPager.addOnPageChangeListener(this);
        manager.indicator().setViewPagerId(viewPager.getId());
        Log.i("viewPager id",""+viewPager.getId());


        setDynamicCount(manager.indicator().isDynamicCount());
        int count = getViewPagerCount();
        Log.i("viewPager count",""+count);

        /*if (isRtl()) {
            int selectedPosition = (count - 1) - viewPager.getCurrentItem();
            manager.indicator().setSelectedPosition(selectedPosition);
        }*/

        setCount(count);
    }

    public void releaseViewPager() {
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(this);
            viewPager = null;
        }
    }
}

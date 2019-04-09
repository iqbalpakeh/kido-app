/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.progremastudio.kido.R;
import com.progremastudio.kido.util.ActiveContext;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public abstract class HistoryFragment extends Fragment implements TimeFilter {

    public static final int TIME_FILTER_POSITION_TODAY = 0;
    public static final int TIME_FILTER_POSITION_THIS_WEEK = 1;
    public static final int TIME_FILTER_POSITION_THIS_MONTH = 2;
    public static final int TIME_FILTER_POSITION_ALL = 3;
    private static final int STATE_ONSCREEN = 0;
    private int state = STATE_ONSCREEN;
    private static final int STATE_OFFSCREEN = 1;
    private static final int STATE_RETURNING = 2;
    private ObserveableListView listview;
    private LinearLayout placeholder;
    private TranslateAnimation animation;
    private View quickreturn;
    private boolean isShowBackground = false;
    private int quickReturnHeight;
    private int cacheVerticalRange;
    private int scrollY;
    private int rawY;
    private int minRawY = 0;

    public static String[] createTimeFilter(Context context, int position) {
        /**
         * as stated here: http://developer.android.com/reference/java/util/Calendar.html
         * 24:00:00 "belongs" to the following day.
         * That is, 23:59 on Dec 31, 1969 < 24:00 on Jan 1, 1970 < 24:01:00 on Jan 1, 1970
         * form a sequence of three consecutive minutes in time.
         */
        long oneWeekDuration = TimeUnit.DAYS.toMillis(7);
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);
        String startTime;
        switch (position) {
            case TIME_FILTER_POSITION_TODAY:
                break;
            case TIME_FILTER_POSITION_THIS_MONTH:
                startCalendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case TIME_FILTER_POSITION_ALL:
                startCalendar.set(Calendar.YEAR, 1);
                break;
            case TIME_FILTER_POSITION_THIS_WEEK:
            default:
                startCalendar.setTimeInMillis(startCalendar.getTimeInMillis() - oneWeekDuration);
                break;
        }
        startTime = String.valueOf(startCalendar.getTimeInMillis());
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 0);
        String endTime = String.valueOf(endCalendar.getTimeInMillis());
        String[] timeFilter = {
                String.valueOf(ActiveContext.getActiveBaby(context).getActivityId()),
                startTime,
                endTime
        };
        return timeFilter;
    }

    public static int getPosition(Context context, String filter) {
        if (filter.equals(context.getString(R.string.str_for_today))) {
            return 0;
        } else if (filter.equals(context.getString(R.string.str_for_this_week))) {
            return 1;
        } else if (filter.equals(context.getString(R.string.str_for_this_month))) {
            return 2;
        } else {
            return 3; // for @string/str_All
        }
    }

    public void attachQuickReturnView(View root, int id) {
        this.quickreturn = root.findViewById(id);
    }

    public void attachPlaceHolderLayout(View rootPlaceHolder, int id) {
        this.placeholder = (LinearLayout) rootPlaceHolder.findViewById(id);
    }

    public void attachListView(ObserveableListView listview) {
        this.listview = listview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // add menu_global layout listener
        listview.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        quickReturnHeight = quickreturn.getHeight();
                        listview.computeScrollY();
                        cacheVerticalRange = listview.getListHeight();
                        placeholder.setMinimumHeight(quickReturnHeight);
                    }
                });

        // add scroll listener
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // nothing happened here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                scrollY = 0;
                int translationY = 0;
                String stateTrace = "";

                if (listview.scrollYIsComputed()) {
                    scrollY = listview.getComputedScrollY();
                }

                rawY = -scrollY;

                switch (state) {
                    case STATE_ONSCREEN: // state 0
                        stateTrace = "00";
                        if (rawY < -quickReturnHeight) {
                            state = STATE_OFFSCREEN;
                            minRawY = rawY;
                            stateTrace = "01";
                        }
                        if (rawY >= placeholder.getHeight()) {
                            rawY = 0;
                            stateTrace = "02";
                        }
                        translationY = rawY;
                        break;

                    case STATE_OFFSCREEN: // state 1
                        stateTrace = "10";
                        if (rawY <= minRawY) {
                            minRawY = rawY;
                            stateTrace = "11";
                        } else {
                            state = STATE_RETURNING;
                            stateTrace = "12";
                        }
                        translationY = rawY;
                        break;

                    case STATE_RETURNING: // state 2
                        translationY = (rawY - minRawY) - quickReturnHeight;
                        stateTrace = "20";
                        if (translationY > 0) {
                            translationY = 0;
                            minRawY = rawY - quickReturnHeight;
                            stateTrace = "21";
                            if (rawY < 0 && !isShowBackground) {
                                setShadowBackground(R.drawable.header_shadow);
                                isShowBackground = true;
                                stateTrace = "24";
                            } else if (rawY >= 0 && isShowBackground) {
                                setShadowBackground(0);
                                isShowBackground = false;
                                stateTrace = "25";
                            }
                        }
                        if (rawY >= 0) {
                            state = STATE_ONSCREEN;
                            translationY = rawY;
                            stateTrace = "22";
                        }
                        if (translationY < -quickReturnHeight) {
                            state = STATE_OFFSCREEN;
                            minRawY = rawY;
                            stateTrace = "23";
                        }
                        if (stateTrace.equals("20") && !isShowBackground &&
                                (-translationY < quickReturnHeight) && (translationY != 0)) {
                            setShadowBackground(R.drawable.header_shadow);
                            isShowBackground = true;
                            stateTrace = "26";
                        }
                        break;
                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
                    animation = new TranslateAnimation(0, 0, translationY, translationY);
                    animation.setFillAfter(true);
                    animation.setDuration(0);
                    quickreturn.startAnimation(animation);
                } else {
                    quickreturn.setTranslationY(translationY);
                }
            }
        });
    }

    private void setShadowBackground(int iBackgroundId) {
        int paddingLeft = quickreturn.getPaddingLeft();
        int paddingTop = quickreturn.getPaddingTop();
        int paddingRight = quickreturn.getPaddingRight();
        int paddingBottom = quickreturn.getPaddingBottom();
        quickreturn.setBackgroundResource(iBackgroundId);
        quickreturn.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }
}

/*
 * Copyright (c) 2015, Progrema Studio. All rights reserved.
 */

package com.progremastudio.kido.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ObserveableListView extends ListView {

    private Callback callback;
    private boolean bIsScrollUp = true;
    private boolean bScrollIsComputed = false;
    private float fLastTouchX;
    private float fLastTouchY;
    private float fPositionX;
    private float fPositionY;
    private int INVALID_POINTER_ID = MotionEvent.INVALID_POINTER_ID;
    private int iActivePointerId = INVALID_POINTER_ID;
    private int SCROLLING_BUFFER = 3;
    private int iTotalHeight;
    private int iItemCount;
    private int iItemOffsetY[];
    private int iCacheHeight;

    public ObserveableListView(Context context) {
        super(context);
    }

    public ObserveableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObserveableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCallbacks(Callback listener) {
        callback = listener;
    }

    public int getListHeight() {
        return iTotalHeight;
    }

    public void computeScrollY() {
        iTotalHeight = 0;
        iItemCount = getAdapter().getCount();
        iItemOffsetY = null; // Let GC works!!
        iItemOffsetY = new int[iItemCount];

        for (int i = 0; i < iItemCount; ++i) {
            /**
             * Performance improvement:
             * First iteration is Header, second and the rest are Entry.
             * We don't measure all the menu_entry's height since it's all the same to
             * improve the performance.
             */
            if (i < 2) {
                View view = getAdapter().getView(i, null, this);
                view.measure(
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                iItemOffsetY[i] = iTotalHeight;
                iCacheHeight = view.getMeasuredHeight() + this.getDividerHeight();
                iTotalHeight += iCacheHeight;
            } else {
                iItemOffsetY[i] = iTotalHeight;
                iTotalHeight += iCacheHeight;
            }

        }
        bScrollIsComputed = true;
    }

    public boolean scrollYIsComputed() {
        return bScrollIsComputed;
    }

    public int getComputedScrollY() {
        int iPosition;
        int iScrollY = 0;
        int iItemY;
        String sExceptionTrace = "no";
        View view = null;
        iPosition = getFirstVisiblePosition();
        view = getChildAt(0);
        iItemY = view.getTop();
        try {
            iScrollY = iItemOffsetY[iPosition] - iItemY;
        } catch (Exception e) {
            // avoid array out of bond exception
            sExceptionTrace = "yes";
        }
        return iScrollY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (callback != null) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    final int iPointerIndex = MotionEventCompat.getActionIndex(ev);
                    final float fCoordinateX = MotionEventCompat.getX(ev, iPointerIndex);
                    final float fCoordinateY = MotionEventCompat.getY(ev, iPointerIndex);

                    // Remember where we started (for dragging)
                    fLastTouchX = fCoordinateX;
                    fLastTouchY = fCoordinateY;
                    // Save the activityId of this pointer (for dragging)
                    iActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    final int iPointerIndex = MotionEventCompat.findPointerIndex(ev, iActivePointerId);
                    if (iPointerIndex == -1) break;
                    final float fCoordinateX = MotionEventCompat.getX(ev, iPointerIndex);
                    final float fCoordinateY = MotionEventCompat.getY(ev, iPointerIndex);

                    // Calculate the distance moved
                    final float fDeltaX = fCoordinateX - fLastTouchX;
                    final float fDeltaY = fCoordinateY - fLastTouchY;

                    fPositionX += fDeltaX;
                    fPositionY += fDeltaY;

                    invalidate();

                    // Remember this touch position for the next move event
                    fLastTouchX = fCoordinateX;
                    fLastTouchY = fCoordinateY;

                    if (fDeltaY > SCROLLING_BUFFER) {
                        if (!bIsScrollUp) {
                            callback.onScrollDown();
                            bIsScrollUp = true;
                        }
                    } else if (fDeltaY < -SCROLLING_BUFFER) {
                        if (bIsScrollUp) {
                            callback.onScrollUp();
                            bIsScrollUp = false;
                        }
                    }
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    iActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    iActivePointerId = INVALID_POINTER_ID;
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    final int iPointerIndex = MotionEventCompat.getActionIndex(ev);
                    final int iPointerId = MotionEventCompat.getPointerId(ev, iPointerIndex);

                    if (iPointerId == iActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = iPointerIndex == 0 ? 1 : 0;
                        fLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                        fLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                        iActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                    }
                    break;
                }
            }


        }
        return super.onTouchEvent(ev);
    }

    public static interface Callback {
        public void onScrollUp();

        public void onScrollDown();
    }
}

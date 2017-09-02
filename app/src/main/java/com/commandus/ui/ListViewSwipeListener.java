package com.commandus.ui;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * http://stackoverflow.com/questions/17520750/list-view-item-swipe-left-and-swipe-right
 */
public class ListViewSwipeListener implements View.OnTouchListener {
    private final SwipeEvent mSwipeEvent;
    private final ListView mListView;

    public interface SwipeEvent {
        void onSwipe(Action action, int position);
    }

    public static final String TAG = ListViewSwipeListener.class.getSimpleName();

    public ListViewSwipeListener(ListView listview, SwipeEvent swipeEvent) {
        mSwipeEvent = swipeEvent;
        mListView = listview;
    }

    public static enum Action {
		LR, // Left to Right
		RL, // Right to Left
		None // when no action was detected
	}

	private static int MIN_DISTANCE = 30;
	private float downX, downY, upX, upY;
	private Action mSwipeDetected = Action.None;

	public boolean swipeDetected() {
		return mSwipeDetected != Action.None;
	}

	public Action getAction() {
		return mSwipeDetected;
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				downX = event.getX();
				downY = event.getY();
				mSwipeDetected = Action.None;
				return false; // allow other events like Click to be processed
			}
			case MotionEvent.ACTION_MOVE: {
				upX = event.getX();
				upY = event.getY();
				int deltaX = (int) (downX - upX);
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					// left or right
                    int position = mListView.pointToPosition((int) upX, (int) upY);
                    if (deltaX < 0) {
						mSwipeDetected = Action.LR;
                        mSwipeEvent.onSwipe(mSwipeDetected, position);
						return true;
					}
					if (deltaX > 0) {
						mSwipeDetected = Action.RL;
                        mSwipeEvent.onSwipe(mSwipeDetected, position);
						return true;
					}
				}
				return true;
			}
		}
		return false;
	}
}
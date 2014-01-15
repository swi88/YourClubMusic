package de.uniol.yourclubmusic;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

// Modified version of http://stackoverflow.com/a/9340202
// (Removed TB/BT detection)
public class SwipeDetector implements View.OnTouchListener {

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int HORIZONTAL_MIN_DISTANCE = 100;
    private float downX, upX;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();

                float deltaX = downX - upX;

                // horizontal swipe detection
                if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0) {
                        // Log.i(logTag, "Swipe Left to Right");
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        // Log.i(logTag, "Swipe Right to Left");
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
}
package me.zhanghai.patternlock.internal;

import android.app.Activity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.R;

/**
 *
 */
public class BasePatternActivityDelegate {

    private static final int CLEAR_PATTERN_DELAY_MILLI = 2000;

    public TextView messageText;
    public PatternView patternView;
    public LinearLayout buttonContainer;
    public Button leftButton;
    public Button rightButton;

    public void onCreate(Activity activity) {
        activity.setContentView(R.layout.pl_base_pattern_activity);
        messageText = (TextView) activity.findViewById(R.id.pl_message_text);
        patternView = (PatternView) activity.findViewById(R.id.pl_pattern);
        buttonContainer = (LinearLayout) activity.findViewById(R.id.pl_button_container);
        leftButton = (Button) activity.findViewById(R.id.pl_left_button);
        rightButton = (Button) activity.findViewById(R.id.pl_right_button);
    }


    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            patternView.clearPattern();
        }
    };

    public void removeClearPatternRunnable() {
        patternView.removeCallbacks(clearPatternRunnable);
    }

    public void postClearPatternRunnable() {
        removeClearPatternRunnable();
        patternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
    }
}

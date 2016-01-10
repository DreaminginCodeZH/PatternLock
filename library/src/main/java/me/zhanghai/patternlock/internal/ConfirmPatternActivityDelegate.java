package me.zhanghai.patternlock.internal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.List;

import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.R;
import me.zhanghai.patternlock.ViewAccessibilityCompat;

/**
 *
 */
public class ConfirmPatternActivityDelegate extends BasePatternActivityDelegate implements PatternView.OnPatternListener {

    private static final String KEY_NUM_FAILED_ATTEMPTS = "num_failed_attempts";
    public static final int RESULT_FORGOT_PASSWORD = Activity.RESULT_FIRST_USER;

    protected int numFailedAttempts;
    private Activity activity;
    private Receiver receiver;

    public void onCreate(Activity activity, final Receiver receiver, Bundle savedInstanceState) {
        super.onCreate(activity);

        this.activity = activity;
        this.receiver = receiver;

        messageText.setText(R.string.pl_draw_pattern_to_unlock);
        patternView.setInStealthMode(isStealthModeEnabled());
        patternView.setOnPatternListener(this);
        leftButton.setText(R.string.pl_cancel);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver.onCancel();
            }
        });
        rightButton.setText(R.string.pl_forgot_pattern);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver.onForgotPassword();
            }
        });

        ViewAccessibilityCompat.announceForAccessibility(messageText, messageText.getText());

        if (savedInstanceState == null) {
            numFailedAttempts = 0;
        } else {
            numFailedAttempts = savedInstanceState.getInt(KEY_NUM_FAILED_ATTEMPTS);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_NUM_FAILED_ATTEMPTS, numFailedAttempts);
    }

    @Override
    public void onPatternStart() {

        removeClearPatternRunnable();

        // Set display mode to correct to ensure that pattern can be in stealth mode.
        patternView.setDisplayMode(PatternView.DisplayMode.Correct);
    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> pattern) {}

    @Override
    public void onPatternDetected(List<PatternView.Cell> pattern) {
        if (isPatternCorrect(pattern)) {
            receiver.onConfirmed();
        } else {
            messageText.setText(R.string.pl_wrong_pattern);
            patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
            postClearPatternRunnable();
            ViewAccessibilityCompat.announceForAccessibility(messageText, messageText.getText());
            onWrongPattern();
        }
    }

    @Override
    public void onPatternCleared() {
        removeClearPatternRunnable();
    }

    private boolean isStealthModeEnabled() {
        return receiver.isStealthModeEnabled();
    }

    private boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        return receiver.isPatternCorrect(pattern);
    }

    public void onWrongPattern() {
        ++numFailedAttempts;
    }

    public boolean defaultIsStealthModeEnabled() {
        return false;
    }

    public boolean defaultIsPatternCorrect(List<PatternView.Cell> pattern) {
        return true;
    }

    public void defaultOnConfirmed() {
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    public void defaultOnCancel() {
        activity.setResult(Activity.RESULT_CANCELED);
        activity.finish();
    }

    public void defaultOnForgotPassword() {
        activity.setResult(RESULT_FORGOT_PASSWORD);
        activity.finish();
    }

    public interface Receiver {
        boolean isStealthModeEnabled();
        boolean isPatternCorrect(List<PatternView.Cell> pattern);

        void onForgotPassword();
        void onCancel();
        void onConfirmed();
    }
}

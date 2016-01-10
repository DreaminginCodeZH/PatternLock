package me.zhanghai.patternlock.internal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.R;
import me.zhanghai.patternlock.ViewAccessibilityCompat;

/**
 *
 */
public class SetPatterActivityDelegate extends BasePatternActivityDelegate implements PatternView.OnPatternListener {

    private Activity activity;
    private Receiver receiver;

    private enum LeftButtonState {

        Cancel(R.string.pl_cancel, true),
        CancelDisabled(R.string.pl_cancel, false),
        Redraw(R.string.pl_redraw, true),
        RedrawDisabled(R.string.pl_redraw, false);

        public final int textId;
        public final boolean enabled;

        LeftButtonState(int textId, boolean enabled) {
            this.textId = textId;
            this.enabled = enabled;
        }
    }

    private enum RightButtonState {

        Continue(R.string.pl_continue, true),
        ContinueDisabled(R.string.pl_continue, false),
        Confirm(R.string.pl_confirm, true),
        ConfirmDisabled(R.string.pl_confirm, false);

        public final int textId;
        public final boolean enabled;

        RightButtonState(int textId, boolean enabled) {
            this.textId = textId;
            this.enabled = enabled;
        }
    }

    private enum Stage {

        Draw(R.string.pl_draw_pattern, LeftButtonState.Cancel, RightButtonState.ContinueDisabled,
                true),
        DrawTooShort(R.string.pl_pattern_too_short, LeftButtonState.Redraw,
                RightButtonState.ContinueDisabled, true),
        DrawValid(R.string.pl_pattern_recorded, LeftButtonState.Redraw, RightButtonState.Continue,
                false),
        Confirm(R.string.pl_confirm_pattern, LeftButtonState.Cancel,
                RightButtonState.ConfirmDisabled, true),
        ConfirmWrong(R.string.pl_wrong_pattern, LeftButtonState.Cancel,
                RightButtonState.ConfirmDisabled, true),
        ConfirmCorrect(R.string.pl_pattern_confirmed, LeftButtonState.Cancel,
                RightButtonState.Confirm, false);

        public final int messageId;
        public final LeftButtonState leftButtonState;
        public final RightButtonState rightButtonState;
        public final boolean patternEnabled;

        Stage(int messageId, LeftButtonState leftButtonState,
              RightButtonState rightButtonState, boolean patternEnabled) {
            this.messageId = messageId;
            this.leftButtonState = leftButtonState;
            this.rightButtonState = rightButtonState;
            this.patternEnabled = patternEnabled;
        }
    }

    private static final String KEY_STAGE = "stage";
    private static final String KEY_PATTERN = "pattern";

    private int minPatternSize;
    private List<PatternView.Cell> pattern;
    private Stage stage;

    public void onCreate(Activity activity, Receiver receiver, Bundle savedInstanceState) {
        super.onCreate(activity);

        this.activity = activity;
        this.receiver = receiver;

        minPatternSize = getMinPatternSize();

        patternView.setOnPatternListener(this);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftButtonClicked();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightButtonClicked();
            }
        });

        if (savedInstanceState == null) {
            updateStage(Stage.Draw);
        } else {
            String patternString = savedInstanceState.getString(KEY_PATTERN);
            if (patternString != null) {
                pattern = PatternUtils.stringToPattern(patternString);
            }
            updateStage(Stage.values()[savedInstanceState.getInt(KEY_STAGE)]);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STAGE, stage.ordinal());
        if (pattern != null) {
            outState.putString(KEY_PATTERN, PatternUtils.patternToString(pattern));
        }
    }

    @Override
    public void onPatternStart() {

        removeClearPatternRunnable();

        messageText.setText(R.string.pl_recording_pattern);
        patternView.setDisplayMode(PatternView.DisplayMode.Correct);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> pattern) {}

    @Override
    public void onPatternDetected(List<PatternView.Cell> newPattern) {
        switch (stage) {
            case Draw:
            case DrawTooShort:
                if (newPattern.size() < minPatternSize) {
                    updateStage(Stage.DrawTooShort);
                } else {
                    pattern = new ArrayList<>(newPattern);
                    updateStage(Stage.DrawValid);
                }
                break;
            case Confirm:
            case ConfirmWrong:
                if (newPattern.equals(pattern)) {
                    updateStage(Stage.ConfirmCorrect);
                } else {
                    updateStage(Stage.ConfirmWrong);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected stage " + stage + " when "
                        + "entering the pattern.");
        }
    }

    @Override
    public void onPatternCleared() {
        removeClearPatternRunnable();
    }

    private void onLeftButtonClicked() {
        if (stage.leftButtonState == LeftButtonState.Redraw) {
            pattern = null;
            updateStage(Stage.Draw);
        } else if (stage.leftButtonState == LeftButtonState.Cancel) {
            activity.setResult(Activity.RESULT_CANCELED);
            activity.finish();
        } else {
            throw new IllegalStateException("left footer button pressed, but stage of " + stage
                    + " doesn't make sense");
        }
    }

    private void onRightButtonClicked() {
        if (stage.rightButtonState == RightButtonState.Continue) {
            if (stage != Stage.DrawValid) {
                throw new IllegalStateException("expected ui stage " + Stage.DrawValid
                        + " when button is " + RightButtonState.Continue);
            }
            updateStage(Stage.Confirm);
        } else if (stage.rightButtonState == RightButtonState.Confirm) {
            if (stage != Stage.ConfirmCorrect) {
                throw new IllegalStateException("expected ui stage " + Stage.ConfirmCorrect
                        + " when button is " + RightButtonState.Confirm);
            }
            onSetPattern(pattern);
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }
    }

    private void updateStage(Stage newStage) {

        Stage previousStage = stage;
        stage = newStage;

        if (stage == Stage.DrawTooShort) {
            messageText.setText(activity.getString(stage.messageId, minPatternSize));
        } else {
            messageText.setText(stage.messageId);
        }

        leftButton.setText(stage.leftButtonState.textId);
        leftButton.setEnabled(stage.leftButtonState.enabled);

        rightButton.setText(stage.rightButtonState.textId);
        rightButton.setEnabled(stage.rightButtonState.enabled);

        patternView.setInputEnabled(stage.patternEnabled);

        switch (stage) {
            case Draw:
                // clearPattern() resets display mode to DisplayMode.Correct.
                patternView.clearPattern();
                break;
            case DrawTooShort:
                patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case DrawValid:
                break;
            case Confirm:
                patternView.clearPattern();
                break;
            case ConfirmWrong:
                patternView.setDisplayMode(PatternView.DisplayMode.Wrong);
                postClearPatternRunnable();
                break;
            case ConfirmCorrect:
                break;
        }

        // If the stage changed, announce the header for accessibility. This
        // is a no-op when accessibility is disabled.
        if (previousStage != stage) {
            ViewAccessibilityCompat.announceForAccessibility(messageText, messageText.getText());
        }
    }

    protected int getMinPatternSize() {
        return receiver.getMinPatternSize();
    }

    protected void onSetPattern(List<PatternView.Cell> pattern) {
        receiver.onSetPattern(pattern);
    }

    public int defaultGetMinPatternSize() {
        return 4;
    }

    public void defaultOnSetPattern(List<PatternView.Cell> pattern) {}

    public interface Receiver {
        int getMinPatternSize();
        void onSetPattern(List<PatternView.Cell> pattern);
    }
}

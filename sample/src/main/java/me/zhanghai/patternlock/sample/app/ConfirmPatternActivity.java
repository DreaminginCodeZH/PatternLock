/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.patternlock.sample.app;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.sample.util.AppUtils;
import me.zhanghai.patternlock.sample.util.PatternLockUtils;
import me.zhanghai.patternlock.sample.util.PreferenceContract;
import me.zhanghai.patternlock.sample.util.PreferenceUtils;

public class ConfirmPatternActivity extends me.zhanghai.patternlock.ConfirmPatternActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUtils.setupActionBar(this);
    }

    @Override
    public boolean isStealthModeEnabled() {
        return !PreferenceUtils.getBoolean(PreferenceContract.KEY_PATTERN_VISIBLE,
                PreferenceContract.DEFAULT_PATTERN_VISIBLE, this);
    }

    @Override
    public boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        return PatternLockUtils.isPatternCorrect(pattern, this);
    }

    @Override
    public void onForgotPassword() {

        startActivity(new Intent(this, ResetPatternActivity.class));

        // Finish with RESULT_FORGOT_PASSWORD.
        super.onForgotPassword();
    }
}

/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.patternlock.appcompat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.internal.ConfirmPatternActivityDelegate;

public class ConfirmPatternActivity extends AppCompatActivity implements ConfirmPatternActivityDelegate.Receiver {

    public static final int RESULT_FORGOT_PASSWORD = ConfirmPatternActivityDelegate.RESULT_FORGOT_PASSWORD;

    protected ConfirmPatternActivityDelegate delegate = new ConfirmPatternActivityDelegate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate.onCreate(this, this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        delegate.onSaveInstanceState(outState);
    }

    @Override
    public boolean isStealthModeEnabled() {
        return delegate.defaultIsStealthModeEnabled();
    }

    @Override
    public boolean isPatternCorrect(List<PatternView.Cell> pattern) {
        return delegate.defaultIsPatternCorrect(pattern);
    }

    @Override
    public void onForgotPassword() {
        delegate.defaultOnForgotPassword();
    }

    @Override
    public void onCancel() {
        delegate.defaultOnCancel();
    }

    @Override
    public void onConfirmed() {
        delegate.defaultOnConfirmed();
    }
}

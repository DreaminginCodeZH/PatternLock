/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.patternlock.appcompat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import me.zhanghai.patternlock.PatternView;
import me.zhanghai.patternlock.internal.SetPatterActivityDelegate;

public class SetPatternActivity extends AppCompatActivity implements SetPatterActivityDelegate.Receiver {

    protected SetPatterActivityDelegate delegate = new SetPatterActivityDelegate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        delegate.onCreate(this, this, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        delegate.onSaveInstanceState(outState);
    }

    public int getMinPatternSize() {
        return delegate.defaultGetMinPatternSize();
    }

    public void onSetPattern(List<PatternView.Cell> pattern) {
        delegate.defaultOnSetPattern(pattern);
    }
}

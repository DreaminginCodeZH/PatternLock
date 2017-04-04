/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.patternlock.sample.app;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.patternlock.PatternView;
import me.zhanghai.android.patternlock.sample.R;
import me.zhanghai.android.patternlock.sample.util.AppUtils;

public class AboutActivity extends ThemedAppCompatActivity {

    private static final List<PatternView.Cell> LOGO_PATTERN = new ArrayList<>();
    static {
        LOGO_PATTERN.add(PatternView.Cell.of(0, 1));
        LOGO_PATTERN.add(PatternView.Cell.of(1, 0));
        LOGO_PATTERN.add(PatternView.Cell.of(2, 1));
        LOGO_PATTERN.add(PatternView.Cell.of(1, 2));
        LOGO_PATTERN.add(PatternView.Cell.of(1, 1));
    }

    @BindView(R.id.pattern_view)
    PatternView mPatternView;
    @BindView(R.id.version_text)
    TextView mVersionText;
    @BindView(R.id.github_text)
    TextView mGitHubText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUtils.setActionBarDisplayUp(this);

        setContentView(R.layout.about_activity);
        ButterKnife.bind(this);

        mPatternView.setPattern(PatternView.DisplayMode.Animate, LOGO_PATTERN);
        String version = getString(R.string.about_version,
                AppUtils.getPackageInfo(this).versionName);
        mVersionText.setText(version);
        mGitHubText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AppUtils.navigateUp(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

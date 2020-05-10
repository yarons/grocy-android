package xyz.zedler.patrick.grocy.view;

/*
    This file is part of Grocy Android.

    Grocy Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Grocy Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Grocy Android.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2020 by Patrick Zedler & Dominic Zedler
*/

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import xyz.zedler.patrick.grocy.R;

public class ExpandableCard extends LinearLayout {

    private Context context;
    private TextView textViewCollapsed, textViewExpanded;
    private boolean isOrWillBeExpanded = false;
    private ValueAnimator heightAnimator;
    private int heightCollapsed, heightExpanded;
    private long animatorPlayTime = 0;

    public ExpandableCard(Context context) {
        super(context);

        this.context = context;
        init();
    }

    public ExpandableCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        init();
    }

    private void init() {
        inflate(context, R.layout.view_expandable_card, this);

        textViewCollapsed = findViewById(R.id.text_expandable_card_collapsed);
        textViewExpanded = findViewById(R.id.text_expandable_card_expanded);

        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        heightCollapsed = textViewCollapsed.getHeight() + dp(32);
                        heightExpanded = textViewExpanded.getHeight() + dp(32);

                        getLayoutParams().height = heightCollapsed;
                        requestLayout();

                        if (getViewTreeObserver().isAlive()) {
                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });

        findViewById(R.id.card_expandable_card).setOnClickListener(v -> changeState());
    }

    public void setText(String text) {
        textViewCollapsed.setText(text);
        textViewExpanded.setText(text);
    }

    public void changeState() {
        int duration = 300;
        int targetHeight = isOrWillBeExpanded ? heightCollapsed : heightExpanded;
        if(heightAnimator != null) {
            if(heightAnimator.isRunning()) heightAnimator.pause();
        }
        heightAnimator = ValueAnimator.ofInt(getLayoutParams().height, targetHeight);
        heightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorPlayTime = 0;
            }
        });
        heightAnimator.addUpdateListener(animation -> {
            getLayoutParams().height = (int) animation.getAnimatedValue();
            requestLayout();
            animatorPlayTime = animation.getCurrentPlayTime();
        });
        heightAnimator.setDuration(
                animatorPlayTime > 0 && animatorPlayTime < duration
                        ? animatorPlayTime
                        : duration
        ).setInterpolator(new FastOutSlowInInterpolator());
        heightAnimator.start();
        isOrWillBeExpanded = !isOrWillBeExpanded;
    }

    private int dp(float dp){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }
}

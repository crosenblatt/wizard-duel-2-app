package com.cs307.crosenblatt.wizardduel2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cs307.crosenblatt.fragment.OnboardingFragmentOne;
import com.cs307.crosenblatt.fragment.OnboardingFragmentThree;
import com.cs307.crosenblatt.fragment.OnboardingFragmentTwo;
import com.cs307.crosenblatt.fragment.OnboardingFragmentTwo2;
import com.vlonjatg.android.apptourlibrary.AppTour;

public class OnboardingActivity extends AppTour {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        int customSlideColor = Color.parseColor("#FF5722");
        addSlide(new OnboardingFragmentOne(), customSlideColor);
        addSlide(new OnboardingFragmentTwo(), customSlideColor);
        addSlide(new OnboardingFragmentTwo2(), customSlideColor);
        addSlide(new OnboardingFragmentThree(), customSlideColor);
        setSeparatorColor(Color.parseColor("#FF5722"));
        setSkipButtonTextColor(Color.WHITE);
        setNextButtonColorToWhite();
        setDoneButtonTextColor(Color.WHITE);
        setActiveDotColor(Color.parseColor("#2E2E2E"));
        setDoneText("LOGIN");
    }

    @Override
    public void onSkipPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
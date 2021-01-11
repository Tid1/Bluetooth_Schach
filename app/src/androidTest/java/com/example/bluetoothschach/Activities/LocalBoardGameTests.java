package com.example.bluetoothschach.Activities;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import net.bytebuddy.asm.Advice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import Model.Spiellogik.BoardImpl;
import Model.Spiellogik.Color;
import Model.Spiellogik.Figuren.Bauer;
import Model.Spiellogik.Figuren.Position;

@RunWith(AndroidJUnit4.class)
public class LocalBoardGameTests {
    private BoardImpl board;
    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(LocalBoardActivity.class);

    @Before
    public void setUp(){

    }

    @Test
    public void testMock(){

    }


}

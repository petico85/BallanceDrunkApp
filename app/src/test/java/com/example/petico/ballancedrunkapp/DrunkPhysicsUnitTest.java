package com.example.petico.ballancedrunkapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DrunkPhysicsUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        DrunkPhysics drunkPhysics = new DrunkPhysics(75,96);
        drunkPhysics.processMove(2,2);
        //assertEquals(2, drunkPhysics.moveX);
        //assertEquals(2, drunkPhysics.moveY);

    }
}
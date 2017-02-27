package com.ub.smssender;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void sendMessage(){
        SMSUtils.sendSMS(null,0,"6672118438",null,"Hi Stackoverflow! its me Maher. Sent by sim1",null,null);
    }
}
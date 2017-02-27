package com.ub.smssender.Main;

import android.app.IntentService;
import android.content.Intent;

import com.ub.smssender.services.WSUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ulises on 26/02/17.
 */

public class SMSService extends IntentService {

    private Timer timer = new Timer();

    public SMSService() {
        super("SMS Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000);
    }


    private class mainTask extends TimerTask
    {
        public void run()
        {

        }
    }
}

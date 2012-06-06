package com.mattmayers.PetRawk;

import android.app.Activity;
import orbotix.robot.base.DeviceAsyncData;
import orbotix.robot.base.DeviceMessenger.AsyncDataListener;

public class RobotSensorListener implements AsyncDataListener {
    Activity mActivity;

    public RobotSensorListener(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onDataReceived(DeviceAsyncData data) {
        // TODO Auto-generated method stub

    }

}

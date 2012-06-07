package com.mattmayers.PetRawk;

import orbotix.robot.app.CalibrationActivity;
import orbotix.robot.app.StartupActivity;
import orbotix.robot.base.DeviceMessenger;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.robot.base.RollCommand;
import orbotix.robot.base.SetDataStreamingCommand;
import orbotix.robot.util.SensorLowPassFilter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    Context context;
    
    private static final String TAG = "MainActivity";
    private final Handler handler = new Handler();
    
    private final static int CONNECT_ACTIVITY = 0;
    private Robot mRobot;
    private TextView mRobotID,
                     mDeviceMagnetX, mDeviceMagnetY, mDeviceMagnetZ,
                     mDeviceAccelX,  mDeviceAccelY,  mDeviceAccelZ,
                     mRobotMagnetX,  mRobotMagnetY,  mRobotMagnetZ,
                     mRobotAccelX,   mRobotAccelY,   mRobotAccelZ;
    private Button mSpheroConnect, mSpheroCalibrate, mSpheroForward;
    
    private SensorManager mSensorManager;
    private Sensor mMagnetSensor, mAccelSensor;
    private DeviceSensorListener mDeviceListener;
    private RobotSensorListener mRobotListener;
    
    private Handler mSensorDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            float[] sensorData = (float[])msg.obj;
            switch (msg.what){
                case DeviceSensorListener.MAGNETOMETER_DATA:
                    mDeviceMagnetX.setText(String.valueOf(sensorData[0]));
                    mDeviceMagnetY.setText(String.valueOf(sensorData[1]));
                    mDeviceMagnetZ.setText(String.valueOf(sensorData[2]));
                    break;
                case DeviceSensorListener.ACCELEROMETER_DATA:
                    mDeviceAccelX.setText(String.valueOf(sensorData[0]));
                    mDeviceAccelY.setText(String.valueOf(sensorData[1]));
                    mDeviceAccelZ.setText(String.valueOf(sensorData[2]));
                    break;

                case RobotSensorListener.ACCELEROMETER_DATA:
                    mRobotAccelX.setText(String.valueOf(sensorData[0]));
                    mRobotAccelY.setText(String.valueOf(sensorData[1]));
                    mRobotAccelZ.setText(String.valueOf(sensorData[2]));
                    break;
                case RobotSensorListener.MAGNETOMETER_DATA:
                    mRobotMagnetX.setText(String.valueOf(sensorData[0]));
                    mRobotMagnetY.setText(String.valueOf(sensorData[1]));
                    mRobotMagnetZ.setText(String.valueOf(sensorData[2]));
                    break;
            }
        }
    };
    
    
    private SensorLowPassFilter mDeviceMagnetFilter;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = getApplicationContext();
        
        mRobotID       = (TextView) findViewById(R.id.text_RobotID);
        mDeviceMagnetX = (TextView) findViewById(R.id.text_DeviceMagnetX);
        mDeviceMagnetY = (TextView) findViewById(R.id.text_DeviceMagnetY);
        mDeviceMagnetZ = (TextView) findViewById(R.id.text_DeviceMagnetZ);
        mRobotMagnetX  = (TextView) findViewById(R.id.text_RobotMagnetX);
        mRobotMagnetY  = (TextView) findViewById(R.id.text_RobotMagnetY);
        mRobotMagnetZ  = (TextView) findViewById(R.id.text_RobotMagnetZ);
        mDeviceAccelX  = (TextView) findViewById(R.id.text_DeviceAccelX);
        mDeviceAccelY  = (TextView) findViewById(R.id.text_DeviceAccelY);
        mDeviceAccelZ  = (TextView) findViewById(R.id.text_DeviceAccelZ);
        mRobotAccelX   = (TextView) findViewById(R.id.text_RobotAccelX);
        mRobotAccelY   = (TextView) findViewById(R.id.text_RobotAccelY);
        mRobotAccelZ   = (TextView) findViewById(R.id.text_RobotAccelZ);
        mSpheroConnect   = (Button) findViewById(R.id.button_SpheroConnect);
        mSpheroCalibrate = (Button) findViewById(R.id.button_SpheroCalibrate);
        mSpheroForward   = (Button) findViewById(R.id.button_SpheroForward);
        
        mSpheroConnect.setOnClickListener(this);
        mSpheroCalibrate.setOnClickListener(this);
        mSpheroForward.setOnClickListener(this);
        
        startDeviceStreaming();
        Log.d(TAG, "I HAVE STARTED!!!!!!!!");

        Object retained = getLastNonConfigurationInstance();
        if(retained != null){
            mRobot = (Robot)retained;
            mRobotID.setText(mRobot.getUniqueId());
            startRobotStreaming();
        }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONNECT_ACTIVITY && resultCode == RESULT_OK){
            final String robot_id = data.getStringExtra(StartupActivity.EXTRA_ROBOT_ID);
            if(robot_id != null && !robot_id.equals("")){
                mRobot = RobotProvider.getDefaultProvider().findRobot(robot_id);
                mRobotID.setText(mRobot.getUniqueId());
                startRobotStreaming();
                Toast.makeText(this, "Connected to " + mRobot.getName(), Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mDeviceListener);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mDeviceListener, mMagnetSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mDeviceListener, mAccelSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onClick(View v) {
        if(v == mSpheroConnect){
            Intent spheroIntent = new Intent(this, StartupActivity.class);
            startActivityForResult(spheroIntent, CONNECT_ACTIVITY);
        }else if(v == mSpheroCalibrate){
            Log.d(TAG, "Robot ID: " + mRobot.getUniqueId());
            Intent spheroIntent = new Intent(this, CalibrationActivity.class);
            spheroIntent.putExtra(CalibrationActivity.ROBOT_ID_EXTRA, mRobot.getUniqueId());
            startActivity(spheroIntent);
        }else if(v == mSpheroForward){
            Log.d(TAG, "Forward Motion!");
            RollCommand.sendCommand(mRobot, 0.0f, 0.5f);
            handler.postDelayed(new Runnable(){
                public void run(){
                    RollCommand.sendStop(mRobot);
                }
            }, 1000);
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mRobot;
    }

    private void startDeviceStreaming(){
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mMagnetSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mDeviceListener = new DeviceSensorListener(mSensorDataHandler);
        mSensorManager.registerListener(mDeviceListener, mMagnetSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mDeviceListener, mAccelSensor, SensorManager.SENSOR_DELAY_UI);
    }

    private void startRobotStreaming(){
        if(mRobot != null){
            mRobotListener = new RobotSensorListener(mSensorDataHandler);
            int mask =
                SetDataStreamingCommand.DATA_STREAMING_MASK_MAGNETOMETER_X_FILTERED  |
                SetDataStreamingCommand.DATA_STREAMING_MASK_MAGNETOMETER_Y_FILTERED  |
                SetDataStreamingCommand.DATA_STREAMING_MASK_MAGNETOMETER_Z_FILTERED  |
                SetDataStreamingCommand.DATA_STREAMING_MASK_ACCELEROMETER_X_FILTERED |
                SetDataStreamingCommand.DATA_STREAMING_MASK_ACCELEROMETER_Y_FILTERED |
                SetDataStreamingCommand.DATA_STREAMING_MASK_ACCELEROMETER_Z_FILTERED ;

            //Specify a divisor. The frequency of responses that will be sent is 400hz divided by this divisor.
            final int divisor = 50;

            //Specify the number of frames that will be in each response. You can use a higher number to "save up" responses
            //and send them at once with a lower frequency, but more packets per response.
            final int packet_frames = 6;

            //Total number of responses before streaming ends. 0 is infinite.
            final int response_count = 0;

            //Send this command to Sphero to start streaming
            SetDataStreamingCommand.sendCommand(mRobot, divisor, packet_frames, mask, response_count);

            //Set the AsyncDataListener that will process each response.
            DeviceMessenger.getInstance().addAsyncDataListener(mRobot, mRobotListener);
        }
    }
}
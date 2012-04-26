package com.harrcharr.reverb;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harrcharr.reverb.pulse.Context;
import com.harrcharr.reverb.pulse.Mainloop;
import com.harrcharr.reverb.pulse.SinkInfo;

public class ReverbActivity extends Activity {
	static {
		System.loadLibrary("json");
		System.loadLibrary("sndfile");
		System.loadLibrary("pulsecommon-UNKNOWN.UNKNOWN");
		System.loadLibrary("pulse");
		System.loadLibrary("pulse_interface");
	}
	protected Mainloop m;
	protected Context c;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	System.out.println("poop");
    	final Context.SinkInfoCallback sinkInfoCb = new Context.SinkInfoCallback() {
			public void run(long iPtr) {
				final SinkInfo info = new SinkInfo(iPtr);
				runOnUiThread(new Runnable() {
					public void run() {
						TextView desc = (TextView)findViewById(R.id.sinkDesc);
						desc.setText(info.getDescription());
					}
				});
			}
		};
//    	final Context.SuccessCallback toggleMuteCb = new Context.SinkInfoCallback() {
//			@Override
//			public void run(final SinkInfo info) {
//				// TODO Auto-generated method stub
//				runOnUiThread(new Runnable() {
//					public void run() {
//						TextView desc = (TextView)findViewById(R.id.sinkDesc);
//						desc.setText(info.getDescription());
//					}
//				});
//			}
//		};
		m = new Mainloop();
		c = new Context(m);
    	new Thread(new Runnable() {
			public void run() {				 
				c.connect("192.168.0.9");
				c.getSinkInfo(1, sinkInfoCb);
			}
    	}).start();
    	
        final Button button = (Button) findViewById(R.id.loadSink);
        final EditText sinkIdx = (EditText) findViewById(R.id.sinkIndex);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	c.getSinkInfo(Integer.parseInt(sinkIdx.getText().toString()), 
            			sinkInfoCb);
            }
        });
    }
    
    public void onToggleClicked(View v) {
        // Perform action on clicks
        final EditText sinkIdx = (EditText) findViewById(R.id.sinkIndex);
//        int nSink = Integer.parseInt(sinkIdx.getText().toString());
        System.out.println("About to set sink mute, in ReverbActivity");
        if(c != null && c.isReady())
         	c.setSinkMute(0, ((ToggleButton)v).isChecked());
    }
}
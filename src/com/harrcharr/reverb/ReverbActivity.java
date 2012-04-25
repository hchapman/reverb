package com.harrcharr.reverb;

import android.app.Activity;
import android.os.Bundle;

import com.harrcharr.reverb.pulse.Context;
import com.harrcharr.reverb.pulse.Mainloop;

public class ReverbActivity extends Activity {
	static {
		System.loadLibrary("json");
		System.loadLibrary("sndfile");
		System.loadLibrary("pulsecommon-UNKNOWN.UNKNOWN");
		System.loadLibrary("pulse");
		System.loadLibrary("pulse_interface");
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	System.out.println("poop");
    	new Thread(new Runnable() {
			public void run() {
				Mainloop m = new Mainloop();
				Context c = new Context(m);
				 
				c.connect("192.168.0.9");
				c.getSinkInfo(1);
			}
    	}).start();
    }
}
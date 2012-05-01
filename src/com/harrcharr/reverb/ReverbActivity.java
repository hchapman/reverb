package com.harrcharr.reverb;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.harrcharr.reverb.pulse.InfoCallback;
import com.harrcharr.reverb.pulse.Mainloop;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;

public class ReverbActivity extends Activity {
	protected Mainloop m;
	protected PulseContext c;
	
	protected ListView mSinkInputView;
	protected ArrayList<SinkInput> sinkInputs;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	System.out.println("poop");
    	
    	sinkInputs = new ArrayList<SinkInput>();

		m = new Mainloop();
		c = new PulseContext(m);
		
    	final SinkInputAdapter siAdapter = new SinkInputAdapter(this, c);
    	mSinkInputView = (ListView)findViewById(R.id.sinkInputList);
    	mSinkInputView.setAdapter(siAdapter);
    	
    	new Thread(new Runnable() {
			public void run() {				 
				c.connect("192.168.0.9");
				c.subscribe();
				c.getSinkInputInfoList(siAdapter.getInfoCallback());
				c.subscribeSinkInput(siAdapter.getSubscriptionCallback());
			}
    	}).start();
    }
}
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

import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.InfoCallback;
import com.harrcharr.reverb.pulse.Mainloop;
import com.harrcharr.reverb.pulse.SinkInfo;
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
    	
    	mSinkInputView = (ListView)findViewById(R.id.sinkInputList);
    	mSinkInputView.setAdapter(new SinkInputAdapter(getBaseContext(), c,
    			sinkInputs));
    	
    	final InfoCallback sinkInfoCb = new InfoCallback() {
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
    	final InfoCallback sinkInputInfoCb = new InfoCallback() {
			public void run(long iPtr) {
				final SinkInput info = new SinkInput(c, iPtr);
				runOnUiThread(new Runnable() {
					public void run() {
						TextView desc = (TextView)findViewById(R.id.clientName);
						desc.setText(info.getName());
						sinkInputs.add(info);
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
		c = new PulseContext(m);
    	new Thread(new Runnable() {
			public void run() {				 
				c.connect("192.168.0.9");
				c.getSinkInfo(1, sinkInfoCb);
//				c.getClientInfo(0, null);
				c.getSinkInputInfoList(sinkInputInfoCb);
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
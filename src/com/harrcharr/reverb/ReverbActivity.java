/*******************************************************************************
 * Copyright (c) 2012 Harrison Chapman.
 * 
 * This file is part of Reverb.
 * 
 *     Reverb is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 * 
 *     Reverb is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Reverb.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Harrison Chapman - initial API and implementation
 ******************************************************************************/
package com.harrcharr.reverb;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.harrcharr.reverb.pulse.Mainloop;
import com.harrcharr.reverb.pulse.NotifyCallback;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;

public class ReverbActivity extends Activity {
	protected final String DEFAULT_SERVER = "192.168.1.104";
	
	protected Mainloop m;
	protected PulseContext mPulse;
	
	protected ListView mSinkInputView;
	protected ArrayList<SinkInput> sinkInputs;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	System.out.println("poop");
    	
    	getActionBar().setCustomView(R.layout.server_actionbar);
    	getActionBar().setDisplayShowTitleEnabled(false);
    	getActionBar().setDisplayShowCustomEnabled(true);
    	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	
    	((EditText)getActionBar().getCustomView().findViewById(R.id.serverUrl)).setText(DEFAULT_SERVER);
    	((Button)getActionBar().getCustomView().findViewById(R.id.serverChange)).setOnClickListener(
    			new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						connect(((EditText)getActionBar().getCustomView().findViewById(R.id.serverUrl)).getText().toString());
					}
				});
    	
    	sinkInputs = new ArrayList<SinkInput>();

    	final SinkInputFragment siFrag = (SinkInputFragment)getFragmentManager()
				.findFragmentById(R.id.siFrag);
    	
		m = new Mainloop();
		mPulse = new PulseContext(m);
		
		mPulse.setStateCallback(new NotifyCallback() {
			@Override
			public void run() {
				Log.d("Reverb", "Context status is now "+mPulse.getStatus());
				
				if (mPulse.getStatus() == 4) {
					mPulse.subscribe();
					
			    	siFrag.setPulseContext(mPulse);
			    	
					mPulse.getSinkInputInfoList(siFrag.getInfoCallback());
					//mPulse.subscribeSinkInput(siFrag.getSubscriptionCallback());
				}
			}
		});

		connect(DEFAULT_SERVER);
    }
    
    public void connect(String server) {
    	final SinkInputFragment siFrag = (SinkInputFragment)getFragmentManager()
				.findFragmentById(R.id.siFrag);
    
    	
    	Log.d("Reverb", server);
    	
    	if(mPulse.isConnected()) {
    		mPulse.close();
    		mPulse = new PulseContext(m);
    		
    		mPulse.setStateCallback(new NotifyCallback() {
    			@Override
    			public void run() {
    				Log.d("Reverb", "Context status is now "+mPulse.getStatus());
    				
    				if (mPulse.getStatus() == 4) {
    					mPulse.subscribe();
    					
    			    	siFrag.setPulseContext(mPulse);
    			    	
    					mPulse.getSinkInputInfoList(siFrag.getInfoCallback());
    					mPulse.subscribeSinkInput(siFrag.getSubscriptionCallback());
    				}
    			}
    		});
    	}
    	
    	try {
			mPulse.connect(server);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("Reverb", "weird");
			e.printStackTrace();
		}
		
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
}

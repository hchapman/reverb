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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.harrcharr.reverb.pulse.Mainloop;
import com.harrcharr.reverb.pulse.NotifyCallback;
import com.harrcharr.reverb.pulse.PulseContext;
import com.harrcharr.reverb.pulse.SinkInput;

public class ReverbActivity extends SherlockFragmentActivity {
	protected final String DEFAULT_SERVER = "192.168.1.104";
	
	protected Mainloop m;
	protected PulseContext mPulse;
	
	protected ListView mSinkInputView;
	protected ArrayList<SinkInput> sinkInputs;
	
	protected ActionBar mActionBar;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	System.out.println("poop");
    	
    	mActionBar = getSupportActionBar();
    	
    	mActionBar.setCustomView(R.layout.server_actionbar);
    	mActionBar.setDisplayShowTitleEnabled(false);
    	mActionBar.setDisplayShowCustomEnabled(true);
    	mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    	
    	((EditText)mActionBar.getCustomView()
    			.findViewById(R.id.serverUrl)).setText(DEFAULT_SERVER);
    	((Button)mActionBar.getCustomView()
    			.findViewById(R.id.serverChange)).setOnClickListener(
    			new OnClickListener() {
					public void onClick(View v) {
						connect(((EditText)mActionBar.getCustomView()
								.findViewById(R.id.serverUrl))
								.getText().toString());
					}
				});
    	
    	sinkInputs = new ArrayList<SinkInput>();

    	final SinkInputFragment siFrag = (SinkInputFragment)getSupportFragmentManager()
				.findFragmentById(R.id.siFrag);
    	
		m = new Mainloop();

		connect(DEFAULT_SERVER);
    }
    
    public synchronized void connect(final String server) {
    	final SinkInputFragment siFrag = (SinkInputFragment)getSupportFragmentManager()
				.findFragmentById(R.id.siFrag);
    
    	Log.d("Reverb", server);
    	
    	if(mPulse != null && mPulse.isConnected()) {
    		mPulse.close();
    		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    	}
    	
    	mPulse = new PulseContext(m);

    	mPulse.setConnectionReadyCallback(new NotifyCallback() {
    		@Override
    		public void run() {
    			
    			siFrag.setPulseContext(mPulse);

    			mPulse.getSinkInputInfoList(siFrag.getInfoCallback());
    			mPulse.subscribeSinkInput(siFrag.getSubscriptionCallback());

    			final Context context = getApplicationContext();
    			final CharSequence text = "Successfully connected to "+server;
    			final int duration = Toast.LENGTH_SHORT;

    			runOnUiThread(new Runnable() {
    				public void run() {
    					mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    					Toast toast = Toast.makeText(context, text, duration);
    					toast.show();
    				}
    			});
    			
    		}
    	});
    	mPulse.setConnectionFailedCallback(new NotifyCallback() {
    		@Override
    		public void run() {				
    			final Context context = getApplicationContext();
    			final CharSequence text = "Failed to connect to "+server;
    			final int duration = Toast.LENGTH_SHORT;

    			runOnUiThread(new Runnable() {
    				public void run() {
    					final Toast toast = Toast.makeText(context, text, duration);
    					toast.show();
    				}
    			});
    			
    		}
    	});
    	
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
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
}

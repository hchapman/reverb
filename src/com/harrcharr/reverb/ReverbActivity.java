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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.harrcharr.pulse.SinkInput;
import com.harrcharr.reverb.pulseutil.HasPulseManager;
import com.harrcharr.reverb.pulseutil.PulseConnectionListener;
import com.harrcharr.reverb.pulseutil.PulseManager;

public class ReverbActivity extends ActionBarTabsPager
implements HasPulseManager, PulseConnectionListener {	
	protected ListView mSinkInputView;
	protected ArrayList<SinkInput> sinkInputs;
	
	protected ActionBar mActionBar;
	
	public ReverbActivity() {
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);

    	mActionBar = getSupportActionBar();

    	Tab sinkInputTab = mActionBar.newTab().setText("Playback");
    	Tab sourceOutputTab = mActionBar.newTab().setText("Recording");
    	Tab sinkTab = mActionBar.newTab().setText("Outputs");
    	Tab sourceTab = mActionBar.newTab().setText("Inputs");
    	
    	mViewPager = (ViewPager)findViewById(R.id.pager);
    	
    	mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);
    	mTabsAdapter.addTab(sinkInputTab, SinkInputFragment.class, null);
    	mTabsAdapter.addTab(sourceOutputTab, SourceOutputFragment.class, null);
    	mTabsAdapter.addTab(sinkTab, SinkFragment.class, null);
    	mTabsAdapter.addTab(sourceTab, SourceFragment.class, null);
    	
    	mActionBar.setCustomView(R.layout.server_actionbar);
    	mActionBar.setDisplayShowTitleEnabled(false);
    	mActionBar.setDisplayShowCustomEnabled(true);
    	mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    	
    	((EditText)mActionBar.getCustomView()
    			.findViewById(R.id.serverUrl)).setText(ReverbSharedPreferences.getDefaultServer(this));
    	((ImageButton)mActionBar.getCustomView()
    			.findViewById(R.id.serverChange)).setOnClickListener(
    			new OnClickListener() {
					public void onClick(View v) {
						getPulseManager().connect(((EditText)mActionBar.getCustomView()
								.findViewById(R.id.serverUrl))
								.getText().toString());
					}
				});
    }
    
    @Override
    public void onStart() {
    	getPulseManager().addOnPulseConnectionListener(this);
    	super.onStart();
    }
    
    @Override
    public void onStop() {
    	getPulseManager().removeOnPulseConnectionListener(this);
    	super.onStop();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        Intent prefsIntent = new Intent(this.getApplicationContext(),
                ReverbPreferencesActivity.class);
         
        MenuItem preferences = menu.findItem(R.id.item1);
        preferences.setIntent(prefsIntent);
        return true;
    }
    
    public PulseManager getPulseManager() {
    	return ((HasPulseManager)getApplication()).getPulseManager();
    }
    
    public void onPulseConnectionReady(PulseManager p) {
    	Log.d("ReverbActivity", "PulseManager is Connected.");
		final Context context = getApplicationContext();
		final CharSequence text = "Successfully connected to "+p.getServerName();
		final int duration = Toast.LENGTH_SHORT;

		runOnUiThread(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		});
    }
    
    public void onPulseConnectionFailed(PulseManager p) {
		final Context context = getApplicationContext();
		final CharSequence text = "Failed to connect to "+p.getServerName();
		final int duration = Toast.LENGTH_SHORT;

		runOnUiThread(new Runnable() {
			public void run() {
				final Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
		});
    }
}

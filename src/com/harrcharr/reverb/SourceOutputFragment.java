package com.harrcharr.reverb;

import android.util.Log;

import com.harrcharr.pulse.InfoCallback;
import com.harrcharr.pulse.SourceOutput;
import com.harrcharr.pulse.SourceOutputInfoCallback;
import com.harrcharr.pulse.SubscriptionCallback;

public class SourceOutputFragment extends StreamNodeFragment<SourceOutput> {
	protected InfoCallback<SourceOutput> mInfoCallback = 
			new SourceOutputCallback();
	protected SubscriptionCallback mSubscriptionCallback = 
			new SourceOutputSubscriptionCallback();
	
	public SourceOutputFragment() {
		super();
	}
	
	private class SourceOutputCallback extends SourceOutputInfoCallback {
		public void run(final SourceOutput si) {
			int idx = si.getIndex();
			Log.d("Reverb [adapter]", "We're in a SourceOutputCallback run().");
			Log.d("Reverb", "Update index "+idx+"view group"+getViewGroup());
			
			if (getViewGroup() != null) {
				final StreamNodeView<SourceOutput> v = getStreamNodeViewByIndex(si.getIndex());	
				
				Log.d("Reverb", "Update node is "+v+" and index "+idx);
				if (v != null) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							v.setNode(si);
						}
					});
					
					Log.d("Reverb", "put with idx "+idx);
					return;
				}
			}
			
			Log.d("Reverb", "put with idx "+idx);
			SourceOutputFragment.this.addNode(si);
		}
	}
	
	private class SourceOutputSubscriptionCallback extends SubscriptionCallback {
		public void run(int type, int index) {
			Log.d("SourceOutputSubscriptionCallback", type + ", index: " + index);
			if (type == EVENT_REMOVE) {
				removeNode(index);
			} else {
				Log.w("Reverb", ""+getPulseContext());
				getPulseContext().getSourceOutputInfo(index, getInfoCallback());
			}
		}
	}

	@Override
	protected InfoCallback<SourceOutput> getInfoCallback() {
		return mInfoCallback;
	}

	@Override
	protected SubscriptionCallback getSubscriptionCallback() {
		return mSubscriptionCallback;
	}

	@Override
	protected void loadStreamNodeList() {
		getPulseContext().getSourceOutputInfoList(getInfoCallback());
	}

	@Override
	protected void subscribeStreamNode() {
		getPulseContext().subscribeSourceOutput(getSubscriptionCallback());
	}

}

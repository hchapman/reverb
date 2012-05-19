package com.harrcharr.reverb;

import android.util.Log;

import com.harrcharr.pulse.InfoCallback;
import com.harrcharr.pulse.SinkInfo;
import com.harrcharr.pulse.SinkInfoCallback;
import com.harrcharr.pulse.SinkInput;
import com.harrcharr.pulse.SubscriptionCallback;

public class SinkFragment extends StreamNodeFragment<SinkInfo> {
	protected InfoCallback<SinkInfo> mInfoCallback = 
			new SinkCallback();
	protected SubscriptionCallback mSubscriptionCallback = 
			new SinkInputSubscriptionCallback();
	
	public SinkFragment() {
		super();
	}
	
	private class SinkCallback extends SinkInfoCallback {
		public void run(final SinkInfo si) {
			int idx = si.getIndex();
			Log.d("Reverb [adapter]", "We're in a SinkInputCallback run().");
			Log.d("Reverb", "Update index "+idx+"view group"+getViewGroup());
			
			if (getViewGroup() != null) {
				final StreamNodeView<SinkInfo> v = getStreamNodeViewByIndex(si.getIndex());	
				
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
			SinkFragment.this.addNode(si);
		}
	}
	
	private class SinkInputSubscriptionCallback extends SubscriptionCallback {
		public void run(int type, int index) {
			Log.d("Reverb", type + " " + index);
			if (type == EVENT_REMOVE) {
				removeNode(index);
			} else {
				Log.w("Reverb", ""+getPulseContext());
				getPulseContext().getSinkInfo(index, getInfoCallback());
			}
		}
	}

	@Override
	protected InfoCallback<SinkInfo> getInfoCallback() {
		return mInfoCallback;
	}

	@Override
	protected SubscriptionCallback getSubscriptionCallback() {
		return mSubscriptionCallback;
	}

	@Override
	protected void loadStreamNodeList() {
		getPulseContext().getSinkInfoList(getInfoCallback());
	}

	@Override
	protected void subscribeStreamNode() {
		getPulseContext().subscribeSink(getSubscriptionCallback());
	}

}

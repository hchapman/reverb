package com.harrcharr.reverb;

import android.util.Log;

import com.harrcharr.pulse.InfoCallback;
import com.harrcharr.pulse.SinkInput;
import com.harrcharr.pulse.SinkInputInfoCallback;
import com.harrcharr.pulse.SubscriptionCallback;

public class SinkInputFragment extends StreamNodeFragment<SinkInput> {
	protected InfoCallback<SinkInput> mInfoCallback = 
			new SinkInputCallback();
	protected SubscriptionCallback mSubscriptionCallback = 
			new SinkInputSubscriptionCallback();
	
	public SinkInputFragment() {
		super();
	}
	
	private class SinkInputCallback extends SinkInputInfoCallback {
		public void run(final SinkInput si) {
			int idx = si.getIndex();
			Log.d("Reverb [adapter]", "We're in a SinkInputCallback run().");
			Log.d("Reverb", "Update index "+idx+"view group"+getViewGroup());
			
			if (getViewGroup() != null) {
				final StreamNodeView<SinkInput> v = getStreamNodeViewByIndex(si.getIndex());	
				
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
			SinkInputFragment.this.addNode(si);
		}
	}
	
	private class SinkInputSubscriptionCallback extends SubscriptionCallback {
		public void run(int type, int index) {
			Log.d("SinkInputSubscriptionCallback", type + ", index: " + index);
			if (type == EVENT_REMOVE) {
				removeNode(index);
			} else {
				Log.w("Reverb", ""+getPulseContext());
				getPulseContext().getSinkInputInfo(index, getInfoCallback());
			}
		}
	}

	@Override
	protected InfoCallback<SinkInput> getInfoCallback() {
		return mInfoCallback;
	}

	@Override
	protected SubscriptionCallback getSubscriptionCallback() {
		return mSubscriptionCallback;
	}

	@Override
	protected void loadStreamNodeList() {
		getPulseContext().getSinkInputInfoList(getInfoCallback());
	}

	@Override
	protected void subscribeStreamNode() {
		getPulseContext().subscribeSinkInput(getSubscriptionCallback());
	}

}

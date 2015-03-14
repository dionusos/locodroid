package hu.denes.locodroid.async;

import hu.denes.locodroid.ClientSingleton;
import hu.denes.locodroid.adapter.ControlCenterAdapter;

import java.net.InetAddress;
import java.util.ArrayList;

import android.os.AsyncTask;

public class DiscoverControlCentersAsyncTask extends
		AsyncTask<Object, Integer, ArrayList<String>> {

	private final ControlCenterAdapter adapter;

	public DiscoverControlCentersAsyncTask(
			final ControlCenterAdapter controlCenterAdapter) {
		adapter = controlCenterAdapter;
	}

	@Override
	protected ArrayList<String> doInBackground(final Object... params) {
		final ArrayList<String> hosts = new ArrayList<String>();
		for (final InetAddress a : (ClientSingleton.getInstance().getClient()
				.discoverHosts(54777, 5000))) {
			hosts.add(a.getHostAddress());
		}
		return hosts;
	}

	@Override
	protected void onPostExecute(final ArrayList<String> result) {
		adapter.setHosts(result);
		adapter.notifyDataSetChanged();
		super.onPostExecute(result);
	}

}

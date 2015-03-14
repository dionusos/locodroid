package hu.denes.locodroid;

import java.net.InetAddress;
import java.util.ArrayList;

import android.os.AsyncTask;

import com.esotericsoftware.kryonet.Client;

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
		for (final InetAddress a : (((Client) (params[0])).discoverHosts(54777,
				5000))) {
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

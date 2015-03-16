package hu.denes.locodroid.async;

import hu.denes.locodroid.ClientSingleton;
import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.adapter.LocoListAdapter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

public class QueryLocosAsyncTask extends
AsyncTask<Object, Integer, ArrayList<Loco>> {
	LocoListAdapter adapter;
	String hostAddress;

	public QueryLocosAsyncTask(final LocoListAdapter adapter,
			final String hostAddress) {
		this.adapter = adapter;
		this.hostAddress = hostAddress;
	}

	@Override
	protected ArrayList<Loco> doInBackground(final Object... params) {
		final ArrayList<Loco> locos = new ArrayList<Loco>();
		try {
			ClientSingleton
			.getInstance()
			.getClient()
			.connect(5000, InetAddress.getByName(hostAddress), 54555,
					54777);

			final String query = "{" + "\"target\": \"command-center\","
					+ "\"function\": {" + "\"type\": \"get-locos\","
					+ "\"value\": \"\"" + "}" + "}";
			ClientSingleton.getInstance().getClient().sendTCP(query);
			Log.i("LOCOLIST", query);

		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return locos;

	}

	@Override
	protected void onPostExecute(final ArrayList<Loco> result) {
		adapter.notifyDataSetChanged();
		super.onPostExecute(result);
	}
}

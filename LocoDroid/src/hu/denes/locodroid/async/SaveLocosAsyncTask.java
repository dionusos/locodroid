package hu.denes.locodroid.async;

import hu.denes.locodroid.ClientSingleton;
import hu.denes.locodroid.adapter.Loco;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class SaveLocosAsyncTask extends AsyncTask<Loco, Integer, Object> {
	String hostAddress;

	public SaveLocosAsyncTask(final String hostAddress) {
		this.hostAddress = hostAddress;
	}

	@Override
	protected Object doInBackground(final Loco... params) {
		try {
			ClientSingleton
					.getInstance()
					.getClient()
					.connect(5000, InetAddress.getByName(hostAddress), 54555,
							54777);
			final Loco l = params[0];
			final String query = "{" + "\"target\": \"command-center\","
					+ "\"function\": {" + "\"type\": \"save-loco\","
					+ "\"value\": {" + "\"address\": " + l.getAddress() + ","
					+ "\"name\":" + l.getName() + "," + "\"max-speed\":"
					+ l.getMaxSpeed() + "}" + "}" + "}";
			ClientSingleton.getInstance().getClient().sendTCP(query);
			Log.i("LOCOLIST", query);

		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	@Override
	protected void onPostExecute(final Object result) {
		super.onPostExecute(result);
	}
}

package hu.denes.locodroid.async;

import hu.denes.locodroid.ClientSingleton;

import java.io.IOException;
import java.net.InetAddress;

import android.os.AsyncTask;

public class TrainDriverCommandAsyncTask extends
		AsyncTask<String, Integer, Object> {

	@Override
	protected Object doInBackground(final String... params) {
		try {
			ClientSingleton
					.getInstance()
					.getClient()
					.connect(5000, InetAddress.getByName(params[1]), 54555,
							54777);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		ClientSingleton.getInstance().getClient().sendTCP(params[0]);
		return null;
	}

}

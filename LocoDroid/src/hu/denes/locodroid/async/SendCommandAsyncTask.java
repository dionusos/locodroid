package hu.denes.locodroid.async;

import hu.denes.locodroid.ClientSingleton;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.AsyncTask;

import com.esotericsoftware.kryonet.Client;

public class SendCommandAsyncTask extends AsyncTask<String, Integer, Object> {

	@Override
	protected Object doInBackground(final String... params) {
		final Client client = ClientSingleton.getInstance().getClient();
		try {
			client.connect(5000, InetAddress.getByName(params[0]), 54555, 54777);
			client.sendTCP(params[1]);
		} catch (final UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

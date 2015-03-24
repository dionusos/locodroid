package hu.denes.locodroid.service;

import hu.denes.locodroid.ClientSingleton;
import hu.denes.locodroid.Globals;
import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.async.SendCommandAsyncTask;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class NetworkCommunicationService extends Service {

	Client client;
	String hostAddress;
	Context _this;

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			Log.i("NetworkCommunication", "broadcast received");
			if (intent == null) {
				return;
			}
			if ("getLocoList".equals(intent.getStringExtra("job"))) {
				Log.i("NetworkCommunication", "getLocoList broadcast received");
				final String query = "{" + "\"target\": \"command-center\","
						+ "\"function\": {" + "\"type\": \"get-locos\","
						+ "\"value\": \"\"" + "}" + "}";
				sendData(query);
			} else if ("getAttachedLocoList".equals(intent
					.getStringExtra("job"))) {
				Log.i("NetworkCommunication",
						"getAttachedLocoList broadcast received");
				final String query = "{" + "\"target\": \"command-center\","
						+ "\"function\": {"
						+ "\"type\": \"get-attached-locos\"," + "\"value\": \""
						+ intent.getIntExtra("locoAddress", 0) + "\"" + "}"
						+ "}";
				sendData(query);
			}
		};
	};

	@Override
	public void onCreate() {
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("GET_LOCO_LIST"));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("GET_ATTACHED_LOCO_LIST"));
		super.onCreate();
	}

	private void sendData(final String data) {
		new SendCommandAsyncTask().execute(hostAddress, data);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		if (intent != null) {
			hostAddress = intent.getStringExtra("serverAddress");
		} else {
			hostAddress = "127.0.0.1";
		}
		client = ClientSingleton.getInstance().getClient();
		client.start();
		_this = this;
		client.addListener(new Listener() {
			@Override
			public void received(final Connection connection,
					final Object object) {
				if (object instanceof String) {
					final String response = (String) object;
					if (!response.startsWith("{")) {
						return;
					}
					try {
						final JSONObject jo = new JSONObject(response);
						if ("client".equals(jo.get("target"))) {
							final JSONObject function = jo
									.getJSONObject("function");
							if ("answer-get-locos".equals(function.get("type"))) {
								final ArrayList<Loco> locos = new ArrayList<Loco>();
								final JSONArray jLocos = function
										.getJSONArray("value");
								for (int i = 0; i < jLocos.length(); ++i) {
									final JSONObject jLoco = (JSONObject) jLocos
											.get(i);
									final Loco loco = new Loco(jLoco
											.getInt("address"));
									loco.setName(jLoco.getString("name"));
									locos.add(loco);
								}
								final Intent i = new Intent(
										"LOCO_LIST_RECEIVED");
								Globals.GLOBAL_LOCO_LIST = locos;
								i.putExtra("locoList", "globals");
								LocalBroadcastManager.getInstance(_this)
										.sendBroadcast(i);
							} else if ("answer-get-attached-locos"
									.equals(function.get("type"))) {
								final ArrayList<Loco> locos = new ArrayList<Loco>();
								final JSONArray jLocos = function
										.getJSONArray("value");
								for (int i = 0; i < jLocos.length(); ++i) {
									final JSONObject jLoco = (JSONObject) jLocos
											.get(i);
									final Loco loco = new Loco(jLoco
											.getInt("address"));
									loco.setName(jLoco.getString("name"));
									loco.setTicked(jLoco.getBoolean("attached"));
									locos.add(loco);
								}
								final Intent i = new Intent(
										"ATTACHED_LOCO_LIST_RECEIVED");
								Globals.GLOBAL_LOCO_LIST = locos;
								i.putExtra("locoList", "globals");
								LocalBroadcastManager.getInstance(_this)
										.sendBroadcast(i);
							}
						}
					} catch (final JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		client.stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}

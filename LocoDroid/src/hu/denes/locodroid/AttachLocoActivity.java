package hu.denes.locodroid;

import hu.denes.locodroid.adapter.AttachedLocoListAdapter;
import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.adapter.LocoListAdapter;
import hu.denes.locodroid.async.SendCommandAsyncTask;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;

public class AttachLocoActivity extends Activity implements OnRefreshListener {

	AttachedLocoListAdapter adapter;
	int locoAddress;
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (intent == null) {
				return;
			}

			final ArrayList<Loco> l = Globals.GLOBAL_LOCO_LIST;
			if (l == null) {
				return;
			}
			adapter.setLocos(l);
			adapter.notifyDataSetChanged();
			swipeRefreshLayout.setRefreshing(false);
		}

	};
	private String controlCenterAddress;
	private ListView listView;
	SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attach_loco);
		final Intent intent = getIntent();
		controlCenterAddress = intent.getStringExtra("hostAddress");
		locoAddress = intent.getIntExtra("locoAddress", 0);
		adapter = new AttachedLocoListAdapter(controlCenterAddress, locoAddress);
		listView = (ListView) findViewById(R.id.list);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshAttachedLocoList);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW,
				Color.GREEN, Color.BLUE);
		swipeRefreshLayout.setRefreshing(true);
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("ATTACHED_LOCO_LIST_RECEIVED"));

		final Intent i = new Intent("GET_ATTACHED_LOCO_LIST");
		i.putExtra("job", "getAttachedLocoList");
		i.putExtra("locoAddress", locoAddress);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);

		final Context _this = this;

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final Loco loco = ((Loco) ((LocoListAdapter) listView
						.getAdapter()).getItem(position));
				final CheckBox c = (CheckBox) view
						.findViewById(R.id.selectAttachedLococheckBox);
				c.setChecked(!c.isChecked());
				Log.i("AttachLocoSelectedItem: ", "" + position);
				String request;
				if (c.isChecked()) {
					request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ locoAddress
							+ ",	\"type\": \"add-loco-to-train\", \"value\": "
							+ loco.getAddress() + " } }";
				} else {
					request = "{\"target\": \"loco\",\"function\": {\"address\": "
							+ locoAddress
							+ ",	\"type\": \"remove-loco-from-train\", \"value\": \""
							+ ((Loco) adapter.getItem(position)).getAddress()
							+ "\"} }";
				}
				sendCommand(request);

			}
		});

		listView.setAdapter(adapter);
		registerForContextMenu(listView);

	}

	private void sendCommand(final String request) {

		new SendCommandAsyncTask().execute(controlCenterAddress, request);

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.attach_loco, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		final Intent i = new Intent("GET_ATTACHED_LOCO_LIST");
		i.putExtra("job", "getAttachedLocoList");
		i.putExtra("hostAddress", controlCenterAddress);
		i.putExtra("locoAddress", locoAddress);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);
	}
}

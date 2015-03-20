package hu.denes.locodroid;

import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.adapter.LocoListAdapter;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class LocoListActivity extends ListActivity {

	public final String ACTION = "LOCO_LIST_RECEIVED";

	String controlCenterAddress;
	LocoListAdapter adapter;
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
		}

	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_loco_list);
		final Intent intent = getIntent();
		controlCenterAddress = intent.getStringExtra("hostAddress");
		adapter = new LocoListAdapter(controlCenterAddress);

		setListAdapter(adapter);
		registerForContextMenu(getListView());

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("LOCO_LIST_RECEIVED"));

		final Intent i = new Intent("GET_LOCO_LIST");
		i.putExtra("job", "getLocoList");
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);

	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.loco_list, menu);
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
		if (id == R.id.refreshListMenuItem) {
			final Intent i = new Intent("GET_LOCO_LIST");
			i.putExtra("job", "getLocoList");
			i.putExtra("hostAddress", controlCenterAddress);
			LocalBroadcastManager.getInstance(this).sendBroadcast(i);
			Log.i("LocoListActivity", "refresh broadcast sent");
			return true;
		}
		if (id == R.id.addNewLocoMenuItem) {
			final Intent intent = new Intent(this, AddNewLocoActivity.class);
			intent.putExtra("hostAddress", controlCenterAddress);
			intent.putExtra("loco-type", "new-loco");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		final Loco loco = ((Loco) ((LocoListAdapter) getListAdapter())
				.getItem(position));
		final Intent intent = new Intent(this, TrainDriverActivity.class);
		intent.putExtra("hostAddress", controlCenterAddress);
		intent.putExtra("locoAddress", loco.getAddress());
		intent.putExtra("locoName", loco.getName());
		startActivity(intent);
		// super.onListItemClick(l, v, position, id);
	}
}

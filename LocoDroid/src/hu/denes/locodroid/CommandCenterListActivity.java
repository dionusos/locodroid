package hu.denes.locodroid;

import hu.denes.locodroid.adapter.ControlCenterAdapter;
import hu.denes.locodroid.service.NetworkCommunicationService;

import java.net.InetAddress;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CommandCenterListActivity extends Activity implements OnRefreshListener {

	InetAddress commandCenterAddress;
	int currentLocoAddress = 0;
	ControlCenterAdapter adapter;

	private ListView listView;
	private SwipeRefreshLayout swipeRefreshLayout;

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (intent == null) {
				return;
			}
			swipeRefreshLayout.setRefreshing(false);
			adapter.notifyDataSetChanged();
		}

	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_command_center);

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("NETWORK-DISCOVERED"));

		listView = (ListView) findViewById(R.id.list);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshCommandCenterList);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW,
				Color.GREEN, Color.BLUE);

		final Context _this = this;

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final String hostAddress = ((String) ((ControlCenterAdapter) listView
						.getAdapter()).getItem(position));
				// create service
				final Intent i = new Intent(_this.getApplicationContext(),
						NetworkCommunicationService.class);
				i.putExtra("serverAddress", hostAddress);
				startService(i);

				final Intent intent = new Intent(_this, LocoListActivity.class);
				intent.putExtra("hostAddress", hostAddress);
				startActivity(intent);

			}
		});

		ClientSingleton.getInstance().getClient().start();

		adapter = new ControlCenterAdapter();
		listView.setAdapter(adapter);
		registerForContextMenu(listView);
		onRefresh();

	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		adapter.save(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(final Bundle state) {
		adapter.restore(state);
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onResume() {

		super.onResume();
	};

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (id == R.id.exit) {
			final Intent i = new Intent(this, NetworkCommunicationService.class);
			stopService(i);
			System.exit(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		final int menuItemIndex = item.getItemId();
		if (menuItemIndex == 0) {
			final String hostAddress = ((String) ((ControlCenterAdapter) listView
					.getAdapter()).getItem(info.position));

			final Intent intent = new Intent(this, LocoListActivity.class);
			intent.putExtra("hostAddress", hostAddress);
			startActivity(intent);

		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	@Override
	public void onRefresh() {
		swipeRefreshLayout.setRefreshing(true);
		adapter.setContext(this);
		adapter.refresh();
	}

}

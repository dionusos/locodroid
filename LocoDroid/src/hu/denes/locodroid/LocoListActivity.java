package hu.denes.locodroid;

import hu.denes.locodroid.adapter.Loco;
import hu.denes.locodroid.adapter.LocoListAdapter;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class LocoListActivity extends Activity implements OnRefreshListener {

	public final String ACTION = "LOCO_LIST_RECEIVED";
	private ListView listView;

	String controlCenterAddress;
	LocoListAdapter adapter;

	SwipeRefreshLayout swipeRefreshLayout;
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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loco_list);
		final Intent intent = getIntent();
		controlCenterAddress = intent.getStringExtra("hostAddress");
		adapter = new LocoListAdapter(controlCenterAddress);
		listView = (ListView) findViewById(R.id.list);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLocoList);
		swipeRefreshLayout.setOnRefreshListener(this);
		swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW,
				Color.GREEN, Color.BLUE);
		final Context _this = this;

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final Loco loco = ((Loco) ((LocoListAdapter) listView
						.getAdapter()).getItem(position));
				final Intent intent = new Intent(_this,
						TrainDriverActivity.class);
				intent.putExtra("hostAddress", controlCenterAddress);
				intent.putExtra("locoAddress", loco.getAddress());
				intent.putExtra("locoName", loco.getName());
				startActivity(intent);

			}
		});
		((Button) findViewById(R.id.addLocoButton))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent(_this,
						AddNewLocoActivity.class);
				intent.putExtra("hostAddress", controlCenterAddress);
				intent.putExtra("loco-type", "new-loco");
				startActivity(intent);
			}
		});

		listView.setAdapter(adapter);
		registerForContextMenu(listView);

		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter("LOCO_LIST_RECEIVED"));

		final Intent i = new Intent("GET_LOCO_LIST");
		i.putExtra("job", "getLocoList");
		swipeRefreshLayout.setRefreshing(true);
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		final Intent i = new Intent("GET_LOCO_LIST");
		i.putExtra("job", "getLocoList");
		i.putExtra("hostAddress", controlCenterAddress);
		LocalBroadcastManager.getInstance(this).sendBroadcast(i);
	}
}

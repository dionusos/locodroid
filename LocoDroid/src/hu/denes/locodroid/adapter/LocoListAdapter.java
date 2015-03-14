package hu.denes.locodroid.adapter;

import hu.denes.locodroid.ClientSingleton;
import hu.denes.locodroid.R;
import hu.denes.locodroid.async.QueryLocosAsyncTask;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class LocoListAdapter extends BaseAdapter {
	ArrayList<Loco> locos;
	String hostAddress;

	public LocoListAdapter(final String hostAddress) {
		this.hostAddress = hostAddress;
		locos = new ArrayList<Loco>();
		final BaseAdapter a = this;
		ClientSingleton.getInstance().getClient().addListener(new Listener() {
			@Override
			public void received(final Connection connection,
					final Object object) {
				if (object instanceof String) {
					final String response = (String) object;
					try {
						final JSONObject jo = new JSONObject(response);
						if ("client".equals(jo.get("target"))) {
							final JSONObject function = jo
									.getJSONObject("function");
							if ("answer-get-locos".equals(function.get("type"))) {
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
							}
						}

					} catch (final JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void refresh() {
		locos.clear();

		final QueryLocosAsyncTask t = new QueryLocosAsyncTask(this, hostAddress);
		t.execute();
	}

	@Override
	public int getCount() {

		return locos.size();
	}

	@Override
	public Object getItem(final int position) {
		return locos.get(position);

	}

	@Override
	public long getItemId(final int position) {

		return 0;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final Loco loco = locos.get(position);
		if (loco == null) {
			return null;
		}
		final LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View locoView = inflater.inflate((R.layout.loco_list_item), null);
		((TextView) locoView.findViewById(R.id.locoNameTextView)).setText(loco
				.getName());
		((TextView) locoView.findViewById(R.id.locoAddressTextView))
		.setText(loco.getAddress().toString());

		return locoView;
	}

}

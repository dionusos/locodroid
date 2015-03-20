package hu.denes.locodroid.adapter;

import hu.denes.locodroid.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LocoListAdapter extends BaseAdapter {
	ArrayList<Loco> locos;
	String hostAddress;

	public void setLocos(final List<Loco> locos) {
		this.locos.clear();
		this.locos.addAll(locos);
	}

	public LocoListAdapter(final String hostAddress) {
		locos = new ArrayList<Loco>();
	}

	/*
	 * public void refresh() { locos.clear(); // final QueryLocosAsyncTask t =
	 * new QueryLocosAsyncTask(this, // hostAddress); // t.execute(); }
	 */

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

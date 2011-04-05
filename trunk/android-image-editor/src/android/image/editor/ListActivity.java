package android.image.editor;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListActivity extends android.app.ListActivity {

	@Override public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<String> testNames = new ArrayList<String>();
		testNames.add("Example 1");
		testNames.add("Example 2");
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testNames.toArray(new String[0])));
	}

	protected void onListItemClick (ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Object o = this.getListAdapter().getItem(position);
		String testName = o.toString();
		

		Bundle bundle = new Bundle();
		Intent intent;
		if (testName.equals("Example 1")) {
			intent = new Intent(this, MainActivity.class);
		} else {
			intent = new Intent(this, MainActivity1.class);
		}
		intent.putExtras(bundle);

		startActivity(intent);
	}
	

}

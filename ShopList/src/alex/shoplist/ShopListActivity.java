package alex.shoplist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import alex.shoplist.GetList;
import alex.shoplist.MainActivity.SendStatus;

public class ShopListActivity extends Activity implements OnItemClickListener {

	Order slist = null;
	Settings sets = null;
	ListView slView;
	final String tag = "ShopLIST Activity";
	final int ACTIVITY_getlist = 1;
	final int ShowItem = 2;
	final int ACTIVITY_customerdetail = 3;
	AlertDialog.Builder adb;
	ArrayAdapter<ShopListEntry> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		this.sets = new Settings(getApplicationContext());

		final Button gl = (Button) findViewById(R.id.getlistbutton);
		adapter = new ArrayAdapter<ShopListEntry>(this,
				android.R.layout.simple_list_item_1,
				new Vector<ShopListEntry>());
		;
		gl.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				try {
					Intent progress = new Intent(v.getContext(), GetList.class);
					slist.getShoplist().clear();
					startActivityForResult(progress,
							ShopListActivity.this.ACTIVITY_getlist);
					
				} catch (Exception e) {
					Log.i(ShopListActivity.this.tag, "OOOPS[" + e.getMessage()
							+ "]");

				}
			}
		});

		final TextView custv = (TextView) findViewById(R.id.customerview);

		final Button cd = (Button) findViewById(R.id.customerdet);
		cd.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				try {
					Intent intent = new Intent(v.getContext(),
							CustomerDetail.class);
					if (slist != null && slist.getCustomer() != null) {
						intent.putExtras(slist.getCustomer().toBundle());

					}
					startActivityForResult(intent,
							ShopListActivity.this.ACTIVITY_customerdetail);

				} catch (Exception e) {
					Log.i(ShopListActivity.this.tag, "OOOPS[" + e.getMessage()
							+ "]");

				}
			}
		});

		final Button sendReport = (Button) findViewById(R.id.report);

		sendReport.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				try {

					slist.sendXML();
				
					 AlertDialog ad = ShopListActivity.this.adb.create();
                     ad.setMessage("Отправка выполнена успешно");
                     ad.show();

				} catch (Exception e) {
					Log.i(ShopListActivity.this.tag, "OOOPS[" + e.getMessage()
							+ "]");

				}
			}
		});

		TextView tv = (TextView) findViewById(R.id.statuslabel);

		this.slist = Order.parse(getApplicationContext());
		if (this.slist == null) {
			Log.d(tag, "slist is null");

			// we need to do this to allow the list to have something to
			// display!
			// even though it is empty!
			this.slist = new Order(getApplicationContext());
		}
		if (slist.getCustomer() == null) {

			custv.setText("Нет данных");
		} else {
			custv.setText(slist.getCustomer().getName());
		}

		if (this.slist.getItemCount() == 0) {
			tv.setText("Список пуст");
		} else {
			tv.setText("There are " + this.slist.getItemCount() + " item.");
		}

		// get a reference to the list view
		this.slView = (ListView) findViewById(R.id.buylist);

		// setup data adapter
		adapter.clear();
		adapter.addAll(slist.getShoplist());

		// assign adapter to list view
		this.slView.setAdapter(adapter);

		// install handler
		this.slView.setOnItemClickListener(this);

		// hilight the first entry in the list...
		this.slView.setSelection(0);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ACTIVITY_getlist) {
			adapter.clear();
			slist.getShoplist().clear();
			slist = Order.parse(getApplicationContext());

			adapter.addAll(slist.getShoplist());
			adapter.notifyDataSetChanged();
			sendActive();

		}

		if (requestCode == ShowItem) {

			if (data != null) {

				Bundle b = data.getExtras();
				if (b != null) {

					ShopListEntry sle = ShopListEntry.fromBundle(b);
					slist.replace(sle);
					adapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onItemClick(AdapterView parent, View v, int position, long id) {

		ShopListEntry sle = slist.getList(position);
		if(sle == null)
			return;

		Log.i("ShopLIST APP", "item clicked! [" + sle.getListID() + "]");

		// a Job has been selected, let's get it ready to display
		Intent itemIntent = new Intent(v.getContext(), ShowItem.class);

		// use the toBundle() helper method to assist in pushing
		// data across the "Activity" boundary
		Bundle b = sle.toBundle();
		// jobintent.putExtra("android.intent.extra.INTENT", b);
		itemIntent.putExtras(b);
		// we start this as a "sub" activity, because it may get updated
		// and we need to track that (in the method below OnActivityResult)
		startActivityForResult(itemIntent, this.ShowItem);
	}

	public void sendActive() {

		Thread active= new Thread(new SendActive());
active.start();
	}

	class SendActive implements Runnable {

		public void run() {
			try {
				if(slist.getShoplist().isEmpty()){ 
					Log.d("Undefined order ID","0");
					return;
				}
				DefaultHttpClient client = new DefaultHttpClient();
				String serv = sets.getServer();
				String user = sets.getUsername();
				StringBuffer req = new StringBuffer("getShopList/setActive/?");
				req.append("username=");
				req.append(user);
				req.append("&custOrderID=");
				
				req.append(slist.getShoplist().get(0).getListID());
			
				HttpGet getRequest = new HttpGet(serv + req.toString());
				HttpResponse resp = client.execute(getRequest);
				Log.d("URL", getRequest.getRequestLine().toString());
				Log.d("Set Active", "Status "
						+ resp.getStatusLine().getStatusCode());
				client.getConnectionManager().shutdown();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};
	}	
	
	
}

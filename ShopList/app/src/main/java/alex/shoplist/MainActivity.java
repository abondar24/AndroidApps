package alex.shoplist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.os.Bundle;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import alex.shoplist.Settings;
import alex.shoplist.Order.SendReport;

public class MainActivity extends Activity {

	final int ACTIVITY_list = 1;
	final int ACTIVITY_getlist = 2;
	final int ACTIVITY_viewsettings = 3;
	final String tag = "ShopLIST APP";
	Boolean isOn = false;
	AlertDialog.Builder adb;
	Settings mysets = null;
	Order list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.adb = new AlertDialog.Builder(this);
		final Button gotolist = (Button) findViewById(R.id.listbutton);

		this.mysets = new Settings(getApplicationContext());

		gotolist.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				try {
					startActivityForResult(new Intent(v.getContext(),
							ShopListActivity.class),
							MainActivity.this.ACTIVITY_list);
				} catch (Exception e) {
					Log.i(MainActivity.this.tag, "OOOPS[" + e.getMessage()
							+ "]");
				}

			}

		});

		final Button viewsets = (Button) findViewById(R.id.setbutton);

		viewsets.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				try {
					startActivityForResult(new Intent(v.getContext(),
							ViewSettings.class),
							MainActivity.this.ACTIVITY_viewsettings);

				} catch (Exception e) {
					Log.i(MainActivity.this.tag, "OOOPS[" + e.getMessage()
							+ "]");

				}
			}
		});

		final CheckBox status = (CheckBox) findViewById(R.id.setEmpStat);

		status.setOnClickListener(new CheckBox.OnClickListener() {

			public void onClick(View v) {
				try {

			//		if (status.isChecked() == true) {
						sendStatus().start();
						if (isOn == true) {
							isOn = false;
							status.setText("Offline");
							sendStatus().start();
						} else {

							isOn = true;
							status.setText("Online");
							sendStatus().start();
						}

						Log.i(MainActivity.this.tag, "Item bought");
					//}

				} catch (Exception e) {
					Log.i(MainActivity.this.tag, "OOOPS[" + e.getMessage()
							+ "]");

				}
			}
		});

	}

	public Thread sendStatus() {

		return new Thread(new SendStatus());

	}

	class SendStatus implements Runnable {

		public void run() {
			try {

				DefaultHttpClient client = new DefaultHttpClient();
				String serv = mysets.getServer();
				String user = mysets.getUsername();
				StringBuffer req = new StringBuffer("getShopList/setStatus/?");
				req.append("username=");
				req.append(user);
				req.append("&isOn=");
				req.append(isOn);
				HttpGet getRequest = new HttpGet(serv + req.toString());
				HttpResponse resp = client.execute(getRequest);
				Log.d("URL", getRequest.getRequestLine().toString());
				Log.d("Sent Succesfully", "Status "
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

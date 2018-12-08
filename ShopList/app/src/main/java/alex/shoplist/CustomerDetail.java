package alex.shoplist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CustomerDetail extends Activity {

	CustomerEntry customer = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customerdetail);

		final TextView custID = (TextView) findViewById(R.id.custID);
		final TextView name = (TextView) findViewById(R.id.name);
		final TextView address = (TextView) findViewById(R.id.adreess);
		Intent startingIntent = getIntent();

		if (startingIntent != null) {

			Bundle b = startingIntent.getExtras();

			if (b == null) {
				Log.e("ShopLIST APP", "bad bundle");

			} else {
				customer = CustomerEntry.fromBundle(b);
				custID.setText(customer.getCustomerID());
				name.setText(customer.getName());
				address.setText(customer.getAddress());
				Log.d("Customer Detail", customer.toString() + "\n" + customer.toXMLString());
			}
		}

	}

}

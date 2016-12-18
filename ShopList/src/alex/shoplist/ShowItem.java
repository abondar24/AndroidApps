package alex.shoplist;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import android.app.Activity;
import android.content.Intent;
public class ShowItem extends Activity {

	Settings sets=null;
	ShopListEntry sle=null;
	final String tag = "ShowIem";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showitem);
		this.sets = new Settings(getApplicationContext());
		
	     final   StringBuilder sb = new StringBuilder();

	        String details = null;

	        Intent startingIntent = getIntent();

	        if (startingIntent != null) {
	        
	            Bundle b = startingIntent.getExtras();
	            if (b == null) {
	                Log.i(tag, "bad bundle");
	                details = "bad bundle?";
	            } else {
	            	
	                this.sle = ShopListEntry.fromBundle(b);
	                
	          	    sb.append("ID Cписка: " + this.sle.getListID() + "\n\n");
	          	    sb.append("Товар: " + this.sle.getItemName() + "\n\n");
	          	    sb.append("Количество: " + this.sle.getQuantity().toString() + "\n\n");
	          	    sb.append("Единицы измерения: " + this.sle.getUom() + "\n\n");
	          	    sb.append("Цена: " + this.sle.getPrice().toString() + "\n\n");
	          	    sb.append("Производитель: " + this.sle.getCompany() + "\n\n");
	          	    sb.append("Магазин: " + this.sle.getShopName() + "\n\n");
	                details = sb.toString();
	                Log.d("ShopList App, sle:", sle.toString());
	            }
	        } else {
	            details = "Нет данных.";
	            TextView tv = (TextView) findViewById(R.id.showItemdeatils);
	            tv.setText(details);
	            return;
	        }

	        TextView tv = (TextView) findViewById(R.id.showItemdeatils);
	        tv.setText(details);
	        TextView tv1 = (TextView) findViewById(R.id.itemsbought);
	        tv1.setText("Куплено" + this.sle.getUom().toString() + "\n\n") ;
	     final  EditText changeQuan = (EditText) findViewById(R.id.howmanybought);
	
	        
 
	       
	   final     CheckBox bought =(CheckBox) findViewById(R.id.boughtitem);
	 
	       bought.setOnClickListener(new CheckBox.OnClickListener() {

		        public void onClick(View v) {
		            try {

		                if (bought.isEnabled()){
		                	
		       	    	sle.setBought(true);
	       	    	
		       	    	bought.setEnabled(false);
		       	    	bought.setText("Куплено");
		       	   
		       	    	ShowItem.this.sle.setQuantity(Double.valueOf(changeQuan.getText().toString()));
		       	    	sb.append(changeQuan.getText().toString());
		       	    	Log.i(ShowItem.this.tag, "Item bought" );
		       	    
		       	   
		                }
		                      
		                            } catch (Exception e) { Log.i(ShowItem.this.tag, "OOOPS[" + e.getMessage() + "]");
		               
		            }
		        }
		    });
		   
	        
	        
	        
	    
	    	  
	      
		
}
	@Override
	public void onBackPressed() {

	    Intent data = new Intent();
	    Bundle bundle = new Bundle();

	   
	    data.putExtras(sle.toBundle());

	    if (getParent() == null) {
	        setResult(Activity.RESULT_OK, data);
	    } else {
	        getParent().setResult(Activity.RESULT_OK, data);
	    }

	    super.onBackPressed();
	}
 }	



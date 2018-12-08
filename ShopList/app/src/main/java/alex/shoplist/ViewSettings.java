package alex.shoplist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ViewSettings extends Activity{

	final String tag = "ShopLIST APP";
	   AlertDialog.Builder adb;
	   Settings mysets = null;
	
	   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewsettings);
		
		
		 this.adb = new AlertDialog.Builder(this);
		final Button save = (Button) findViewById(R.id.savebutton);
		final EditText serv = (EditText) findViewById(R.id.servername);
		final EditText usr =(EditText) findViewById(R.id.username);
		 this.mysets = new Settings(getApplicationContext());
		 serv.setText(this.mysets.getServer());
		 usr.setText(this.mysets.getUsername());
	   



	   save.setOnClickListener(new Button.OnClickListener() {

	        public void onClick(View v) {
	            try {
	            
                  
	            	
          if (serv.getText().length()==0) {

                        AlertDialog ad = ViewSettings.this.adb.create();
                      ad.setMessage("Введите адрес сервера");
                      ad.show();
                      return;
	              
	            } 
	       
          if (usr.getText().length()==0) {

              AlertDialog ad = ViewSettings.this.adb.create();
            ad.setMessage("Введите имя пользователя");
            ad.show();
            return;
        
      } 
 
          
             ViewSettings.this.mysets.setServer(serv.getText().toString());
             ViewSettings.this.mysets.setUsername(usr.getText().toString());
              ViewSettings.this.mysets.save();
           
	                            } catch (Exception e) { Log.i(ViewSettings.this.tag, "OOOPS[" + e.getMessage() + "]");
	               
	            }
	        }
	    });
	   
	  
	   
	
	   
	   
	}


}

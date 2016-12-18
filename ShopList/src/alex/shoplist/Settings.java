package alex.shoplist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {

	

	    private SharedPreferences _prefs = null;
	    private Editor _editor = null;
	
	    private String _serverurl = "http://android12.msi-wireless.com/";
	    private String username="user1";

	    public Settings(Context context) {
	        this._prefs = context.getSharedPreferences("PREFS_PRIVATE", Context.MODE_PRIVATE);
	        this._editor = this._prefs.edit();
	    }

	    public String getValue(String key, String defaultvalue) {
	        if (this._prefs == null) {
	            return "Unknown";
	        }

	        return this._prefs.getString(key, defaultvalue);
	    }

	    public void setValue(String key, String value) {
	        if (this._editor == null) {
	            return;
	        }

	        this._editor.putString(key, value);

	    }

	    

	    public String getUsername() {
	        if (this._prefs == null) {
	            return "user1";
	        }

	        this.username = this._prefs.getString("username", "user1");
	        return this.username;
	    }

	   

	    public void setUsername(String username) {
	        if (this._editor == null) {
	            return;
	        }
	        this._editor.putString("username", username);
	    }

	    public String getServer() {
	        if (this._prefs == null) {
	            return "http://android12.msi-wireless.com/";
	        }

	        this._serverurl = this._prefs.getString("serverurl", "http://android12.msi-wireless.com/");
	        return this._serverurl;
	    }

	   

	    public void setServer(String serverurl) {
	        if (this._editor == null) {
	            return;
	        }
	        this._editor.putString("serverurl", serverurl);
	    }
	    
	    public void save() {
	        if (this._editor == null) {
	            return;
	        }
	        this._editor.commit();
	    }
	

}

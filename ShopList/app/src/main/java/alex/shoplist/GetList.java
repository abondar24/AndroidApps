package alex.shoplist;



import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;



import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ListView;

public class GetList  extends Activity {
	Order slist= null;
	Settings sets=null;
	ListView slView; 
	final String tag = "GetList";
	ProgressDialog myprogress;
    Handler progresshandler;
    Boolean bCancel = false;
    
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getlist);
		this.sets = new Settings(getApplicationContext());
		this.myprogress = ProgressDialog.show(this, "Получение списка", "Пожалуйста подождите", true, false);
		this.progresshandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                switch (msg.what) {
                    case 0:
                        // update progress bar
                        GetList.this.myprogress.setMessage("" + (String) msg.obj);
                        break;
                    case 1:
                        GetList.this.myprogress.cancel();
                        finish();
                        break;
                    case 2: // error occurred
                        GetList.this.myprogress.cancel();
                        finish();
                        break;
                }
                // super.handleMessage(msg);
            }
        };
        Thread workthread = new Thread(new ReadList());

        workthread.start();
	}

	
	class ReadList implements Runnable {

        public void run() {
            InputSource is = null;

            // set up our message - used to convey progress information
            Message msg = new Message();
            msg.what = 0;

            try {
                // Looper.prepare();

                msg.obj = ("Connecting ...");
                GetList.this.progresshandler.sendMessage(msg);
                URL url = new URL(GetList.this.sets.getServer() + "getShopList" +"/?username="+GetList.this.sets.getUsername());
                // get our data via the url class
                is = new InputSource(url.openStream());

                // create the factory
                SAXParserFactory factory = SAXParserFactory.newInstance();

                // create a parser
                SAXParser parser = factory.newSAXParser();

                // create the reader (scanner)
                XMLReader xmlreader = parser.getXMLReader();

                // instantiate our handler
                ShopListHandler slHandler = new ShopListHandler(getApplication().getApplicationContext(),
                    GetList.this.progresshandler);

                // assign our handler
                xmlreader.setContentHandler(slHandler);

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Parsing ...");
                GetList.this.progresshandler.sendMessage(msg);
                Log.d("ShopList App", "Parsing....");
                // perform the synchronous parse
                xmlreader.parse(is);

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Parsing Complete");
               GetList.this.progresshandler.sendMessage(msg);

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Saving Shop List");
               GetList.this.progresshandler.sendMessage(msg);

               Log.d(tag, "Try to save....");
                slHandler.getList().persist();
                Log.d(tag, "Saved....");

                msg = new Message();
                msg.what = 0;
                msg.obj = ("Shop List Saved.");
               GetList.this.progresshandler.sendMessage(msg);

                msg = new Message();
                msg.what = 1;
                GetList.this.progresshandler.sendMessage(msg);

            } catch (Exception e) {
                Log.d("ShopLIST APP", "Exception: " + e.getMessage());
                msg = new Message();
                msg.what = 2; // error occured
                msg.obj = ("Caught an error retrieving Job data: " + e.getMessage());
               GetList.this.progresshandler.sendMessage(msg);

            }
        }
    }

	
}

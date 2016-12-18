package alex.shoplist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class Order {

	private Context _context = null;
	private Long listId;
	private List<ShopListEntry> shoplist = new Vector<ShopListEntry>(0);
	private CustomerEntry customer;
	Settings mysets = null;

	Order(Context c) {
		this._context = c;
		this.shoplist = new Vector<ShopListEntry>(0);
		this.customer = new CustomerEntry();
		this.listId = 0L;

	}

	int addList(ShopListEntry sl) {
		this.shoplist.add(sl);
		return this.shoplist.size();
	}

	public CustomerEntry getCustomer() {
		return customer;
	}

	ShopListEntry getList(int location) {
		if (shoplist.isEmpty()){
			return null;
			
		}
		return this.shoplist.get(location);
	}

	List<ShopListEntry> getAllLists() {
		return this.shoplist;
	}

	int getItemCount() {
		return this.shoplist.size();
	}

	void replace(ShopListEntry newItem) {
		try {
			Order newlist = new Order(this._context);
			for (int i = 0; i < getItemCount(); i++) {
				ShopListEntry sle = getList(i);
				if (sle.getItemID().equals(newItem.getItemID())) {
					Log.d("ShopLIST APP", "Replacing Item");
					newlist.addList(newItem);
				} else {
					newlist.addList(sle);
				}
			}
			this.shoplist = newlist.shoplist;
			persist();
		} catch (Exception e) {

		}
	}

	void persist() {
		try {
			FileOutputStream fos = this._context.openFileOutput("Order.xml",
					Context.MODE_PRIVATE);
			fos.write(toXMLString().getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.d("Order persist", "Failed to write out file? "
					+ e.getCause().getMessage() + "\n" + e.getMessage());
		}
	}

	static Order parse(Context context) {
		try {
			FileInputStream fis = context.openFileInput("Order.xml");

			if (fis == null) {
				return null;
			}
			// we need an input source for the sax parser
			InputSource is = new InputSource(fis);

			// create the factory
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// create a parser
			SAXParser parser = factory.newSAXParser();

			// create the reader (scanner)
			XMLReader xmlreader = parser.getXMLReader();

			// instantiate our handler
			ShopListHandler slHandler = new ShopListHandler(context, null /*
																		 * no
																		 * progress
																		 * updates
																		 * when
																		 * reading
																		 * file
																		 */);

			// assign our handler
			xmlreader.setContentHandler(slHandler);

			// perform the synchronous parse
			xmlreader.parse(is);

			// clean up
			fis.close();

			// return our new joblist
			return slHandler.getList();
		} catch (Exception e) {
			StringBuffer sb = new StringBuffer();
			for (StackTraceElement el : e.getStackTrace()) {
				sb.append(el.toString());
			}
			Log.d("Order parse",
					"Error parsing job list xml file : " + e.getCause() + "\n"
							+ sb);
			return null;
		}
	}

	public String toXMLString() {
		StringBuilder sb = new StringBuilder("");

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		sb.append("<customerOrder>");
		sb.append("<listId>");
		sb.append(getListId());
		sb.append("</listId>");
		if (customer != null)
			sb.append(customer.toXMLString());
		else
			sb.append("<customer/>");
		sb.append("<ShopList>");
		for (int i = 0; i < getItemCount(); i++) {
			ShopListEntry sle = getList(i);
			sb.append(sle.toXMLString());
		}
		sb.append("</ShopList>");
		sb.append("</customerOrder>");

//        Log.d("Order send",sb.toString());		
		return sb.toString();
	}
	
	

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

public void sendXML(){
	
	Thread sender = new Thread(new SendReport());
	sender.start();
	
	
}	
	
class SendReport implements Runnable{	
	
	public void run() {
		try {
			mysets = new Settings(_context);
			DefaultHttpClient client = new DefaultHttpClient();
			String serv = mysets.getServer();
			HttpPost postRequest = new HttpPost(serv + "getShopList/" 
			+ mysets.getUsername() );

			String body = Order.this.toXMLString();
			Log.d("SendReport, body",body);
			
			StringEntity send = new StringEntity(body, HTTP.UTF_8);
			send.setContentType("application/xml");
			
/*			postRequest.addHeader("Content-Type","application/xml");
			postRequest.addHeader("Accept","text/xml");
*/			
			postRequest.setEntity(send);
			Header[] hdrs = postRequest.getAllHeaders();
			for(int i = 0; i< hdrs.length; i++)
			  Log.d("SendReport",hdrs[i].getName() + ":" + hdrs[i].getValue());
			Log.d("SendReport", postRequest.getRequestLine().getMethod() + postRequest.getRequestLine().getUri());
			

			HttpResponse resp = client.execute(postRequest);
			Log.d("Sent Succesfully","Status " + resp.getStatusLine().getStatusCode());
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
	public List<ShopListEntry> getShoplist() {
		return shoplist;
	}

	public void setShoplist(List<ShopListEntry> shoplist) {
		this.shoplist = shoplist;
	}

	public void setCustomer(CustomerEntry customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "Order [ listId" + listId + ", shoplist=" + shoplist
				+ ", customer=" + customer + "]";
	}

}

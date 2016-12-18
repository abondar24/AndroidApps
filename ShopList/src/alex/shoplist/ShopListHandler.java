package alex.shoplist;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ShopListHandler extends DefaultHandler {
	private Handler phandler = null;
	private Order order;
	private ShopListEntry sle;
	private String _lastElementName = "";
	private StringBuilder sb = null;
	private Context _context;

	ShopListHandler(Context c, Handler progresshandler) {
		this._context = c;
		if (progresshandler != null) {
			this.phandler = progresshandler;
			Message msg = new Message();
			msg.what = 0;
			msg.obj = ("Processing List");
			this.phandler.sendMessage(msg);
		}
	}

	public Order getList() {
		Message msg = new Message();
		msg.what = 0;
		msg.obj = ("Fetching List");
		if (this.phandler != null) {
			this.phandler.sendMessage(msg);
		}
		if(order != null)
		   Log.d("ShoplistHandler getList", order.toString());
		else 
			Log.d("ShoplistHandler getList", "order not parsed yet!!!");
		return order;
	}

	@Override
	public void startDocument() throws SAXException {
		Message msg = new Message();
		msg.what = 0;
		msg.obj = ("Starting Document");
		if (this.phandler != null) {
			this.phandler.sendMessage(msg);
		}

		// initialize our JobLIst object - this will hold our parsed contents
		order = new Order(this._context);

	}

	@Override
	public void endDocument() throws SAXException {
		Message msg = new Message();
		msg.what = 0;
		msg.obj = ("End of Document");
		if (this.phandler != null) {
			this.phandler.sendMessage(msg);
		}

	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		try {
			this.sb = new StringBuilder("");

			if (localName.equals("item")) {
				// create a new item

				Message msg = new Message();
				msg.what = 0;
				msg.obj = (localName);
				if (this.phandler != null) {
					this.phandler.sendMessage(msg);
				}

				this.sle = new ShopListEntry();

			} else if (localName.equals("customer")) {

				Message msg = new Message();
				msg.what = 0;
				msg.obj = (localName);
				if (this.phandler != null) {
					this.phandler.sendMessage(msg);

				}
			}

		} catch (Exception ee) {
			Log.d("ShopList App", ee.getStackTrace().toString());
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (localName.equals("item")) {
			// add our job to the list!
			this.order.addList(this.sle);
			Message msg = new Message();
			msg.what = 0;
			msg.obj = ("Shopping List # " + this.sle.getListID());
			if (this.phandler != null) {
				this.phandler.sendMessage(msg);
			}
			Log.d("ShopList App, parsing",
					"Shopping List # " + this.sle.toXMLString());
			return;
		}

		if (localName.equals("customer")) {
			// order.setCustomer(ce);
			Message msg = new Message();
			msg.what = 0;
			msg.obj = ("Customer # " + order.getCustomer().getCustomerID());
			if (this.phandler != null) {
				this.phandler.sendMessage(msg);
			}
			Log.d("ShopListHandler, parsing", "Customer " + order.getCustomer().toXMLString());
			return;
		}

		if (localName.equals("customerId")) {
			order.getCustomer().setCustomerID(this.sb.toString());
			return;
		}

		if (localName.equals("name")) {
			order.getCustomer().setName(this.sb.toString());
			return;
		}

		if (localName.equals("address")) {
			order.getCustomer().setAddress(this.sb.toString());
			return;
		}

		if (localName.equals("listId")) {
			if(sle != null)
			  sle.setListID(this.sb.toString());
			order.setListId(Long.valueOf(sb.toString()));
			return;
		}
		if (localName.equals("itemId")) {
			this.sle.setItemID(this.sb.toString());
			return;
		}
		if (localName.equals("itemName")) {
			this.sle.setItemName(this.sb.toString());
			return;
		}
		if (localName.equals("bought")) {
			this.sle.setBought(Boolean.valueOf(this.sb.toString())); // чтоделать??
			return;
		}
		if (localName.equals("quantity")) {
			this.sle.setQuantity(Double.valueOf(this.sb.toString()));// аналогично
			Log.d("ShopList App, quantity:", sle.getQuantity().toString());
			return;
		}
		if (localName.equals("uom")) {
			this.sle.setUOM(this.sb.toString());
			return;
		}
		if (localName.equals("price")) {
			this.sle.setPrice((Double.valueOf(this.sb.toString())));
			return;
		}
		if (localName.equals("company")) {
			this.sle.setCompany(this.sb.toString());
			return;
		}
		if (localName.equals("shopName")) {
			this.sle.setShopName(this.sb.toString());
			return;
		}

	}

	@Override
	public void characters(char ch[], int start, int length) {
		String theString = new String(ch, start, length);
		// Log.d("ShopLIST APP", "characters[" + theString + "]");
		this.sb.append(theString);
	}

}

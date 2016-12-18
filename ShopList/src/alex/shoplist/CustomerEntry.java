package alex.shoplist;

import android.os.Bundle;

public class CustomerEntry {
	
	private String customerID="";
	private String name="";
	private String address="";
	
	
	
	
	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerId) {
		this.customerID = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	

	@Override
	public String toString() {
		return "Customer [customerID=" + customerID + ", name=" + name
				+ ", address=" + address + "" + "]";
	}

	public String toXMLString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<customer>");
        sb.append("<customerId>");
        sb.append(getCustomerID());
        sb.append("</customerId>");
        sb.append("<name>").append(getName()).append("</name>");
        sb.append("<address>").append(getAddress()).append("</address>");
        
        sb.append("</customer>");
        return sb.toString();
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("customerID", getCustomerID());
        b.putString("name",getName() );
        b.putString("address", getAddress());
        
        return b;
    }

    public static CustomerEntry fromBundle(Bundle b) {
        CustomerEntry ce = new CustomerEntry();
        ce.setCustomerID(b.getString("customerID"));
        ce.setName(b.getString("name"));
        ce.setAddress(b.getString("address"));
     
        return ce;
    }

}

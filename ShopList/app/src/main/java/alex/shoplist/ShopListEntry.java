package alex.shoplist;

import android.os.Bundle;





public class ShopListEntry {
	
	private String listID="";
	private String itemID="";
	

	private String itemName="";
	private Boolean bought= false;
    private Double quantity= 0.0;
    private String uom="";
    private Double price = 0.0;
    public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

	private String company="";
    private String shopName="";
	
	public String getListID() {
		return listID;
	}
	public void setListID(String listID) {
		this.listID = listID;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Boolean getBought() {
		return bought;
	}
	public void setBought(Boolean bought) {
		this.bought = bought;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	public String getUom() {
		return uom;
	}
	public void setUOM(String uom) {
		this.uom = uom;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	@Override
	public String toString() {
		/*return "ShopListEntry [customerId=" + customerId + ", listID=" + listID
				+ ", itemName=" + itemName + ", Bought=" + bought
				+ ", Quantity=" + quantity + ", UOM=" + uom + ", company="
				+ company + ", shopName=" + shopName + "]";
				*/
		return getItemName()+ ":" + getQuantity() + " " + getUom();
	}
    
	public String toXMLString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<item>");
        sb.append("<listId>" + getListID() + "</listId>");
        sb.append("<itemId>" + getItemID() + "</itemId>");
        sb.append("<itemName>" + getItemName()+ "</itemName>");
        sb.append("<bought>" + getBought() + "</bought>");
        sb.append("<quantity>" + getQuantity() + "</quantity>");
        sb.append("<uom>" + getUom() + "</uom>");
        sb.append("<price>"+getPrice()+"</price>");
        sb.append("<company>" + getCompany() + "</company>");
        sb.append("<shopName>" + getShopName() + "</shopName>");
        sb.append("</item>");
        return sb.toString() ;
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("listId", getListID());
        b.putString("itemId", getItemID());
        b.putString("itemName",getItemName() );
        b.putBoolean("bought",getBought());
        b.putDouble("quantity",getQuantity());
        b.putString("uom",getUom());
        b.putDouble("price", getPrice());
        b.putString("company",getCompany());
        b.putString("shopName", getShopName());
        return b;
    }

    public static ShopListEntry fromBundle(Bundle b) {
        ShopListEntry sle = new ShopListEntry();
        sle.setListID(b.getString("listId"));
        sle.setItemID(b.getString("itemId"));
        sle.setItemName(b.getString("itemName"));
        sle.setBought(b.getBoolean("bought"));
        sle.setQuantity(b.getDouble("quantity"));
        sle.setUOM(b.getString("uom"));
        sle.setPrice(b.getDouble("price"));
        sle.setCompany(b.getString("company"));
        sle.setShopName(b.getString("shopName"));
        return sle;
    }
   
}

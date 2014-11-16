package struts;

public class SteamListing {
	String sessionId;
	public String listingId;
	public String itemName;
	int price;
	int priceWithoutFee;
	int appId;
	
	public SteamListing(String sessionId, String listingId, int price, int priceWithoutFee) {
		this.sessionId = sessionId;
		this.listingId = listingId;
		this.price = price;
		this.priceWithoutFee = priceWithoutFee;
	}
	
	public SteamListing(String itemName, String sessionId, String listingId, int price, int priceWithoutFee) {
		this.sessionId = sessionId;
		this.listingId = listingId;
		this.price = price;
		this.priceWithoutFee = priceWithoutFee;
		this.itemName = itemName;
	}
	
	public SteamListing(String sessionId, String listingId, int price, int priceWithoutFee, int appId) {
		this.sessionId = sessionId;
		this.listingId = listingId;
		this.price = price;
		this.priceWithoutFee = priceWithoutFee;
		this.appId = appId;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getListingId() {
		return listingId;
	}
	
	public void setListingId(String listingId) {
		this.listingId = listingId;
	}
	
	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public int getPriceWithoutFee() {
		return priceWithoutFee;
	}
	
	public void setPriceWithoutFee(int priceWithoutFee) {
		this.priceWithoutFee = priceWithoutFee;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof SteamListing)
			return this.listingId.equals(((SteamListing)o).listingId);
		else
			return super.equals(o);
	}

	public int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}
	
}

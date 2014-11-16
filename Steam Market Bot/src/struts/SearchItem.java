package struts;

import java.io.Serializable;

public class SearchItem implements Serializable {
	private String itemName;
	private String[] searchTerms;
	private int maxBuyout;
	private boolean exactMatch = false;
	
	public SearchItem(String itemName, String[] searchTerms, int maxBuyout) {
		this.itemName = itemName;
		this.searchTerms = searchTerms;
		this.maxBuyout = maxBuyout;
	}
	
	public SearchItem(String itemName, String[] searchTerms, int maxBuyout, boolean exactMatch) {
		this.itemName = itemName;
		this.searchTerms = searchTerms;
		this.maxBuyout = maxBuyout;
		this.exactMatch = exactMatch;
	}

	public String getItemName() {
		return itemName;
	}
	
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
	public String[] getSearchTerms() {
		return searchTerms;
	}
	
	public void setSearchTerms(String[] searchTerms) {
		this.searchTerms = searchTerms;
	}
	
	public int getMaxBuyout() {
		return maxBuyout;
	}
	
	public void setMaxBuyout(int maxBuyout) {
		this.maxBuyout = maxBuyout;
	}

	public boolean isExactMatch() {
		return exactMatch;
	}

	public void setExactMatch(boolean exactMatch) {
		this.exactMatch = exactMatch;
	}
	
	
	
	
}

package api;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JOptionPane;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import struts.PubKey;
import struts.SearchItem;
import struts.SteamAccount;
import struts.SteamListing;

public class Steam {
	static JSONParser parser = new JSONParser();
	static SteamAccount account;
	static int amount = 13;
	static SteamListing listedItem = null;
	static int threads = 50;
	static String itemName;

	public static void main(String[] args) throws Exception {
		ArrayList<SearchItem> searchItems =  new ArrayList<SearchItem>();
		
		
		// Knives
		searchItems.add(new SearchItem("Gut Knife", new String[]{"gut knife"}, 2500));
		searchItems.add(new SearchItem("Flip Knife", new String[]{"flip knife"}, 2500));
		searchItems.add(new SearchItem("Bayonet", new String[]{"bayonet"}, 2500));
		searchItems.add(new SearchItem("Karambit", new String[]{"karambit"}, 2500));
		
		// Cases
		searchItems.add(new SearchItem("Operation Phoenix Weapon Case", new String[]{"phoenix weapon case"}, 125));
		searchItems.add(new SearchItem("eSports Case", null, 55, true));
		searchItems.add(new SearchItem("Winter Offensive Weapon Case", null, 20, true));
		searchItems.add(new SearchItem("Operation Bravo Case", null, 150, true));
		searchItems.add(new SearchItem("eSports Winter Case", null, 7, true));
		
		// Weapons 
		searchItems.add(new SearchItem("P250 | Mehndi", new String[]{"p250 | mehndi"}, 250));
		searchItems.add(new SearchItem("AK-47 | Fire Serpent", new String[]{"fire serpent"}, 2500));
		searchItems.add(new SearchItem("??? | Asiimov", new String[]{"| asiimov"}, 2500));

		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./smitteh1103.items"));
		oos.writeObject(searchItems);
		oos.close();
		if(searchItems.size() > 0)
			return;
		account = login("", "", null);
		String emailCode = JOptionPane.showInputDialog("Email key");
		account = login("", "", emailCode, null);
		steamEligibilityCheck(account);
		

		while(true){
			ArrayList<SteamListing> listings; 
			try{
				listings = getRecentListings(account);
			}catch(Exception e){
				continue;
			}
			for(SteamListing listing : listings){
				for(SearchItem item : searchItems){
					if(item.isExactMatch()){
						if(listing.getItemName().equals(item.getItemName())){
							System.out.println(listing.getItemName() + " found price: $" + ((float)listing.getPrice() / 100f));
							if(listing.getPrice() <= item.getMaxBuyout()){
								System.out.println("Buying " + listing.getItemName() + " for $" + ((float)listing.getPrice() / 100f));
								JSONObject response = purchaseListing(account, listing);
								if(response != null){
									System.out.println(response.get("message"));
								}
							}
						}
					} else {
						for(String term : item.getSearchTerms()){
							if(listing.getItemName().toLowerCase().contains(term.toLowerCase())){
								System.out.println(listing.getItemName() + " found price: $" + ((float)listing.getPrice() / 100f));
								if(listing.getPrice() <= item.getMaxBuyout()){
									System.out.println("Buying " + listing.getItemName() + " for $" + ((float)listing.getPrice() / 100f));
									JSONObject response = purchaseListing(account, listing);
									if(response != null){
										System.out.println(response.get("message"));
									}
								}
								break;
							}
						}
					}
				}
			}
			
		//	Thread.sleep(10);
		}
		
	}
	
	
	public static JSONObject purchaseListing(SteamAccount account, SteamListing listing) throws Exception{
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		
		
		String postData = "sessionid="+account.getSessionId()+"&currency=1&subtotal="+listing.getPriceWithoutFee()+"&fee="+(listing.getPrice() - listing.getPriceWithoutFee())+"&total=" + listing.getPrice();
		
		String packet = "POST /market/buylisting/"+listing.getListingId()+" HTTP/1.1\r\n";
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0\r\n";
		packet += "Accept-Language: en-US,en;\r\n";
		packet += "Content-Type: application/x-www-form-urlencoded;\r\n";
		packet += "Referer: http://steamcommunity.com/market/listings/730/%E2%98%85%20Flip%20Knife%20|%20Boreal%20Forest%20(Field-Tested)\r\n";
		packet += "Content-Length: "+postData.length()+"\r\n";
		packet += "Origin: http://steamcommunity.com\r\n";
		packet += "Cookie: " + account.getCookie() + "\r\n";;
		packet += "Connection: close\r\n\r\n";

		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		System.out.println(packet);
		System.out.println(postData);
		bw.write(packet);
		bw.write(postData);
		
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		String line;
		JSONObject ret = null;
		while ((line = br.readLine()) != null) {
			if(line.equals(""))
				ret = (JSONObject) parser.parse(br.readLine());
			//System.out.println(line);
		}

		bw.close();
		br.close();
		return ret;
	}
	
	public static String genRandomString(){
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		String ret = "";
		for(int i = 0; i < 12; i++)
			ret += chars.charAt((int)(Math.random() * (double)chars.length()));
		
		return ret;
	}
	
	public static void steamEligibilityCheck(SteamAccount account) throws Exception{
		
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		
		String packet = "GET /market/eligibilitycheck/?goto=%2Fmarket%2Flistings%2F730%2FGalil%2520AR%2520%7C%2520Sandstorm%2520%28Field-Tested%29 HTTP/1.1\r\n";
		
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0\r\n";
		packet += "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;\r\n";
		packet += "Accept-Language: en-US,en;\r\n";
		packet += "Cookie: " + account.getCookie() + "\r\n";
		packet += "Connection: close\r\n\r\n";
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		bw.write(packet);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		String ret = "";
		HashMap<String, String> cookies = new HashMap<String, String>();
		
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				cookies.put(cookieKey, cookieValue);
			}
		}
		
		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; " ;
		}
		account.setSessionId(cookies.get("sessionid"));
		account.setCookie(account.getCookie() + c);

		bw.close();
		br.close();

	}
	
	
	public static double getWalletBalance(SteamAccount account) throws Exception{
		//itemName = URLEncoder.encode(itemName, "UTF-8");
		System.out.println("Getting wallet balance");
		URLConnection conn = new URL("https://steamcommunity.com/market/").openConnection();
		conn.addRequestProperty("Cookie", account.getCookie());
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String line;

		JSONObject response = null;
		double balance = 0.00d;
		
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			if(line.contains("marketWalletBalanceAmount")){
				balance = Double.parseDouble(parse(line, "#36;", "<"));
				break;
			}
		}
		
		br.close();
		

		
		//System.out.println(response.toJSONString());

		return balance;
	}
	
	public static ArrayList<SteamListing> getRecentListings(SteamAccount account) throws Exception{
		//itemName = URLEncoder.encode(itemName, "UTF-8");
		Charset charset = Charset.forName("UTF-8");
		URLConnection conn = new URL("https://steamcommunity.com/market/recent").openConnection();
		conn.addRequestProperty("Cookie", account.getCookie());
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		
		String line;

		JSONObject response = null;
		
		while ((line = br.readLine()) != null) {
			response = (JSONObject) parser.parse(line);
		}
		
		ArrayList<SteamListing> ret = new ArrayList<SteamListing>();
		
		JSONObject assets;
		JSONObject listings;
		
		try{
			assets = (JSONObject) response.get("assets");
			listings = (JSONObject) response.get("listinginfo");
		} catch(Exception e){
			br.close();
			
			return ret;
		}
		for(Object listingKey : listings.keySet()){
			try{
				JSONObject listing = (JSONObject) listings.get(listingKey);
				long fee = (Long)listing.get("converted_fee");
				long price = (Long)listing.get("converted_price");
				JSONObject listingAssets = (JSONObject) listing.get("asset");
				long appId = (Long)listingAssets.get("appid");
				String id = listingAssets.get("id").toString();
				String name = getListingName(assets, Long.toString(appId), id);
				ret.add(new SteamListing(name, listingKey.toString(), (int)(fee + price), (int)price, (int)appId));
			}catch(Exception e){
				continue;
			}
			//System.out.println(listing.get("listingid"));

			//System.out.println(name);
		}
		
		br.close();
		
		//System.out.println(response.toJSONString());

		return ret;
	}
	
	public static String getListingName(JSONObject assets, String appId, String id){
		JSONObject category = (JSONObject) assets.get(appId);
		for(Object childObject : category.keySet()){
			JSONObject child = (JSONObject) category.get(childObject);
			
			if(child.containsKey(id)){
				JSONObject item = (JSONObject) child.get(id);
				
				return item.get("market_name").toString();
			}
		}
		return "";
		
	}
	
	public static ArrayList<SteamListing> getListings2(SteamAccount account, String itemName) throws Exception{
		//itemName = URLEncoder.encode(itemName, "UTF-8");
		Charset charset = Charset.forName("UTF-8");
		URLConnection conn = new URL("https://steamcommunity.com/market/listings/" + itemName).openConnection();
		conn.addRequestProperty("Cookie", account.getCookie());
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
		
		String line;
		String ret = "";
		HashMap<String, String> cookies = new HashMap<String, String>();
		String sessionId = null;
		ArrayList<SteamListing> listings = new ArrayList<SteamListing>();
		search:
		while ((line = br.readLine()) != null) {
			//ret += line + "\r\n";
			//System.out.println(line);
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				cookies.put(cookieKey, cookieValue);
			}
			
			
			if(line.contains("g_sessionID") && sessionId == null)
				sessionId = parse(line, "ID = \"", "\"");
			
			if(line.contains("id=\"listing_") && line.contains("market_listing_row market_recent_listing_row ")){
				String listingId = parse(line, "id=\"listing_", "\"");
				
				for(int i = 0; i < 16; i++){
					String s = br.readLine();
					
					if(s.contains("Sold!"))
						continue search;
				}
				
				String price = br.readLine();
				//System.out.println(price);
				price = parse2(price, ";");
				
				int p = (int)(Float.parseFloat(price) * 100.0f);
				
				br.readLine();
				price = br.readLine();
				
				price = parse2(price, ";");
				
				int p2 = (int)(Float.parseFloat(price) * 100.0f);
				
				listings.add(new SteamListing(sessionId, listingId, p, p2));
			}
		}
		
		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; " ;
		}
		
		account.setCookie(account.getCookie() + c);

		br.close();

		return listings;
	}
	
	public static String parse2(String subject, String start){
		String accepted = "1234567890.";
		
		int s = subject.indexOf(start) + start.length();
		
		String ret = "";
		
		for(int i = s; i < subject.length(); i++){
			if(accepted.contains(subject.charAt(i) + ""))
				ret += subject.charAt(i);
			else
				break;
		}
		return ret;
		
	}
	
	public static ArrayList<SteamListing> getListings(SteamAccount account, String itemName) throws Exception{
		itemName = URLEncoder.encode(itemName, "UTF-8");
		
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		
		String packet = "GET /market/listings/730/Galil%20AR%20|%20Sandstorm%20(Field-Tested) HTTP/1.1\r\n";
		
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:28.0) Gecko/20100101 Firefox/28.0\r\n";
		packet += "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;\r\n";
		packet += "Accept-Language: en-US,en;\r\n";
		packet += "Cookie: " + account.getCookie() + "\r\n";
		packet += "Connection: close\r\n\r\n";
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		bw.write(packet);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		String ret = "";
		HashMap<String, String> cookies = new HashMap<String, String>();
		String sessionId = null;
		ArrayList<SteamListing> listings = new ArrayList<SteamListing>();
		while ((line = br.readLine()) != null) {
			//ret += line + "\r\n";
			System.out.println(line);
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				cookies.put(cookieKey, cookieValue);
			}
			
			
			if(line.contains("g_sessionID") && sessionId == null)
				sessionId = parse(line, "ID = \"", "\"");
			
			if(line.contains("id=\"listing_") && line.contains("market_listing_row market_recent_listing_row ")){
				String listingId = parse(line, "id=\"listing_", "\"");
				
				for(int i = 0; i < 17; i++){
					System.out.println(br.readLine());
				}
				
				String price = br.readLine();
				System.out.println(price);
				price = parse(price, "&#36;", " USD");
				
				int p = (int)(Float.parseFloat(price) * 100.0f);
				
				br.readLine();
				price = br.readLine();
				
				price = parse(price, "&#36;", " USD");
				
				int p2 = (int)(Float.parseFloat(price) * 100.0f);
				
				listings.add(new SteamListing(sessionId, listingId, p, p2));
			}
		}
		
		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; " ;
		}
		
		account.setCookie(account.getCookie() + c);

		bw.close();
		br.close();

		return null;
	}
	
	public static String parse(String subject, String start, String end) {
		int s = subject.indexOf(start) + start.length();
		int e = subject.indexOf(end, s);

		return subject.substring(s, e);
	}

	public static JSONObject getRSAKey(String username) throws Exception {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		String postData = "username=" + username + "&donotcache=" + (System.currentTimeMillis());
		String packet = "POST /login/getrsakey/ HTTP/1.1\r\n";
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0\r\n";
		packet += "Accept: application/json, text/javascript, */*r\n";
		packet += "Accept: */*\r\n";
		packet += "Accept-Language: en-US\r\n";
		packet += "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n";
		packet += "Connection: close\r\n";
		packet += "Content-Length: " + postData.length() + "\r\n\r\n";

		bw.write(packet);
		bw.write(postData);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		JSONObject ret = null;
		boolean read = false;

		while ((line = br.readLine()) != null) {
			if (line.equals("")) {
				ret = (JSONObject) parser.parse(br.readLine());
			}
		}

		bw.close();
		br.close();

		return ret;
	}

	public static SteamAccount login(String username, String password, String machineAuth) throws Exception {
		JSONObject key = getRSAKey(username);
		
		PubKey pKey = new PubKey(new BigInteger(key.get("publickey_mod").toString(), 16), new BigInteger(key.get("publickey_exp").toString(), 16));
		String encPass = new String(encrypt(password, pKey));
		encPass = URLEncoder.encode(encPass, "UTF-8");

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		String postData = "username=" + username + "&password=" + encPass + "&emailauth=&loginfriendlyname=&emailsteamid=&rsatimestamp=" + key.get("timestamp")
				+ "&remember_login=false";
		
		
		String packet = "POST /login/dologin/ HTTP/1.1\r\n";
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0\r\n";
		packet += "Accept: application/json, text/javascript, */*r\n";
		packet += "Accept: */*\r\n";
		packet += "Accept-Language: en-US\r\n";
		packet += "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n";
		packet += "Connection: close\r\n";
		if(machineAuth != null)
			packet += "Cookie: "+machineAuth+"\r\n";
		packet += "Content-Length: " + postData.length() + "\r\n\r\n";

		bw.write(packet);
		bw.write(postData);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		JSONObject ret = null;
		HashMap<String, String> cookies = new HashMap<String, String>();
		while ((line = br.readLine()) != null) {
			//System.out.println(line);
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				if(cookieKey.contains("steamMachineAuth"))
					machineAuth = cookieKey + "=" + cookieValue + "; ";
				cookies.put(cookieKey, cookieValue);
			}
			if (line.equals("")) {
				ret = (JSONObject) parser.parse(br.readLine());
				//System.out.println(ret.toJSONString());
			}
		}

		bw.close();
		br.close();
		
		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; " ;
		}
		
		c += "strInventoryLastContext=730_2; timezoneOffset=-9000,0; " + machineAuth;
		
		SteamAccount account = new SteamAccount(username, password, c, ret);
		account.setSteamMachineAuthCookie(machineAuth);
		
		return account;
	}
	
	public static SteamAccount login(String username, String password, String gid, String text, String machineAuth) throws Exception {
		JSONObject key = getRSAKey(username);
		
		PubKey pKey = new PubKey(new BigInteger(key.get("publickey_mod").toString(), 16), new BigInteger(key.get("publickey_exp").toString(), 16));
		String encPass = new String(encrypt(password, pKey));
		
		encPass = URLEncoder.encode(encPass, "UTF-8");
		

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		String postData = "username=" + username + "&password=" + encPass + "&emailauth=&loginfriendlyname=&emailsteamid=&rsatimestamp=" + key.get("timestamp")
				+ "&remember_login=false&captchagid="+gid+"&captcha_text="+text;
		
		
		String packet = "POST /login/dologin/ HTTP/1.1\r\n";
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0\r\n";
		packet += "Accept: application/json, text/javascript, */*r\n";
		packet += "Accept: */*\r\n";
		packet += "Accept-Language: en-US\r\n";
		packet += "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n";
		packet += "Connection: close\r\n";
		if(machineAuth != null)
			packet += "Cookie: "+machineAuth+"\r\n";
		packet += "Content-Length: " + postData.length() + "\r\n\r\n";

		bw.write(packet);
		bw.write(postData);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		JSONObject ret = null;
		HashMap<String, String> cookies = new HashMap<String, String>();
		while ((line = br.readLine()) != null) {
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				if(cookieKey.contains("steamMachineAuth"))
					machineAuth = cookieKey + "=" + cookieValue + "; ";
				
				cookies.put(cookieKey, cookieValue);
			}
			if (line.equals("")) {
				ret = (JSONObject) parser.parse(br.readLine());
			}
		}

		bw.close();
		br.close();

		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; ";
		}
		
		c += "strInventoryLastContext=730_2; timezoneOffset=-9000,0; " + machineAuth;
		
		SteamAccount account = new SteamAccount(username, password, c, ret);
		account.setSteamMachineAuthCookie(machineAuth);
		
		return account;
	}
	
	public static SteamAccount login(String username, String password, String gid, String text, String emailAuth, String machineAuth) throws Exception {
		JSONObject key = getRSAKey(username);

		PubKey pKey = new PubKey(new BigInteger(key.get("publickey_mod").toString(), 16), new BigInteger(key.get("publickey_exp").toString(), 16));
		String encPass = new String(encrypt(password, pKey));
		
		encPass = URLEncoder.encode(encPass, "UTF-8");

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		String postData = "username=" + username + "&password=" + encPass + "&emailauth="+emailAuth+"&loginfriendlyname="+genRandomString()+"&emailsteamid=&rsatimestamp=" + key.get("timestamp")
				+ "&remember_login=false&captchagid="+gid+"&captcha_text="+text;
		
		
		String packet = "POST /login/dologin/ HTTP/1.1\r\n";
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0\r\n";
		packet += "Accept: application/json, text/javascript, */*r\n";
		packet += "Accept: */*\r\n";
		packet += "Accept-Language: en-US\r\n";
		packet += "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n";
		packet += "Connection: close\r\n";
		if(machineAuth != null)
			packet += "Cookie: "+machineAuth+"\r\n";
		packet += "Content-Length: " + postData.length() + "\r\n\r\n";

		bw.write(packet);
		bw.write(postData);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		JSONObject ret = null;
		HashMap<String, String> cookies = new HashMap<String, String>();

		while ((line = br.readLine()) != null) {
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				if(cookieKey.contains("steamMachineAuth"))
					machineAuth = cookieKey + "=" + cookieValue + "; ";
				cookies.put(cookieKey, cookieValue);
			}
			if (line.equals("")) {
				ret = (JSONObject) parser.parse(br.readLine());
			}
		}

		bw.close();
		br.close();

		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; ";
		}
		
		c += "strInventoryLastContext=730_2; timezoneOffset=-9000,0; " + machineAuth;
		
		SteamAccount account = new SteamAccount(username, password, c, ret);
		account.setSteamMachineAuthCookie(machineAuth);
		
		return account;
	}
	
	
	public static SteamAccount login(String username, String password, String emailAuth, String machineAuth) throws Exception {
		JSONObject key = getRSAKey(username);

		PubKey pKey = new PubKey(new BigInteger(key.get("publickey_mod").toString(), 16), new BigInteger(key.get("publickey_exp").toString(), 16));
		String encPass = new String(encrypt(password, pKey));
		
		encPass = URLEncoder.encode(encPass, "UTF-8");

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) factory.createSocket("steamcommunity.com", 443);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		String postData = "username=" + username + "&password=" + encPass + "&emailauth="+emailAuth+"&loginfriendlyname="+genRandomString()+"&emailsteamid=&rsatimestamp=" + key.get("timestamp")
				+ "&remember_login=false";
		
		
		String packet = "POST /login/dologin/ HTTP/1.1\r\n";
		packet += "Host: steamcommunity.com\r\n";
		packet += "User-Agent: Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20100101 Firefox/22.0\r\n";
		packet += "Accept: application/json, text/javascript, */*r\n";
		packet += "Accept: */*\r\n";
		packet += "Accept-Language: en-US\r\n";
		packet += "Content-Type: application/x-www-form-urlencoded; charset=UTF-8\r\n";
		packet += "Connection: close\r\n";
		if(machineAuth != null)
			packet += "Cookie: "+machineAuth+"\r\n";
		packet += "Content-Length: " + postData.length() + "\r\n\r\n";

		bw.write(packet);
		bw.write(postData);
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String line;
		JSONObject ret = null;
		HashMap<String, String> cookies = new HashMap<String, String>();

		while ((line = br.readLine()) != null) {
			//System.out.println(line);
			if(line.toLowerCase().contains("set-cookie: ")){
				String cookieKey = line.substring(12, line.indexOf("="));
				String cookieValue = line.substring(line.indexOf("=") + 1, line.indexOf(";"));
				if(cookieKey.contains("steamMachineAuth"))
					machineAuth = cookieKey + "=" + cookieValue + "; ";
				cookies.put(cookieKey, cookieValue);
			}
			if (line.equals("")) {
				ret = (JSONObject) parser.parse(br.readLine());
			}
		}

		bw.close();
		br.close();

		String c = "";
		for(String k : cookies.keySet()){
			c += k + "=" + cookies.get(k) + "; ";
		}
		
		c += "strInventoryLastContext=730_2; timezoneOffset=-9000,0; " + machineAuth;
		
		SteamAccount account = new SteamAccount(username, password, c, ret);
		account.setSteamMachineAuthCookie(machineAuth);
		
		return account;
	}

	public static String getHexString(byte[] b) throws Exception {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static PublicKey getPublicKey(byte[] modulus, byte[] exponent, String provider) {
		PublicKey publicKey = null;
		try {
			RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(1, modulus), new BigInteger(1, exponent));
			KeyFactory keyFactory = null;
			if (provider != null && !provider.isEmpty()) {
				keyFactory = KeyFactory.getInstance("RSA", provider);
			} else {
				keyFactory = KeyFactory.getInstance("RSA");
			}

			publicKey = keyFactory.generatePublic(pubKeySpec);
		} catch (Exception ex) {
			return null;
		}

		return publicKey;
	}
	
    public static byte[] encrypt(String data, PubKey pubkey) {
    	BigInteger data2 = pkcs1pad2(data,(pubkey.modulus.bitLength()+7)>>3);
        data2 = data2.modPow(pubkey.encryptionExponent, pubkey.modulus);
        data = data2.toString(16);
        return Base64.encode(Hex.decode(data));
    }

    public static BigInteger pkcs1pad2(String data, int keysize) {
        if(keysize < data.length() + 11)
            return null;
        byte[] buffer = new byte[keysize];
        int i = data.length() - 1;
        while(i >= 0 && keysize > 0)
            buffer[--keysize] = (byte)((int)data.charAt(i--));
        
        buffer[--keysize] = 0;
        
        while(keysize > 2)
           buffer[--keysize] = (byte) (Math.floor(Math.random()*254) + 1);
        buffer[--keysize] = 2;
        buffer[--keysize] = 0;
        return new BigInteger(buffer);
    }
    
}

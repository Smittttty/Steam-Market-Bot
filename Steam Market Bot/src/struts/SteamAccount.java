package struts;
import org.json.simple.JSONObject;


public class SteamAccount {
	private String cookie;
	private String username;
	private String password;
	private JSONObject response;
	private String sessionId;
	private String steamMachineAuthCookie;
	
	public SteamAccount(String username, String password, String cookie, JSONObject response){
		this.username = username;
		this.password = password;
		this.cookie = cookie;
		this.response = response;
	}
	
	public String getCookie() {
		return cookie;
	}
	
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public JSONObject getResponse() {
		return response;
	}

	public void setResponse(JSONObject response) {
		this.response = response;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSteamMachineAuthCookie() {
		return steamMachineAuthCookie;
	}

	public void setSteamMachineAuthCookie(String steamMachineAuthCookie) {
		this.steamMachineAuthCookie = steamMachineAuthCookie;
	}
	

}

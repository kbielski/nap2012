package com.ctp.android.ppm.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton used to store the internal model
 * when copying a day or a week.
 * 
 * @author kbiels
 *
 */
public class SharedModel {

	/** The instance of the Singleton **/
	private static SharedModel instance;
	
	/** Copy Week functionality available in the Options Menu **/
	private WeekProgressModel copiedWeek;
	
	/** Copy Day functionality available in the Context Menu **/
	private DayProgressModel copiedDay;
	
	/** Token used to authenticate the requests to the server **/
	private JsonTokenModel token;
	
	/** Flag indicating if the application is using the CTP endpoint
	 *  for the REST web services. Can be changed in the Options Menu in Login view. **/
	private boolean connectedToCTP;
	
	/** Cache the data from web services, key is the week number 
	 *  and value is the week model **/
	private Map<Integer, WeekProgressModel> cacheMap;
	
	private SharedModel() {
		cacheMap = new HashMap<Integer, WeekProgressModel>();
		connectedToCTP = true;
	}
	
	public static SharedModel getInstance() {
		if(instance == null) {
			instance = new SharedModel();
		}
		return instance;
	}

	public WeekProgressModel getCopiedWeek() {
		return copiedWeek;
	}

	public void setCopiedWeek(WeekProgressModel copiedWeek) {
		this.copiedWeek = copiedWeek;
	}

	public DayProgressModel getCopiedDay() {
		return copiedDay;
	}

	public void setCopiedDay(DayProgressModel copiedDay) {
		this.copiedDay = copiedDay;
	}

	public JsonTokenModel getToken() {
		return token;
	}

	public void setToken(JsonTokenModel token) {
		this.token = token;
	}
	
	public boolean isConnectedToCTP() {
		return connectedToCTP;
	}

	public void setConnectedToCTP(boolean connectedToCTP) {
		this.connectedToCTP = connectedToCTP;
	}
	
	
	public Map<Integer, WeekProgressModel> getCacheMap() {
		return cacheMap;
	}

	public void setCacheMap(Map<Integer, WeekProgressModel> cacheMap) {
		this.cacheMap = cacheMap;
	}

	/**
	 * Clears all the data stored in the singleton.
	 */
	public void clearModel() {
		copiedDay = null;
		copiedWeek = null;
		cacheMap.clear();
	}

}

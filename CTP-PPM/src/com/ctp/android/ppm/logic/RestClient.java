package com.ctp.android.ppm.logic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.content.Context;

import com.ctp.android.ppm.R;
import com.ctp.android.ppm.model.JsonAuthenticationModel;
import com.ctp.android.ppm.model.JsonDateRangeModel;
import com.ctp.android.ppm.model.JsonHoursModel;
import com.ctp.android.ppm.model.JsonProject;
import com.ctp.android.ppm.model.JsonTokenModel;
import com.ctp.android.ppm.model.SharedModel;

public class RestClient {

	private Context context;

	public RestClient(Context context) {
		this.context = context;
	}

	/**
	 * Returning the information about the hours logged for projects in a given
	 * date range.
	 * 
	 * @param startDate
	 * @param endDate
	 * @return List of HoursModel objects containing information about hours
	 *         logged for projects.
	 */
	public List<JsonHoursModel> hoursRange(Date startDate, Date endDate) {
		DateFormat dfJson = new SimpleDateFormat("yyyyMMdd");
		JsonDateRangeModel dateRangeModel = new JsonDateRangeModel();
		dateRangeModel.setStart(dfJson.format(startDate));
		dateRangeModel.setEnd(dfJson.format(endDate));

		//GOOD VERSION
		String url = context.getString(R.string.URL_BASE_PPM)
				+ context.getString(R.string.URL_HOURS_RANGE);

		//FIXME: call OpenShift instead of CTP endpoint
		if(SharedModel.getInstance().isConnectedToCTP() == false) {
			// url = "http://10.0.2.2:8580/rs-helloworld/rest/HoursRange/query?start="
			url = "http://rest-kbielski.rhcloud.com/rs-helloworld/rest/HoursRange/query?start="
					+ dfJson.format(startDate) + "&" + "end=" + dfJson.format(endDate);
		}

		// Populate the headers in an HttpEntity object to use for the request
		HttpEntity<JsonDateRangeModel> requestEntity = new HttpEntity<JsonDateRangeModel>(
				dateRangeModel, getAuthenticatedHttpHeader());

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();

		// Perform the HTTP GET request
		ResponseEntity<JsonHoursModel[]> responseEntity = restTemplate
				.exchange(url, HttpMethod.GET, requestEntity,
						JsonHoursModel[].class);

		// convert the array to a list and return it
		List<JsonHoursModel> hoursList = Arrays
				.asList(responseEntity.getBody());
		return hoursList;
	}

	/**
	 * Authentication for the PPM webservices. Giving a username and password
	 * you can get the token to authenticate the other REST requests.
	 * 
	 * @param username
	 * @param password
	 * @return TokenModel containing the token to authenticate the other REST
	 *         requests
	 */
	public JsonTokenModel login(String username, String password) {
		JsonAuthenticationModel authenticationModel = new JsonAuthenticationModel();
		authenticationModel.setUsername(username);
		authenticationModel.setPassword(password);

		String url = context.getString(R.string.URL_BASE_PPM)
				+ context.getString(R.string.URL_LOGIN);
		RestTemplate restTemplate = new RestTemplate();
		JsonTokenModel token = restTemplate.postForObject(url,
				authenticationModel, JsonTokenModel.class);
		return token;
	}

	/**
	 * Returns the project list assigned to this user.
	 * 
	 * @return list of the project assigned to this user.
	 */
	public List<JsonProject> myProjects() {

		String url = context.getString(R.string.URL_BASE_PPM)
				+ context.getString(R.string.URL_MY_PROJECTS);

		// Populate the headers in an HttpEntity object to use for the request
		HttpEntity<?> requestEntity = new HttpEntity<Object>(getAuthenticatedHttpHeader());

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();

		// Perform the HTTP GET request
		ResponseEntity<JsonProject[]> responseEntity = restTemplate.exchange(
				url, HttpMethod.GET, requestEntity, JsonProject[].class);

		// convert the array to a list and return it
		List<JsonProject> projectList = Arrays.asList(responseEntity.getBody());

		return projectList;
	}

	/**
	 * Creates, updates or deletes the hours for a given project and date.
	 * 
	 * @param hour Json object to modify
	 * @param httpMethod Type of HttpMethod: PUT, DELETE or POST
	 * @return Returns the status of the operation
	 */
	public String createHour(JsonHoursModel hour,
			HttpMethod httpMethod) {

		//GOOD VERSION
		String url = context.getString(R.string.URL_BASE_PPM)
				+ context.getString(R.string.URL_HOURS);

		//FIXME:
		if(SharedModel.getInstance().isConnectedToCTP() == false) {
			url = "http://rest-kbielski.rhcloud.com/rs-helloworld/rest/Hours";
			//url = "http://10.0.2.2:8580/rs-helloworld/rest/Hours";
			
			//FIXME: in local server using delete with query string
			if(httpMethod == HttpMethod.DELETE) {
				url += "/" + hour.getIdhours();
			}
		}

		// Populate the headers in an HttpEntity object to use for the request
		HttpEntity<JsonHoursModel> requestEntity = new HttpEntity<JsonHoursModel>(
				hour, getAuthenticatedHttpHeader());

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();

		// Perform the HTTP GET request
		ResponseEntity<String> responseEntity = restTemplate
				.exchange(url, httpMethod, requestEntity,
						String.class);
		return responseEntity.getBody();
	}
	
	/**
	 * Creates, updates or deletes the hours for a given project and date.
	 * 
	 * @param hoursList Array of objects to modify
	 * @param httpMethod Type of HttpMethod: PUT, DELETE or POST
	 * @return Returns the status of the operation
	 */
	public String createHours(JsonHoursModel[] hours,
			HttpMethod httpMethod) {

		String url = context.getString(R.string.URL_BASE_PPM)
				+ context.getString(R.string.URL_HOURS);

		// Populate the headers in an HttpEntity object to use for the request
		HttpEntity<JsonHoursModel[]> requestEntity = new HttpEntity<JsonHoursModel[]>(
				hours, getAuthenticatedHttpHeader());

		// Create a new RestTemplate instance
		RestTemplate restTemplate = new RestTemplate();

		// Perform the HTTP GET request
		ResponseEntity<String> responseEntity = restTemplate
				.exchange(url, httpMethod, requestEntity,
						String.class);
		return responseEntity.getBody();
	}

	/**
	 * Creates a HttpHeader with the authenticated token.
	 * 
	 * @return HttpHeaders with the token to authenticate.
	 */
	private HttpHeaders getAuthenticatedHttpHeader() {
		// Set the Accept header for "application/json"
		HttpHeaders requestHeaders = new HttpHeaders();
		List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
		acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
		acceptableMediaTypes.add(MediaType.TEXT_PLAIN);
		requestHeaders.setAccept(acceptableMediaTypes);
		
		if(context != null && SharedModel.getInstance().getToken() != null) {
			requestHeaders.add(context.getString(R.string.TOKEN_NAME),
					SharedModel.getInstance().getToken().getTokenValue());
		}
		
//		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
//		requestHeaders.add("Content-Type", "application/json");
		
		return requestHeaders;
	}
}

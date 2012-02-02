package com.ctp.android.ppm.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class JsonTokenModel {

	@JsonProperty("TokenValue")
	private String tokenValue;

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}
}

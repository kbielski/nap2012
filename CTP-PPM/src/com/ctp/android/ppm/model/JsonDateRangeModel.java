package com.ctp.android.ppm.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class JsonDateRangeModel {

	@JsonProperty("Start")
	private String start;
	
	@JsonProperty("End")
	private String end;

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
}

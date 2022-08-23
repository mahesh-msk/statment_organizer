package com.ancit.stmt.model;
public enum GroupTypeEnum {
	INCOME("I"), EXPENSE("E"), BANK("B");
 
	private String statusCode;
 
	private GroupTypeEnum(String s) {
		statusCode = s;
	}
 
	public String getStatusCode() {
		return statusCode;
	}
 
}
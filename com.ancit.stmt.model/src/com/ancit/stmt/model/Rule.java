package com.ancit.stmt.model;

import java.io.Serializable;

public class Rule implements Serializable {

	private String name;
	private String categoryName;
	private String description;
	private String matchString;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getMatchString() {
		return matchString;
	}
	public void setMatchString(String matchString) {
		this.matchString = matchString;
	}
}

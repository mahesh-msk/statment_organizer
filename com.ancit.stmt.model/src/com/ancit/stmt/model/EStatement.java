package com.ancit.stmt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EStatement implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2977950648564023223L;
	String companyName;
	List<Entry> entries;
	List<Group> groups;
	List<Group> bankGroups;
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	public String getCompanyName() {
		return companyName;
	}
	
	public List<Group> getBankGroups() {
		if(bankGroups == null) {
			bankGroups = new ArrayList<Group>();
		}
		return bankGroups;
	}
	
	public List<Group> getGroup() {
		if(groups == null) {
			groups = new ArrayList<Group>();
		}
		return groups;
	}
	
	public List<Entry> getEntries() {
		if(entries == null) {
			entries = new ArrayList<Entry>();
		}
		return entries;
	}

}

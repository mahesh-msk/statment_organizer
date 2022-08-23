package com.ancit.stmt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5490901124739463248L;
	private String name;
	List<Entry> entries;
	boolean isBank;
	boolean isTally;
	private GroupTypeEnum groupType;
	
	public boolean isTally() {
		return isTally;
	}

	public void setTally(boolean isTaxable) {
		this.isTally = isTaxable;
	}
	
	public void setBank(boolean isBank) {
		this.isBank = isBank;
	}
	
	public boolean isBank() {
		return isBank;
	}
	
	
	public List<Entry> getEntries() {
		if(entries == null) {
			entries = new ArrayList<Entry>();
		}
		return entries;
	}
	
	public boolean addEntry(Entry entry) {
		for (Entry groupEntry : entries) {
			if(groupEntry.getSno()==entry.getSno()) {
				return false;
			}
		}
		getEntries().add(entry);
		return true;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getName();
	}

	public GroupTypeEnum getGroupType() {
		return groupType;
	}

	public void setGroupType(GroupTypeEnum groupType) {
		this.groupType = groupType;
	}

}

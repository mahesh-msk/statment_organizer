package com.ancit.stmt.model;

import java.io.Serializable;
import java.util.Date;

public class Entry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8079827216574798479L;
	int sno;
	Date date;
	private Date bankDate;
	String chequeNo;
	String description;
	double amount;
	char type;
	
	Group group;
	
	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	
	public char getType() {
		return type;
	}
	
	public void setType(char type) {
		this.type = type;
	}
	
	public int getSno() {
		return sno;
	}
	
	public void setSno(int sno) {
		this.sno = sno;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getChequeNo() {
		return chequeNo;
	}
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getDate() +":"+ this.getType();
	}

	public Date getBankDate() {
		return bankDate;
	}

	public void setBankDate(Date bankDate) {
		this.bankDate = bankDate;
	}
	
}

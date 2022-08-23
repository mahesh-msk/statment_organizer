package com.ancit.stmt.model.queries;

import java.util.ArrayList;
import java.util.List;

public class RetrieveCompanyList {

	String query = "<ENVELOPE>" + "<HEADER>"
			+ "<TALLYREQUEST>Export Data</TALLYREQUEST>" + "</HEADER>"
			+ "<BODY>" + "<EXPORTDATA>" + "<REQUESTDESC>"
			+ "<REPORTNAME>List of Companies</REPORTNAME>" + "</REQUESTDESC>"
			+ "</EXPORTDATA>" + "</BODY>" + "</ENVELOPE>";

	public List<String> retrieveCompanyList() {
		List<String> companyNames = new ArrayList<String>();

		String result = new PerformQuery().performQuery(query);

		result = result.replace("<ENVELOPE> <COMPANYNAME.LIST>","").replace("</COMPANYNAME.LIST></ENVELOPE>", "").trim();
		String[] splitResult = result.split("  ");
		for (String resultSet : splitResult) {
			if (resultSet.contains("<COMPANYNAME>")) {
				companyNames.add(resultSet.replace("<COMPANYNAME>", "")
						.replace("</COMPANYNAME>", ""));
			}
		}

		return companyNames;
	}

}

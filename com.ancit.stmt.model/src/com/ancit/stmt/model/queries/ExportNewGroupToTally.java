package com.ancit.stmt.model.queries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.GroupTypeEnum;
import com.ancit.stmt.model.StatementManager;

public class ExportNewGroupToTally {

	String startQuery = "<ENVELOPE>" + "<HEADER>"
			+ "<TALLYREQUEST>Import Data</TALLYREQUEST>"
			+ "</HEADER>"
			+ "<BODY>"
			+ "<IMPORTDATA>"
			+ "<REQUESTDESC>"
			+ "<REPORTNAME>Vouchers</REPORTNAME>"
			// + "<STATICVARIABLES>"
			// +
			// "<SVCURRENTCOMPANY>"+StatementManager.getInstance().getStatement().getCompanyName()+"</SVCURRENTCOMPANY>"
			// + "</STATICVARIABLES>"
			+ "</REQUESTDESC>" + "<REQUESTDATA>"
			+ "<TALLYMESSAGE xmlns:UDF=\"TallyUDF\">";

	String endQuery = "</TALLYMESSAGE>" + "</REQUESTDATA>" + "</IMPORTDATA>"
			+ "</BODY>" + "</ENVELOPE>";

	public void exportGroupToTally(Group group) {
		// TODO Auto-generated method stub

		String ledgerStartString = "";
		String ledgerContentString = "";
		String ledgerEndString = "";

		ledgerStartString = "<LEDGER NAME=\""+ group.getName()
				+ "\" ACTION="+"\"Create\""+">" + "<NAME.LIST>" + "<NAME>"
				+ group.getName() + "</NAME>" + "</NAME.LIST>";

		if (group.getGroupType() == GroupTypeEnum.INCOME) {
			ledgerContentString = "<PARENT>" + "Direct Incomes" + "</PARENT>"
					+ "<ISBILLWISEON>No</ISBILLWISEON>"
					+ "<AFFECTSSTOCK>No</AFFECTSSTOCK>"
					+ "<OPENINGBALANCE>0</OPENINGBALANCE>"
					+ "<USEFORVAT>No </USEFORVAT>" + "<TAXCLASSIFICATIONNAME/>"
					+ "<TAXTYPE/>" + "<RATEOFTAXCALCULATION/>";
		} else if (group.getGroupType() == GroupTypeEnum.EXPENSE) {
			ledgerContentString = "<PARENT>" + "Direct Expenses" + "</PARENT>"
					+ "<ISBILLWISEON>No</ISBILLWISEON>"
					+ "<AFFECTSSTOCK>No</AFFECTSSTOCK>"
					+ "<OPENINGBALANCE>0</OPENINGBALANCE>"
					+ "<USEFORVAT>No </USEFORVAT>" + "<TAXCLASSIFICATIONNAME/>"
					+ "<TAXTYPE/>" + "<RATEOFTAXCALCULATION/>";
		} else {
			ledgerContentString = "<PARENT>" + "Bank Accounts" + "</PARENT>"
					+ "<ISBILLWISEON>No</ISBILLWISEON>"
					+ "<AFFECTSSTOCK>No</AFFECTSSTOCK>"
					+ "<OPENINGBALANCE>0</OPENINGBALANCE>"
					+ "<USEFORVAT>No </USEFORVAT>" + "<TAXCLASSIFICATIONNAME/>"
					+ "<TAXTYPE/>" + "<RATEOFTAXCALCULATION/>";
		}
		ledgerEndString = "</LEDGER>";
		String ledgerString = ledgerStartString + ledgerContentString
				+ ledgerEndString;

		String finalQuery = startQuery + ledgerString + endQuery;

		StatementManager.getInstance().performQuery(finalQuery);
		File file = new File("c:\\temp\\tallyexport1.xml");

		// if file doesnt exists, then create it
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(finalQuery.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

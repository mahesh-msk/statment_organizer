package com.ancit.stmt.model.queries;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.StatementManager;

public class ExportGroupToTally {
	
	String startQuery = "<ENVELOPE>"+
	"<HEADER>"+
	"<TALLYREQUEST>Import Data</TALLYREQUEST>"+
	"</HEADER>"+
	"<BODY>"+
	"<IMPORTDATA>"+
	"<REQUESTDESC>"+
	    "<REPORTNAME>Vouchers</REPORTNAME>"
//	    + "<STATICVARIABLES>"
//	    + "<SVCURRENTCOMPANY>"+StatementManager.getInstance().getStatement().getCompanyName()+"</SVCURRENTCOMPANY>"
//	    + "</STATICVARIABLES>"
	   +"</REQUESTDESC>"+
	"<REQUESTDATA>"+
	   "<TALLYMESSAGE xmlns:UDF=\"TallyUDF\">";
	
	String endQuery = "</TALLYMESSAGE>"+
	"</REQUESTDATA>"+
	"</IMPORTDATA>"+
	"</BODY>"+
	"</ENVELOPE>";
	
	
	
	public void exportGroupToTally(Group bankGroup, Group group) {
		
		List<Entry> entries = group.getEntries();
		String bankLedgerName = bankGroup.getName();
		
		StringBuffer allEntries = new StringBuffer();
		
		for (Entry entry : entries) {
			String vchType = "Receipt";
			String debitString = "";
			String creditString = "";
			DecimalFormat df = new DecimalFormat("#.##");
			String amount = df.format(entry.getAmount());
			if(entry.getType()=='D') {
				vchType = "Payment";
				if(group.isBank()) {
					vchType = "Contra";
				}
				debitString = "<LEDGERNAME>"+bankLedgerName+"</LEDGERNAME>"+
						"<ISDEEMEDPOSITIVE>"+"No"+"</ISDEEMEDPOSITIVE>"+
						"<AMOUNT>"+amount+"</AMOUNT>";
				
				creditString = "<LEDGERNAME>"+group.getName()+"</LEDGERNAME>"+
						"<ISDEEMEDPOSITIVE>"+"Yes"+"</ISDEEMEDPOSITIVE>"+
						"<AMOUNT>-"+amount+"</AMOUNT>";
			} else {
				if(group.isBank()) {
					vchType = "Contra";
				}
				debitString = "<LEDGERNAME>"+group.getName()+"</LEDGERNAME>"+
						"<ISDEEMEDPOSITIVE>"+"No"+"</ISDEEMEDPOSITIVE>"+
						"<AMOUNT>"+entry.getAmount()+"</AMOUNT>";
				
				creditString = "<LEDGERNAME>"+bankLedgerName+"</LEDGERNAME>"+
						"<ISDEEMEDPOSITIVE>"+"Yes"+"</ISDEEMEDPOSITIVE>"+
						"<AMOUNT>-"+entry.getAmount()+"</AMOUNT>";
			}
			
			String date = new SimpleDateFormat("yyyyMMdd").format(entry.getDate());
//			String date = "20130401";
			
			String description = entry.getDescription().replace("&", "");
			String voucherQuery = "<VOUCHER VCHTYPE=\""+vchType+"\" ACTION=\"Create\">"+
					"<VOUCHERTYPENAME>"+vchType+"</VOUCHERTYPENAME>"+
					"<DATE>"+date+"</DATE>"+
					"<EFFECTIVEDATE>"+date+"</EFFECTIVEDATE>"+
					"<PARTYLEDGERNAME>"+group.getName()+"</PARTYLEDGERNAME>"+
					"<NARRATION>"+description+"</NARRATION>"+
					"<ALLLEDGERENTRIES.LIST>" +
					debitString +
					"</ALLLEDGERENTRIES.LIST>"+
					"<ALLLEDGERENTRIES.LIST>" +
					creditString +
					"</ALLLEDGERENTRIES.LIST>"+
					"</VOUCHER>";
			
			String finalQuery = startQuery + voucherQuery + endQuery;
			allEntries.append(finalQuery);
			allEntries.append("\n");
			
			StatementManager.getInstance().performQuery(finalQuery);
		}
		
		File file = new File("c:\\temp\\tallyexport.xml");
		 
		// if file doesnt exists, then create it
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(allEntries.toString());
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}

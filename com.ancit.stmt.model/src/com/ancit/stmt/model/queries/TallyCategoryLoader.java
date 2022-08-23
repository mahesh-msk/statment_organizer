package com.ancit.stmt.model.queries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.ancit.stmt.model.Group;
import com.ancit.stmt.model.GroupTypeEnum;
import com.ancit.stmt.model.StatementManager;

public class TallyCategoryLoader {

	

	String Url = StatementManager.getInstance().getTallyServerUrl();

	private Group group;

	public void loadCategoryFile(String companyName) {

		String query = "<ENVELOPE>" + "<HEADER>"
				+ "<TALLYREQUEST>Export Data</TALLYREQUEST>" + "</HEADER>"
				+ "<BODY>" + "<EXPORTDATA>" + "<REQUESTDESC>"
				+ "<REPORTNAME>List of Accounts</REPORTNAME>"
				+ "<STATICVARIABLES>"
			    + "<SVCURRENTCOMPANY>"+companyName+"</SVCURRENTCOMPANY>"
			    + "<SVEXPORTFORMAT>$$SysName:XML</SVEXPORTFORMAT>"
				+ "<ACCOUNTTYPE>Ledgers</ACCOUNTTYPE>"
			    + "</STATICVARIABLES>"
				+"</REQUESTDESC>"
				+ "</EXPORTDATA>" + "</BODY>" + "</ENVELOPE>";
		
		String SOAPAction = "";

		// Create the connection where we're going to send the file.
		try {
			URL url = new URL(Url);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			byte[] b = query.getBytes();

			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length",
					String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type",
					"text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			// Everything's set up; send the XML that was read in to b.
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(
					httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			String inputLine;

			List<Group> groups = StatementManager.getInstance().getStatement().getGroup();
			
			while ((inputLine = in.readLine()) != null) {
				if(inputLine.contains("LEDGER NAME=\"")) {
					String ledgerName = inputLine.replace("<LEDGER NAME=\"", "").replace("\" RESERVEDNAME=\"\">", "");
					if(ledgerName.contains("Profit &amp; Loss A/c")) {
						continue;
					}
					group = new Group();
					group.setName(ledgerName.trim());
					group.setTally(true);
					groups.add(group);
					System.out.println(ledgerName);
				} else if (inputLine.contains("<PARENT>")) {
					if(group != null) {
						if(inputLine.contains("Bank Accounts") || inputLine.contains("Cash-in-hand")) {
							group.setBank(true);
							group.setGroupType(GroupTypeEnum.BANK);
							StatementManager.getInstance().getStatement().getBankGroups().add(group);
						} else {
							if(inputLine.contains("Expenses")) {
								group.setGroupType(GroupTypeEnum.EXPENSE);
							} else {
								group.setGroupType(GroupTypeEnum.INCOME);
							}
							group.setBank(false);
						}
					}
				}
			}

			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void loadCategoryFromFile(String filePath) {

		

		// Create the connection where we're going to send the file.
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));

			String inputLine;

			List<Group> groups = StatementManager.getInstance().getStatement().getGroup();
			
			while ((inputLine = in.readLine()) != null) {
				
				System.out.println(inputLine);
				if(inputLine.contains("LEDGER NAME=\"")) {
					String ledgerName = inputLine.replace("<LEDGER NAME=\"", "").replace("\" RESERVEDNAME=\"\">", "");
					if(ledgerName.contains("Profit &amp; Loss A/c")) {
						continue;
					}
					group = new Group();
					group.setName(ledgerName.trim());
					groups.add(group);
					System.out.println(ledgerName);
				} else if (inputLine.contains("<PARENT>")) {
					if(group != null) {
						if(inputLine.contains("Bank Accounts") || inputLine.contains("Cash-in-hand")) {
							group.setBank(true);
							StatementManager.getInstance().getStatement().getBankGroups().add(group);
						} else {
							group.setBank(false);
						}
					}
				}
			}

			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

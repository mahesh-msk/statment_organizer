package com.ancit.stmt.loader.icici;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class ReadExcel {

  private String inputFile;
  private String content;

  public void setInputFile(String inputFile) {
    this.inputFile = inputFile;
  }

  public void read() throws IOException, ParseException  {
	  
	  FileInputStream fstream = new FileInputStream(inputFile);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(in));
		String strLine;
		int i = 0;
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console
			String[] record = strLine.replace("  ","|").trim().split("\\|");
			ArrayList<String> recordResult = new ArrayList<String>();
			for (String string : record) {
				if(string.length() >= 1) {
					recordResult.add(string);
				}
			}
			
			String[] recordSet = recordResult.toArray(new String[recordResult.size()]);
			
			if(recordSet.length > 8) {
				if (i == 0) {
					i++;
					continue;
				}
			if(recordSet.length == 10) {
				List<Entry> entries = StatementManager.getInstance()
						.getStatement().getEntries();

				Entry entry = new Entry();
				Date date = new SimpleDateFormat("dd/MM/yyyy")
						.parse(recordSet[2]);
				entry.setSno(entries.size() + 1);
				entry.setAmount(Double.parseDouble(recordSet[7].replace(
						",", "")));
				entry.setChequeNo(recordSet[4]);
				entry.setDate(date);
				if (recordSet[6].trim().equalsIgnoreCase("CR")) {
					entry.setType('C');
				} else {
					entry.setType('D');
				}
				entry.setDescription("Transaction with " + recordSet[1]
						+ " was performed on " + recordSet[2] + "for "
						+ recordSet[5]);

				entries.add(entry);

			} else if(recordSet.length==9) {
				List<Entry> entries = StatementManager.getInstance()
						.getStatement().getEntries();

				Entry entry = new Entry();
				Date date = new SimpleDateFormat("dd/MM/yyyy")
						.parse(recordSet[2]);
				entry.setSno(entries.size() + 1);
				entry.setAmount(Double.parseDouble(recordSet[6].replace(
						",", "")));
				
				entry.setDate(date);
				if (recordSet[5].trim().equalsIgnoreCase("CR")) {
					entry.setType('C');
				} else {
					entry.setType('D');
				}
				entry.setDescription("Transaction with " + recordSet[1]
						+ " was performed on " + recordSet[2] + "for "
						+ recordSet[4]);

				entries.add(entry);
				
			}
		}					
		}
		in.close();
		
		for (Entry entry2 : StatementManager.getInstance().getStatement().getEntries()) {
			System.out.println(entry2.getDescription());
		}
    
  }

  public static void main(String[] args) throws IOException, ParseException {
    ReadExcel test = new ReadExcel();
//    test.setInputFile("C:\\Users\\Sathieswar\\Desktop\\From Malai\\Bank Statement\\ICICIBankStatement.txt");
    test.setInputFile("C:\\Users\\malaireva\\Google Drive\\Personal\\Accounts\\Accounts 2015-2016\\Statements\\Dec-FebMid2016.xls");
    test.read();
  }

} 
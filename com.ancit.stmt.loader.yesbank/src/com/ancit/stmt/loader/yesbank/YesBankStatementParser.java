package com.ancit.stmt.loader.yesbank;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class YesBankStatementParser implements IStatementParser {

	public YesBankStatementParser() {
		// TODO Auto-generated constructor stub
	}

	public String getAccountNumber(String line){
		String  accountNumber = null;
		if (line.contains("Statement For")) {
			accountNumber = line.substring(line.indexOf(':')).trim();
		}
		return accountNumber;
	}
	
	@Override
	public String loadStatement(String filePath) {

		try {
			PdfReader reader = new PdfReader(filePath);
			int n = reader.getNumberOfPages();
			// string array to store pdf content by lines
			String[] lineWise = null;
			// string array to store transactions
			String[] tnxData = null;

		//	String accountNumber = null;
			int startOfTxns = Integer.MIN_VALUE, endOfTxns = Integer.MIN_VALUE;

			boolean txnFound = false;
			/* reading pdf file */
			for (int i = 1; i <= n; i++) {
				String str = PdfTextExtractor.getTextFromPage(reader, i);
				lineWise = str.split("\n");
			}

			for (int idx = 0; idx < lineWise.length; idx++) {
				String s = lineWise[idx];
				// code to find account number				

				// code to find starting of transaction details
				String[] txnDates = s.split(" ");
				for (String str : txnDates) {
					if (!txnFound && str.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
						startOfTxns = idx;
						txnFound = true;
						break;
					}
				}

				// code to find end of txn details
				if (s.contains("ViewAll Download PDF")) {
					endOfTxns = idx - 1;
					// System.out.println(endOfTxns);
				}

			}
			tnxData = new String[endOfTxns - startOfTxns + 1];

			int temp = 0;
			for (int idx = startOfTxns; idx <= endOfTxns; idx++) {
				tnxData[temp] = lineWise[idx];
				temp++;
			}

			String[] txnDate = new String[(endOfTxns - startOfTxns + 1) / 3];
			String[] valueDate = new String[(endOfTxns - startOfTxns + 1) / 3];
			String[] description = new String[(endOfTxns - startOfTxns + 1) / 3];
			String[] debtAmt = new String[(endOfTxns - startOfTxns + 1) / 3];
			String[] creditAmt = new String[(endOfTxns - startOfTxns + 1) / 3];
			String[] runningBalance = new String[(endOfTxns - startOfTxns + 1) / 3];

			for (int idxNew = 0, idx = 0; idxNew < tnxData.length; idxNew++, idx++) {
				String[] row1 = tnxData[idxNew].split(" ");
				String[] row2 = tnxData[idxNew + 1].split(" ");
				String[] row3 = tnxData[idxNew + 2].split(" ");

				txnDate[idx] = row1[0] + " " + row3[0];
				valueDate[idx] = row2[0];
				debtAmt[idx] = row2[1];
				creditAmt[idx] = row2[2];
				runningBalance[idx] = row2[3];
				try {
					description[idx] = row1[1] + " " + row1[2] + " " + row3[1] + " " + row3[2];
				} catch (ArrayIndexOutOfBoundsException e) {
					description[idx] = row1[1] + " " + row1[2] + " " + row3[1];
				}
				idxNew = idxNew + 2;
			}

			System.out.println("Tnx Date\t\tValue Date\tDescription\t\t\tDebt Amount\tCredit Amount\tRunning Balance");
			List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
			for (int idx = 0; idx < txnDate.length; idx++) {
				System.out.println(txnDate[idx] + "\t" + valueDate[idx] + "\t" + description[idx] + "\t" + debtAmt[idx]
						+ "\t\t" + creditAmt[idx] + "\t\t" + runningBalance[idx]);
				
				
			//	double value = Double.parseDouble(debtAmt[idx]);
				
				Entry entry=new Entry();
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date date;
				try {
					date = formatter.parse(txnDate[idx]);
					entry.setDate(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				entry.setDescription(description[idx]);
				
				double valueD = Double.parseDouble(debtAmt[idx].replace(",", ""));
				
				if( valueD >0){
				entry.setType('D');
				entry.setAmount(valueD);
				}
				
				double valueC = Double.parseDouble(creditAmt[idx].replace(",", ""));
				if(valueC>0){
					entry.setType('C');
					entry.setAmount(valueC);
				}
				entry.setSno(idx+1);
				entries.add(entry);
				
				
			}
			
			
			
			PrintWriter pr = new PrintWriter("G:\\file.txt");
			pr.println("Tnx Date\t\t\tValue Date\tDescription\t\t\t\t\tDebt Amount\t\tCredit Amount\tRunning Balance");
	        for (int idx = 0; idx < txnDate.length ; idx++){
	            pr.println(txnDate[idx] + "\t" + valueDate[idx] + "\t" + description[idx] + "\t\t" + debtAmt[idx]
						+ "\t\t" + creditAmt[idx] + "\t\t" + runningBalance[idx]);
	        }
	        pr.close();
	        
			

		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}

		return null;
	}

	
}

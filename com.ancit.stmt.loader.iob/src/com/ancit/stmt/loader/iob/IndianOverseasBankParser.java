package com.ancit.stmt.loader.iob;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class IndianOverseasBankParser implements IStatementParser {

	public IndianOverseasBankParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String loadStatement(String filePath) {
		
		try {
			PdfReader reader = new PdfReader(filePath);
			int n = reader.getNumberOfPages();
			ArrayList<String> statementEntries = new ArrayList<String>();
			for (int i = 1; i <= n; i++) {
				String str = PdfTextExtractor.getTextFromPage(reader, i);
				String[] lineWise = str.split("\n");
				
				List<String> entries = Arrays.asList(lineWise);
				
				boolean omit = true;
				for (String string : lineWise) {

					if (i == 1 && !string.startsWith("DATE") && omit) {
						continue;
					}
					else{
						
						if (string.contains("DATE")) {
							omit = false;
							System.out.println(string +"\n");
						}

						Pattern pattern = Pattern
								.compile("^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\-\\d{4}$");
						String matcherString = string.split("  ")[0];
						Matcher matcher = pattern.matcher(matcherString);
						if (matcher.find()) {
							
							statementEntries.add(string.replaceAll("( )+", "\t"));
							
						}
						
						
						
					}
				}
			}
			reader.close();		
			for (String string : statementEntries) {
				System.out.println(string);
			}
			
			int count = 0;
			   double previousBalance = 0;
			   List<Entry> entries = StatementManager.getInstance()
					      .getStatement().getEntries();
			   for (String statementEntry : statementEntries) {
				double currentBalance;
			    String[] statementValues = statementEntry.split("\\t");			    
			    String amountInString = statementValues[statementValues.length-2].replace(",", "");
			    if(count == 0) {
			    	
			    	 Entry entry = new Entry();
				     Date date;
					try {
						date = new SimpleDateFormat("dd-MMM-yyyy")
						   .parse(statementValues[0]);
						entry.setSno(entries.size() + 1);
					     entry.setDate(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				     
				     String description = "";
				     for (int i = 1; i < statementValues.length-2; i++) {
				      description += statementValues[i];        
				     }
				     entry.setDescription(description);
				     
				     Double amount = Double.parseDouble(amountInString);
				     entry.setType('D');

				     entry.setAmount(Math.abs(amount));
				     entries.add(entry);
				     
			     previousBalance = Double.parseDouble(statementValues[statementValues.length-1].replace(",", ""));
			     
			    } else {
			     Entry entry = new Entry();
			     Date date;
				try {
					date = new SimpleDateFormat("dd-MMM-yyyy")
					   .parse(statementValues[0]);
					entry.setSno(entries.size() + 1);
				     entry.setDate(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     
			     String description = "";
			     for (int i = 1; i < statementValues.length-2; i++) {
			      description += statementValues[i];        
			     }
			     entry.setDescription(description);
			     
			     
			     currentBalance = Double.parseDouble(statementValues[statementValues.length-1].replace(",", ""));
			     Double amount = Double.parseDouble(amountInString);
			     if(currentBalance > previousBalance) {
			      entry.setType('C');
			     } else {
			      entry.setType('D');
			     }
			     entry.setAmount(Math.abs(amount));
			     previousBalance = currentBalance;
			     entries.add(entry);
			    }
			    count++;
			   }
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	
		
		return "";
	}

}

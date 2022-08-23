package com.ancit.stmt.loader.kvb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class KVBStatementParser implements IStatementParser {
	Scanner scanner; 
	public KVBStatementParser() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public String loadStatement(String filePath) {	
		try {
			scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int i = 0;
		
		List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
		
		while (scanner.hasNext()) {
			String readLine = scanner.nextLine();

			
			
			i++;
			if (i > 12) {
				try {
					Entry record = new Entry();
					int start = readLine.indexOf("\"");

					if (start != -1) {
						String substring1 = readLine.substring(start + 1);
						int last = substring1.indexOf("\"");
						String value = substring1.substring(0, last);
						String replace = value.replace(",", "");
						String substringWithQuotes = "\"" + value + "\"";
						readLine = readLine.replace(substringWithQuotes, replace);
					}
					String[] spliter = readLine.split(",");
					record.setSno(entries.size()+1);
					SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy");
					record.setDate(df.parse(spliter[1]));
					record.setChequeNo(spliter[3]);
					record.setDescription(spliter[4]);
					if(!spliter[5].isEmpty()){
						record.setAmount(-Double.parseDouble(spliter[5]));
						}
						if(!spliter[6].isEmpty()){
						record.setAmount(Double.parseDouble(spliter[6]));
						}
					entries.add(record);

					} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		return null;
	
	}
	}

	

//	public String loadStatement(String filePath) {	
//	try {
//		PdfReader reader = new PdfReader(filePath);
//		int n = reader.getNumberOfPages();
//		ArrayList<String> statementEntries = new ArrayList<String>();
//		for (int i = 1; i <= n; i++) {
//			
//			String str = PdfTextExtractor.getTextFromPage(reader, i);
//			String[] lineWise = str.split("\n");
//			boolean omit = false;
//			
//			for (String string : lineWise) {
//				if(string.startsWith("Date")){
//					omit = true;
//				}
//				else if(string.startsWith("TOTAL")){
//					omit = false;
//				}
//				if(omit){
//					if(string.split(" ").length>3){
//						statementEntries.add(string);
//					}
//				}
//			}
//		}
//		
//		reader.close();				
//			
//		int count = 0;
//		double previousBalance = 0;
//		  
//		   	for (String statementEntry : statementEntries) {
//		   		
//		   		String[] statementValues = statementEntry.split(" ");
//		   		
//		   		List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
//		   		
//		   		String amountInString = statementValues[statementValues.length-1].replace(",", "");
//		   		
//		   		if(count == 0) {
//		   			previousBalance = Double.parseDouble(amountInString);
//		        } else {
//		        	Entry entry = new Entry();
//		        	
//		        	Date date;
//		        	try {
//		        		date = new SimpleDateFormat("dd/MM/yyyy").parse(statementValues[1]);
//		        		entry.setSno(entries.size() + 1);
//		        		entry.setDate(date);
//		        	} catch (ParseException e) {
//		        		// TODO Auto-generated catch block
//		        		e.printStackTrace();
//		        	}
//		        	
//		        	String description = "";
//		        	for (int i = 3; i < statementValues.length-2; i++) {
//		        		description += statementValues[i];        
//		        	}		        	
//		        	entry.setDescription(description);
//		        	
//		        	double amount = previousBalance - Double.parseDouble(amountInString);
//		        	previousBalance = Double.parseDouble(amountInString);		        	
//		        	if(amount < 0) {
//		        		entry.setType('C');
//		        	} else {
//		        		entry.setType('D');
//		        	}
//		        	entry.setAmount(Math.abs(amount));
//		        	entries.add(entry);
//		        }
//		   		count++;
//		   	}
//	} catch (NumberFormatException | IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//}


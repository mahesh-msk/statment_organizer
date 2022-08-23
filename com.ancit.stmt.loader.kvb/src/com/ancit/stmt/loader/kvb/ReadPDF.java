package com.ancit.stmt.loader.kvb;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class ReadPDF {

	public static void main(String[] args) throws IOException {
		loadStatement("C:\\Users\\Sathieswar\\Desktop\\From Malai\\Bank Statement\\KVBStatement.pdf");
	}

	public static void loadStatement(String filePath) throws IOException {
		PdfReader reader = new PdfReader(filePath);
		int n = reader.getNumberOfPages();
		ArrayList<String> statementEntries = new ArrayList<String>();
		for (int i = 1; i <= n; i++) {
			
			String str = PdfTextExtractor.getTextFromPage(reader, i);
			String[] lineWise = str.split("\n");
			boolean omit = false;
			
			for (String string : lineWise) {
				if(string.startsWith("Date")){
					omit = true;
				}
				else if(string.startsWith("TOTAL")){
					omit = false;
				}
				if(omit){
					if(string.split(" ").length>3){
						statementEntries.add(string);
					}
				}
			}
		}
		
		reader.close();				
			
		int count = 0;
		double previousBalance = 0;
		  
		   	for (String statementEntry : statementEntries) {
		   		
		   		String[] statementValues = statementEntry.split(" ");
		   		
		   		List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
		   		
		   		String amountInString = statementValues[statementValues.length-1].replace(",", "");
		   		
		   		if(count == 0) {
		   			previousBalance = Double.parseDouble(amountInString);
		        } else {
		        	Entry entry = new Entry();
		        	
		        	Date date;
		        	try {
		        		date = new SimpleDateFormat("dd/MM/yyyy").parse(statementValues[1]);
		        		entry.setSno(entries.size() + 1);
		        		entry.setDate(date);
		        	} catch (ParseException e) {
		        		// TODO Auto-generated catch block
		        		e.printStackTrace();
		        	}
		        	
		        	String description = "";
		        	for (int i = 3; i < statementValues.length-2; i++) {
		        		description += statementValues[i];        
		        	}		        	
		        	entry.setDescription(description);
		        	
		        	double amount = previousBalance - Double.parseDouble(amountInString);
		        	previousBalance = Double.parseDouble(amountInString);		        	
		        	if(amount < 0) {
		        		entry.setType('C');
		        	} else {
		        		entry.setType('D');
		        	}
		        	entry.setAmount(Math.abs(amount));
		        	entries.add(entry);
		        }
		   		count++;
		   	}
		for (Entry entry2 : StatementManager.getInstance().getStatement().getEntries()) {
			System.out.println(entry2.getDescription() +"\t\t" + entry2.getAmount() +"\t\t" + entry2.getType());
		}
	}	
}
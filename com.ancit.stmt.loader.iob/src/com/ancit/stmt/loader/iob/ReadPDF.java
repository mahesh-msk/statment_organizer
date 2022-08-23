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

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class ReadPDF {

	public static void main(String[] args) throws IOException {
		loadStatement("C:\\Users\\Sathieswar\\Desktop\\From Malai\\Bank Statement\\IOBStatement.pdf");
	}

	public static void loadStatement(String filePath) throws IOException {
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
		   for (String statementEntry : statementEntries) {
		    String[] statementValues = statementEntry.split("\\t");
		    List<Entry> entries = StatementManager.getInstance()
		      .getStatement().getEntries();

		    
		    String amountInString = statementValues[statementValues.length-2].replace(",", "");
		    if(count == 0) {
		     previousBalance = Double.parseDouble(amountInString);
		     
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
	}
}

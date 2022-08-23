package com.ancit.stmt.model.loader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class ICICIStatementLoader implements IStatementParser{

	public String loadStatement(String filePath) {
		String result = "";
		
		try {

			if (filePath.contains(".psv")) {
				loadPSVFile(filePath);
			} else if (filePath.contains(".txt")) {
				loadCSVFile(filePath);
			} else if (filePath.contains(".pdf")) {
				loadPDFFile(filePath);
			}
		} catch (Exception e) {// Catch exception if any
			 e.printStackTrace();
		}

		return result;
	}

	private void loadPDFFile(String filePath) {
		try {
			PdfReader reader = new PdfReader(filePath);
			int n = reader.getNumberOfPages();
			ArrayList<String> statementEntries = new ArrayList<String>();
			for (int i = 1; i <= n; i++) {
				String str=PdfTextExtractor.getTextFromPage(reader, i); //Extracting the content from a particular page.
			    String[] lineWise = str.split("\n");
			    int count = 1;
			    List<String> entries = Arrays.asList(lineWise);
			    boolean omit = true;
			    for (String string : lineWise) {
			    	
			    	if(!string.toUpperCase().contains("DATE") && omit) {
			    		continue;
			    	}
			    	
			    	if(string.toUpperCase().contains("DATE")) {
			    		omit = false;
//			    		System.out.println(string);
			    	}
			    	
			    	Pattern pattern = Pattern.compile("^(([0-9])|([0-2][0-9])|([3][0-1]))\\-([0-1][0-9])\\-\\d{4}$");
			    	String matcherString = string.split(" ")[0];
					Matcher matcher = pattern.matcher(matcherString);
			    	if(matcher.find()) {
			    		statementEntries.add(string.replaceAll("( )+", "\t"));
			    	}
			    	
			    	count++;
			    	
				}
				
			}
			
			reader.close();
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
					Date date = new SimpleDateFormat("dd-MM-yyyy")
							.parse(statementValues[0]);
					entry.setSno(entries.size() + 1);
					entry.setDate(date);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void loadCSVFile(String filePath) throws FileNotFoundException,
			IOException, ParseException {
		FileInputStream fstream = new FileInputStream(filePath);
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
//							entry.setChequeNo(record[4]);
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
		// Close the input stream
				in.close();
	}

	private void loadPSVFile(String filePath) throws FileNotFoundException,
			IOException, ParseException {
		// Open the file that is the first
		// command line parameter
		FileInputStream fstream = new FileInputStream(filePath);
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(in));
		String strLine;
		int i = 0;
		// Read File Line By Line
		while ((strLine = br.readLine()) != null) {
			// Print the content on the console

			if (strLine.contains("|")) {
				if (i == 0) {
					i++;
					continue;
				}
				System.out.println(strLine);
				String[] record = strLine.split("\\|");

				List<Entry> entries = StatementManager.getInstance()
						.getStatement().getEntries();

				Entry entry = new Entry();
				Date date = new SimpleDateFormat("dd-MM-yyyy")
						.parse(record[2]);
				entry.setSno(entries.size() + 1);
				entry.setAmount(Double.parseDouble(record[7].replace(
						",", "")));
				entry.setChequeNo(record[4]);
				entry.setDate(date);
				if (record[6].equals("CR")) {
					entry.setType('C');
				} else {
					entry.setType('D');
				}
				entry.setDescription("Transaction with " + record[1]
						+ " was performed on " + record[2] + "for "
						+ record[5]);

				entries.add(entry);

			}
		}
		// Close the input stream
		in.close();
	}

	public static void main(String[] args) {
		new ICICIStatementLoader()
				.loadStatement("C:\\revamalai's world\\malais world\\business\\ancit\\Statements\\2013-2014\\unlocked.pdf");
	}

}

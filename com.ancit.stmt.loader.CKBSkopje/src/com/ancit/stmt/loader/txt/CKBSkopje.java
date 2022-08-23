package com.ancit.stmt.loader.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class CKBSkopje implements IStatementParser {
   Scanner scanner;
	public CKBSkopje() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String loadStatement(String filePath) {
		System.out.println("hi");
		

		try {
			File fileDir = new File(filePath);

			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF-8"));
			//BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir),"cp1252"));

			String str;
			int i = 0;
			
			List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
			

			while ((str = in.readLine()) != null) {
				System.out.println(str);

				if(str.trim().isEmpty()) {
					continue;
				}
				
				String readLine = str;
				System.out.println(readLine);
				Entry record = new Entry();
				
				
				if (i >=1 ) {
					try {
						
						System.out.println("hi1");
						String[] spliter = readLine.split("\t");
						int j = 0;
						String description = "";
						for (String string : spliter) {
							System.out.println(string);
							if(j > 7) {
								description += string;
							}
							j++;
						}
						record.setSno(entries.size()+1);
						System.out.println(record.getSno());
						SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
						record.setDate(df.parse(spliter[1]));
						System.out.println(record.getDate());
						record.setChequeNo(spliter[4]);
						System.out.println(record.getChequeNo());
						record.setDescription(description);
						System.out.println(record.getDescription());
						if(!spliter[5].equals("0.00")){
							record.setAmount(-Double.parseDouble(spliter[5]));
							System.out.println(record.getAmount());
							}
						else if(!spliter[6].equals("0.00")){
							record.setAmount(Double.parseDouble(spliter[6]));
							System.out.println(record.getAmount());
							}
						entries.add(record);

						} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}i++;
				

			
			}

			in.close();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	
		
		return null;
	
	}

}

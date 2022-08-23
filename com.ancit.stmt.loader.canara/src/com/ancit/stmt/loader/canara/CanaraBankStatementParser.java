package com.ancit.stmt.loader.canara;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class CanaraBankStatementParser implements IStatementParser {

	private Scanner scanner;

	public CanaraBankStatementParser() {
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
			if (i > 20) {
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
					record.setChequeNo(spliter[2]);
					record.setDescription(spliter[3]);
					if (spliter[4].isEmpty()) {
						String amount = spliter[5];
						record.setAmount(Double.parseDouble(amount));
					}

					if(spliter[5].isEmpty()){

						String amount = spliter[4];
						record.setAmount(-Double.parseDouble(amount));
					}
					entries.add(record);

					System.out.println(record.getDate() + "" + record.getDescription() + "" + record.getAmount());
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

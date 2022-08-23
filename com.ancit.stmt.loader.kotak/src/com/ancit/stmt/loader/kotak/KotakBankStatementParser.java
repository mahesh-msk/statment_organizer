package com.ancit.stmt.loader.kotak;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class KotakBankStatementParser implements IStatementParser {

	Scanner scanner;

	public KotakBankStatementParser() {
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
			if (readLine.startsWith("Opening balance")) {
				break;
			}

			i++;
			if (i > 13) {
				try {
					Entry record = new Entry();
					int start = readLine.indexOf("\"");

					if (start != -1) {
						// 3,27/08/2017,ATL/2021543337/622018/SBI SUCHEENDRAMAGASTHEESWARA,723915002780,"1,600.00",DR,302.05,CR
					//	4,24/08/2017,ATL/2021543337/622018/SBI CMBT BUS STANDCHENNAITNI,723617001689,"1,000.00",DR,"1,902.05",CR
					//	28,5/8/2017,"ATL/2021543337/800010/261, Trunk RoadKanchipuramTN",721713254057,"1,200.00",DR,436.45,CR	
						
						if(Character.isDigit(readLine.charAt(start+1))){
							String substring1 = readLine.substring(start + 1);
							int last = substring1.indexOf("\"");
							String value = substring1.substring(0, last);
							String replace = value.replace(",", "");
							String substringWithQuotes = "\"" + value + "\"";
							readLine = readLine.replace(substringWithQuotes, replace);
						}
					}
					String[] spliter = readLine.split(",");
					record.setSno(entries.size() + 1);
					SimpleDateFormat df = new SimpleDateFormat("dd/mm/yy");
					record.setDate(df.parse(spliter[1]));

					record.setDescription(spliter[2]);
					record.setChequeNo(spliter[3]);
					if (spliter[5].equals("CR")) {
						String amount = spliter[4];
						record.setAmount(Double.parseDouble(amount));
					}
					if (spliter[5].equals("DR")) {
						String amount = spliter[4];
						record.setAmount(-Double.parseDouble(amount));
					}

					entries.add(record);

				} catch (NumberFormatException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return null;
	}

}

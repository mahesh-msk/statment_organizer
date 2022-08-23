package com.ancit.stmt.loader.indianbank;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class IndainStatementPareser implements IStatementParser {

	public IndainStatementPareser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String loadStatement(String filePath) {


		try {

			FileInputStream file = new FileInputStream(filePath);
			
			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			Iterator<Row> iterator = sheet.iterator();
			for (int i = 0; i <= 18; i++) {
				iterator.next();
			}
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
				Entry record = new Entry();
				record.setSno(entries.size()+1);
				Iterator<Cell> cellIterator = currentRow.iterator();
				String value = new String();
				int i = 0;
				while (cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();
					// getCellTypeEnum shown as deprecated for version 3.15
					// getCellTypeEnum ill be renamed to getCellType starting
					// from version 4.0
					if (currentCell.getCellType() == CellType.STRING) {
						value = currentCell.getStringCellValue().trim();

						if (i == 0) {
							if (value.isEmpty()) {
								break;
							} else {
								if(!value.contains("Total"))
								{
															
								SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
								record.setDate(df.parse(value));
								}
								else
									break;
																
							}
						}
						
						else if (i == 3) {
							record.setDescription(currentCell.getStringCellValue());
						}
						
						else if (i == 4) {
							record.setChequeNo((currentCell.getStringCellValue()));
						}
						
						else if (i == 5) {
							if (!value.trim().isEmpty()) {
								record.setAmount(-Double.parseDouble(value.replace(",", "")));
							}
						} else if (i == 6) {
							if (!value.trim().isEmpty()) {
								record.setAmount(Double.parseDouble(value.replace(",", "")));
							}
						}
						

					} 
					i++;

				}
				if (value.contains("Total")) {
					break;
				}
				entries.add(record);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	
		return null;
	}

}

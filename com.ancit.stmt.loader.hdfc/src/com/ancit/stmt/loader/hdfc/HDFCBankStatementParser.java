package com.ancit.stmt.loader.hdfc;

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
import org.apache.poi.ss.usermodel.Row;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class HDFCBankStatementParser implements IStatementParser {

	public HDFCBankStatementParser() {
	}

	@Override
	public String loadStatement(String filePath) {


		try {

			FileInputStream file = new FileInputStream(filePath);
			boolean flag = false;
			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			Iterator<Row> iterator = sheet.iterator();
			for (int i = 0; i <= 21; i++) {
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
					if (currentCell.getCellType() == Cell.CELL_TYPE_STRING) {
						value = currentCell.getStringCellValue().trim();

						if (i == 0) {
							if (value.isEmpty()) {
								break;
							} else {
								if(!value.contains("*"))
								{
								
								SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");
								record.setDate(df.parse(value));
								}
								else
									break;
								
							}
						}
						
						else if (i == 1) {
							record.setDescription(currentCell.getStringCellValue());
						}
						
						else if (i == 2) {
							record.setChequeNo((currentCell.getStringCellValue()));
						}
						

					} else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						double val = currentCell.getNumericCellValue();
						if (i == 4) {
							
							record.setAmount(-Math.abs(val));
						} else if (i == 5) {
							record.setAmount(val);
						}

					}

					i++;

				}
				if(value.isEmpty())
				{
					
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

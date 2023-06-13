package com.ancit.stmt.loader.pnb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

public class PNBankStatementParser implements IStatementParser {

	public PNBankStatementParser() {
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
			for (int i = 0; i <= 16; i++) {
				iterator.next();
			}
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
				Entry record = new Entry();
				
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

						if (i == 1) {
							if (value.isEmpty() || value == null) {
								break;
							} else {
								
								SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
								record.setDate(df.parse(value));
								record.setSno(entries.size() + 1);
								System.out.println(value);

							}

						}
						else if (i == 8) {
							record.setDescription(value);
							System.out.println(value);

						}

						else if (i == 9) {
							record.setDescription(value);
							System.out.println(value);

						}
						

					} else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {

						if (i == 3) {
							 NumberFormat nf=new DecimalFormat("#.######");
							 record.setChequeNo(nf.format(currentCell.getNumericCellValue()));
							System.out.println(record.getChequeNo());
						}
						else if (i == 9) {
							record.setDescription(Double.toString(currentCell.getNumericCellValue()));
							System.out.println(record.getDescription());

						}
						else if (i == 5) {

							record.setAmount(-currentCell.getNumericCellValue());
							System.out.println(record.getAmount());

						} else if (i == 7) {

							record.setAmount(currentCell.getNumericCellValue());
							System.out.println(record.getAmount());

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

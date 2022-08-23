package com.ancit.stmt.loader.sbi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class SBIStatementParser implements IStatementParser {

	public SBIStatementParser() {
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
			for (int i = 0; i <= 19; i++) {
				iterator.next();
			}
			while (iterator.hasNext()) {
				Row currentRow = iterator.next();
				List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
				Entry ent = new Entry();
				ent.setSno(entries.size()+1);
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

						if (i == 2) {
							ent.setDescription(value);
							//System.out.print(value.trim() + "--");
						} 
						
						else if (i == 3) {
							if (!value.trim().isEmpty()) {
								ent.setChequeNo(value);
								//System.out.print(value + "--");
							}
						}
						else if (i == 4) {
							if (!value.trim().isEmpty()) {
								ent.setAmount(-Double.parseDouble(value.replace(",", "")));
								//System.out.print(value + "--");
							}
						} 
						else if (i == 5) {
							if (!value.trim().isEmpty()) {
								ent.setAmount(Double.parseDouble(value.replace(",", "")));
							}
							//System.out.print(value + "--");
						}
						
						
						
						
						

					} else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						double val = currentCell.getNumericCellValue();
						if(i==1){
            			  	
      	            		
	            			ent.setDate(currentCell.getDateCellValue());
		            			System.out.println("Txn Date: "+currentCell.getDateCellValue());
	            			
	            		}
						if(i==4)
						{
							ent.setAmount(-val);
							System.out.print(Double.toString(val)+"--");
						}
						else if(i==5)
						{
							ent.setAmount(val);
							System.out.print(Double.toString(val)+"--");
						}


					}

					i++;

				}
				if(value.contains("**"))break;
				entries.add(ent);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				return null;
	}

}

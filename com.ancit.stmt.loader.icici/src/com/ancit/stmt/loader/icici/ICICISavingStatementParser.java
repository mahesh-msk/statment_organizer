package com.ancit.stmt.loader.icici;

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

public class ICICISavingStatementParser implements IStatementParser {

	public ICICISavingStatementParser() {
		// TODO Auto-generated constructor stub
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
			for (int i = 0; i <= 12; i++) {
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
							System.out.println(value);
							SimpleDateFormat dfh = new SimpleDateFormat("dd/MM/yyyy");
							ent.setDate(dfh.parse(currentCell.getStringCellValue()));
							System.out.println(dfh.parseObject(value));
							System.out.print((currentCell.getStringCellValue() + "--"));
						} 
						else if (i == 4) {
							if(value.contains("-"))
							{
								
							}
							else
							{
							ent.setChequeNo(value);
							System.out.print((currentCell.getStringCellValue() + "--"));
							}
						}
						else if (i == 5) {
							ent.setDescription(currentCell.getStringCellValue());
							System.out.print((currentCell.getStringCellValue() + "--"));
						}
						
						
						

					} else if (currentCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						double val = currentCell.getNumericCellValue();
						
						 if (i == 4) {
							 NumberFormat nf=new DecimalFormat("#.######");
							 ent.setChequeNo(nf.format(val));
							}
						 else if (i == 6) {
								
									ent.setAmount(-Math.abs(val));
								
							} else if (i == 7 ) {
								if(val>0){
									ent.setAmount(val);		
								}
							
								
							}
						 
						 else if(i==4)
						 {
							 ent.setChequeNo(value);
						 }
						 
					}

					i++;

				}
				if(value.isEmpty())
				{
					
					break;
				}
				entries.add(ent);
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

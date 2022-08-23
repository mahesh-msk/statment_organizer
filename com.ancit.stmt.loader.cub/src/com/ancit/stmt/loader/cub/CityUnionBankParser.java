package com.ancit.stmt.loader.cub;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class CityUnionBankParser implements IStatementParser {

	public CityUnionBankParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String loadStatement(String filePath) {

	    File inputWorkbook = new File(filePath);
	    Workbook w;
	    try {
	      w = Workbook.getWorkbook(inputWorkbook);
	      // Get the first sheet
	      Sheet sheet = w.getSheet(0);
	      // Loop over first 10 column and lines

	  		List<Entry> entries = StatementManager.getInstance()
				.getStatement().getEntries();
	      
	      boolean parse = false;
	      for (int j = 0; j < sheet.getRows(); j++) {
	    	  if(sheet.getCell(0, j).getContents().equals("DATE")){
	    		  parse =true;
	    		  continue;
	    	  }
	    	  else if(sheet.getCell(0, j).getContents().equals("TOTAL")){
	    		  parse =false;
	    		  break;
	    	  } 
	    	  else if(sheet.getCell(0, j).getContents().isEmpty()&&parse){
	    		  continue;
	    	  }
	    	  if(parse) {
	    	  Entry entry = new Entry();
	        for (int i = 0; i < sheet.getColumns(); i++) {
	          Cell cell = sheet.getCell(i, j);
	          CellType type = cell.getType();
	          
	          
	          if (type == CellType.LABEL) {
	        	  String cellval = cell.getContents();
	        	  switch (i) {
				case 0:
					Date date;
					try {
						date = new SimpleDateFormat("dd/MM/yyyy")
						.parse(cellval);
						entry.setDate(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					break;
				case 1:
					entry.setDescription(cellval);
					break;
				case 2:
					entry.setChequeNo(cellval);
					break;
				case 3:
					if(!cellval.trim().isEmpty()) {
						entry.setType('D');
						entry.setAmount(Double.parseDouble(cellval));
					}
					break;
				case 4:
					if(!cellval.trim().isEmpty()) {
						entry.setType('C');
						entry.setAmount(Double.parseDouble(cellval));
					}
					break;
				default:
					break;
				}
	        	  
//	            System.out.print(" String = "+cell.getContents()+"\t");
	            
	          }
	         
	        }
	        entry.setSno(entries.size()+1);
	        entries.add(entry);
	      }
	    	  
	      }
	      
	     
	    } catch (BiffException e) {
	      e.printStackTrace();
	    } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    for (Entry entry2 : StatementManager.getInstance().getStatement().getEntries()) {
			System.out.println(entry2.getDescription());
		}
	    
	  
		return "";
	}

}

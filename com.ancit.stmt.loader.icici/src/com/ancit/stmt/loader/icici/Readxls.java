package com.ancit.stmt.loader.icici;

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

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;

public class Readxls {

	public static void main(String[] args) {
		try {
		     
		    FileInputStream file = new FileInputStream("C:\\Users\\malaireva\\Google Drive\\Personal\\Accounts\\Accounts 2015-2016\\Statements\\Dec-FebMid2016.xls");
		    boolean flag = false;
		    //Get the workbook instance for XLS file
		    HSSFWorkbook workbook = new HSSFWorkbook(file);
		 
		    //Get first sheet from the workbook
		    HSSFSheet sheet = workbook.getSheetAt(0);
		     
		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		         
		        //For each row, iterate through each columns
		        Iterator<Cell> cellIterator = row.cellIterator();
		        int cnt = 1;
		        List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
            	Entry ent = new Entry();
		       
		        while(cellIterator.hasNext()) {
		             
		            Cell cell = cellIterator.next();
		            
		            if(flag){
		            	
		            	if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
		            	if(!cell.getStringCellValue().equals("")){
		            		
		            		if(cnt==1){
		            			ent.setSno(Integer.parseInt(cell.getStringCellValue()));
			            			System.out.println("S No: "+cell.getStringCellValue());
		            			cnt++;
		            		}else if(cnt==2){
		            			System.out.println("Value Date:"+cell.getStringCellValue());
		            			cnt++;
		            		}else if(cnt==3){
		            			ent.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(cell.getStringCellValue()));
		            			System.out.println("Transaction Date: "+cell.getStringCellValue());
		            			cnt++;
		            		}else if(cnt==4){
		            			ent.setChequeNo(cell.getStringCellValue());
			            			System.out.println("ChNo: "+cell.getStringCellValue());
		            			cnt++;
		            		}else if(cnt==5){
		            			ent.setDescription(cell.getStringCellValue());
		            			System.out.println("Desc: "+cell.getStringCellValue());
		            			cnt++;
		            		}	            		
		            	}else{
		            		continue;
		            	}
		            	} else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
		            		if(cnt==6){
		            			if(cell.getNumericCellValue()>0){
		            				ent.setType('D');
		            				ent.setAmount(cell.getNumericCellValue());
		            				System.out.println("Debited"+ent.getAmount());
		            			}
		            			cnt++;
		            		}else if(cnt==7){
		            			if(cell.getNumericCellValue()>0){
		            				ent.setType('C');
		            				ent.setAmount(cell.getNumericCellValue());
		            				System.out.println("Credited"+ent.getAmount());
		            			}
		            			cnt++;
		            		}else if(cnt==8){
		            			cnt++;
		            			entries.add(ent);
		            			System.out.println("-------------------------------");
		            		}	
		            	}
		            }
		            
		            if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
		            if(cell.getStringCellValue().equals("S No.")){
		            	flag=true;
		            	break;
		            }
		            }
		        }
		        
		    }
		    file.close();
		   
		     
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

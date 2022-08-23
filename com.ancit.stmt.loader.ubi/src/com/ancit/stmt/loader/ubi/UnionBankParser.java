package com.ancit.stmt.loader.ubi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class UnionBankParser implements IStatementParser{

		
	public UnionBankParser() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		 
	}

	@Override
	public String loadStatement(String filePath) {
		 try {
				FileInputStream file = new FileInputStream(filePath);
				 boolean flag = false;
				HSSFWorkbook workbook = new HSSFWorkbook(file);
				HSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowItr = sheet.iterator();
				
				List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
				while(rowItr.hasNext()){
					Row row = (Row) rowItr.next();
					Iterator<Cell> cellItr = row.cellIterator();
					 int cnt = 1;
					 
		         	Entry ent = new Entry();
		         	
					while(cellItr.hasNext()){
						Cell cell = (Cell) cellItr.next();
						
						if(flag){
			            	
			            	if(cell.getCellType() == CellType.STRING) {
			            	if(!cell.getStringCellValue().equals("")){
			            		
			            		if(cnt==3){
			            			ent.setDescription(cell.getStringCellValue());
			            			System.out.println("Description: "+cell.getStringCellValue());
			            			cnt++;
			            		}else if(cnt==4){
			            			
			            			System.out.println("TID: "+cell.getStringCellValue());
			            			cnt++;
			            		}       		
			            	}else{
			            		continue;
			            	}
			            	} else if(cell.getCellType() == CellType.NUMERIC) {
			            		if(cnt==1){
			            			ent.setSno((int)cell.getNumericCellValue());
				            			System.out.println("S No: "+cell.getNumericCellValue());
			            			cnt++;
			            		}else if(cnt==2){
			            			ent.setDate(cell.getDateCellValue());
			            			System.out.println("Transaction Date: "+cell.getDateCellValue());
			            			cnt++;
			            		}else if(cnt==5){
			            			if(cell.getNumericCellValue()== 0.0){
			            				System.out.println("empty");
			            		//		return;
			            			}
			            			if(cell.getNumericCellValue()>0){
//			            				ent.setChequeNo(cell.getStringCellValue());
				            			System.out.println("ChNo: "+cell.getNumericCellValue());
			            			}
			            		
		            			cnt++;
		            		} else if(cnt==6){
			            			if(cell.getNumericCellValue()>0){
			            				ent.setType('D');
			            				ent.setAmount(cell.getNumericCellValue());
			            				System.out.println("Debited: "+ent.getAmount());
			            			}
			            			cnt++;
			            		}else if(cnt==7){
			            			if(cell.getNumericCellValue()>0){
			            				ent.setType('C');
			            				ent.setAmount(cell.getNumericCellValue());
			            				System.out.println("Credited: "+ent.getAmount());
			            			}
			            			cnt++;
			            		}else if(cnt==8){
			            			cnt++;
			            			 entries.add(ent);
			            		}
			            	}else if(cell.getCellType()== CellType.BLANK){
			            		System.out.println(cell.toString());
			            	}
			            }
			            
			            if(cell.getCellType() == CellType.STRING) {
			            if(cell.getStringCellValue().equals("Sr No.")){
			            	flag=true;
			            	break;
			            }
			            }else  if(cell.getCellType() == CellType.BLANK) {
				            if(cell.getStringCellValue().equals(null)){
				            	flag=true;
				            	break;
				            }
				            }
			            
			           
						
					}
					
				}
				 
				file.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		return null;
	}

	

}

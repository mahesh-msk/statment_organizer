package com.ancit.stmt.loader.icici;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class ReadPdf {
	public static void main(String[] args) {
		try {
			PdfReader reader = new PdfReader("C:\\Users\\Sathieswar\\Downloads\\OpTransactionHistory05-08-2014.pdf");
			int n = reader.getNumberOfPages();
			List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();
			for (int i = 1; i <= n; i++) {
				String str = PdfTextExtractor.getTextFromPage(reader, i);
				String[] lineWise = str.split("\n");
				
				
				boolean omit = true;
				for (int j = 0; j < lineWise.length; j++) {
					if (i == 1 && !lineWise[j].startsWith("Serial Number") && omit) {
						continue;
					} else {

						if (lineWise[j].contains("Serial Number")) {
							omit = false;
						}
						
						if(lineWise[j].split(" ")[0].matches("^[0-9]") && lineWise[j].split(" ").length>1){
								String[] val = lineWise[j].split(" ");
								Entry ent = new Entry();
								ent.setSno(Integer.parseInt(val[0]));
								try {
									ent.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(val[2]));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								ent.setChequeNo(val[3]);
								
								String desc = ""; 
								for (int k = 4; k < val.length-3; k++) {
									desc += val[k]+" ";
								}
								ent.setDescription(desc);
								if(val[val.length-3].equals("Dr.")){
		            				ent.setType('D');
		            			}else if(val[val.length-3].equals("Cr.")){
		            				ent.setType('C');
		            			}
								ent.setAmount(Double.parseDouble(val[val.length-2].replaceAll(",","")));
								entries.add(ent);
						}else if(lineWise[j].split(" ")[0].matches("\\d{1,2}/\\d{1,2}/\\d{4}")){
							String[] val = lineWise[j].split(" ");
							Entry ent = new Entry();
							ent.setSno(Integer.parseInt(lineWise[j+1]));
							try {
								ent.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(val[1]));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ent.setChequeNo(val[2]);
							ent.setDescription(val[3]+" "+lineWise[j+2]);
							
							if(val[val.length-3].equals("Dr.")){
	            				ent.setType('D');
	            			}else if(val[val.length-3].equals("Cr.")){
	            				ent.setType('C');
	            			}
							
							ent.setAmount(Double.parseDouble(val[val.length-2].replaceAll(",","")));																					
							entries.add(ent);
						}
					}
				}
			}
			reader.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}

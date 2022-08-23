package com.ancit.stmt.loader.icici;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.Entry;
import com.ancit.stmt.model.StatementManager;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class ICICICorporateStatementParser implements IStatementParser {

	public ICICICorporateStatementParser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String loadStatement(String filePath) {

		try {

			// if (filePath.contains(".psv")) {
			// loadPSVFile(filePath);
			// } else if (filePath.contains(".txt")) {
			// loadCSVFile(filePath);
			// } else if (filePath.contains(".pdf")) {
			// loadPDFFile(filePath);
			// } else

			if (filePath.contains(".xls")) {
				loadXlsFile(filePath);
			}
		} catch (Exception e) {// Catch exception if any
			e.printStackTrace();
		}
		return "";

	}

	private void loadPDFFile(String filePath) {
		try {
			PdfReader reader = new PdfReader(filePath);
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

						if (lineWise[j].split(" ")[0].matches("^[0-9]") && lineWise[j].split(" ").length > 1) {
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
							for (int k = 4; k < val.length - 3; k++) {
								desc += val[k] + " ";
							}
							ent.setDescription(desc);
							if (val[val.length - 3].equals("Dr.")) {
								ent.setType('D');
							} else if (val[val.length - 3].equals("Cr.")) {
								ent.setType('C');
							}
							ent.setAmount(Double.parseDouble(val[val.length - 2].replaceAll(",", "")));
							entries.add(ent);
						} else if (lineWise[j].split(" ")[0].matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
							String[] val = lineWise[j].split(" ");
							Entry ent = new Entry();
							ent.setSno(Integer.parseInt(lineWise[j + 1]));
							try {
								ent.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(val[1]));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ent.setChequeNo(val[2]);
							ent.setDescription(val[3] + " " + lineWise[j + 2]);

							if (val[val.length - 3].equals("Dr.")) {
								ent.setType('D');
							} else if (val[val.length - 3].equals("Cr.")) {
								ent.setType('C');
							}

							ent.setAmount(Double.parseDouble(val[val.length - 2].replaceAll(",", "")));
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

	private void loadPSVFile(String filePath) {

		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console

				if (strLine.contains("|")) {
					if (i == 0) {
						i++;
						continue;
					}
					System.out.println(strLine);
					String[] record = strLine.split("\\|");

					List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();

					Entry entry = new Entry();
					Date date = new SimpleDateFormat("dd-MM-yyyy").parse(record[2]);
					entry.setSno(entries.size() + 1);
					entry.setAmount(Double.parseDouble(record[7].replace(",", "")));
					entry.setChequeNo(record[4]);
					entry.setDate(date);
					if (record[6].equals("CR")) {
						entry.setType('C');
					} else {
						entry.setType('D');
					}
					entry.setDescription(
							"Transaction with " + record[1] + " was performed on " + record[2] + "for " + record[5]);

					entries.add(entry);

				}
			}
			// Close the input stream
			in.close();
		} catch (NumberFormatException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadCSVFile(String filePath) {

		try {
			FileInputStream fstream = new FileInputStream(filePath);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				String[] record = strLine.replace("  ", "|").trim().split("\\|");
				ArrayList<String> recordResult = new ArrayList<String>();
				for (String string : record) {
					if (string.length() >= 1) {
						recordResult.add(string);
					}
				}

				String[] recordSet = recordResult.toArray(new String[recordResult.size()]);

				if (recordSet.length > 8) {
					if (i == 0) {
						i++;
						continue;
					}
					if (recordSet.length == 10) {
						List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();

						Entry entry = new Entry();
						Date date = new SimpleDateFormat("dd/MM/yyyy").parse(recordSet[2]);
						entry.setSno(entries.size() + 1);
						entry.setAmount(Double.parseDouble(recordSet[7].replace(",", "")));
						entry.setChequeNo(recordSet[4]);
						entry.setDate(date);
						if (recordSet[6].trim().equalsIgnoreCase("CR")) {
							entry.setType('C');
						} else {
							entry.setType('D');
						}
						entry.setDescription("Transaction with " + recordSet[1] + " was performed on " + recordSet[2]
								+ "for " + recordSet[5]);

						entries.add(entry);

					} else if (recordSet.length == 9) {
						List<Entry> entries = StatementManager.getInstance().getStatement().getEntries();

						Entry entry = new Entry();
						Date date = new SimpleDateFormat("dd/MM/yyyy").parse(recordSet[2]);
						entry.setSno(entries.size() + 1);
						entry.setAmount(Double.parseDouble(recordSet[6].replace(",", "")));

						entry.setDate(date);
						if (recordSet[5].trim().equalsIgnoreCase("CR")) {
							entry.setType('C');
						} else {
							entry.setType('D');
						}
						entry.setDescription("Transaction with " + recordSet[1] + " was performed on " + recordSet[2]
								+ "for " + recordSet[4]);

						entries.add(entry);

					}
				}
			}
			in.close();

		} catch (NumberFormatException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadXlsFile(String filePath) {

		try {

			FileInputStream file = new FileInputStream(filePath);
			boolean flag = false;
			// Get the workbook instance for XLS file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			// Get first sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			Iterator<Row> iterator = sheet.iterator();
			for (int i = 0; i <= 6; i++) {
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

						if (i == 3) {

							ent.setDate(new SimpleDateFormat("dd-MM-yyyy").parse(currentCell.getStringCellValue()));
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
						
						if (i == 7) {

							if (value.contains("CR")) {

								ent.setAmount(val);
								System.out.print((currentCell.getNumericCellValue() + "--"));
							} else {
								ent.setAmount(-val);
								System.out.print((currentCell.getNumericCellValue() + "--"));
							}

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


	}
}

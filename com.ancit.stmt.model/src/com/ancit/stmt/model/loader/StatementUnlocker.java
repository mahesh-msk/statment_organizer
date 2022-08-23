package com.ancit.stmt.model.loader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class StatementUnlocker {
	
	public static void main(String[] args) {
		String lockedFile = "C:\\revamalai's world\\malais world\\business\\ancit\\Statements\\2013-2014\\MAY2013.pdf";                // Locked pdf filename
	      String password = "058705002592";              // Locked pdf password
	      String unlockedFile = "C:\\revamalai's world\\malais world\\business\\ancit\\Statements\\2013-2014\\unlocked.pdf";      // Filename of the new unlocked pdf
	       new StatementUnlocker().unlockPdf(lockedFile, password);
	}
	
	public static void unlockPdf(String filePath, String password) {
		try {
				
					  String unlockedFile = filePath.substring(0,filePath.lastIndexOf("\\")+1)+"Unlock-"+filePath.substring(filePath.lastIndexOf("\\")+1);
				      PdfReader reader = new PdfReader(filePath, password.getBytes());
				 
				      System.out.println("Unlocking...");
				      
				      reader = unlockPdf(reader);
				 
				      PdfEncryptor.encrypt(reader, new FileOutputStream(unlockedFile), null,
				         null, PdfWriter.AllowAssembly | PdfWriter.AllowCopy
				         | PdfWriter.AllowDegradedPrinting | PdfWriter.AllowFillIn
				         | PdfWriter.AllowModifyAnnotations | PdfWriter.AllowModifyContents
				         | PdfWriter.AllowPrinting | PdfWriter.AllowScreenReaders, false);
				               
				      System.out.println("PDF Unlocked!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static PdfReader unlockPdf(PdfReader reader) {
		if (reader == null) {
		return reader;
		}
		try {
		Field f = reader.getClass().getDeclaredField("encrypted");
		f.setAccessible(true);
		f.set(reader, false);
		} catch (Exception e) { // ignore
		}
		return reader;
		}

}

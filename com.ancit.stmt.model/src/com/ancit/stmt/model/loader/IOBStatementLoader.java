package com.ancit.stmt.model.loader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
 
public class IOBStatementLoader {
 
    /**
     * Main method.
     * @param args no arguments needed
     * @throws DocumentException 
     * @throws IOException
     */
    public static void main(String[] args)
        throws IOException, DocumentException {
    	
        new IOBStatementLoader().loadStatement("C:\\Eclipse Development\\Projects\\Company Manager\\Statement.pdf");
    }

	public void loadStatement(String filePath) throws IOException {
        PdfReader reader = new PdfReader(filePath);
        int n = reader.getNumberOfPages();
        ArrayList<String> statementEntries = new ArrayList<String>();
        for (int i = 1; i <= n; i++) {
        	String str=PdfTextExtractor.getTextFromPage(reader, i); //Extracting the content from a particular page.
            String[] lineWise = str.split("\n");
            int count = 1;
            List<String> entries = Arrays.asList(lineWise);
            boolean omit = true;
            for (String string : lineWise) {
            	
            	if(i == 1 && !string.startsWith("DATE") && omit) {
            		continue;
            	}
            	
            	if(string.contains("DATE")) {
            		omit = false;
            		System.out.println(string);
            	}
            	
            	Pattern pattern = Pattern.compile("^(([0-9])|([0-2][0-9])|([3][0-1]))\\-(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\-\\d{4}$");
            	String matcherString = string.split(" ")[0];
				Matcher matcher = pattern.matcher(matcherString);
            	if(matcher.find()) {
            		System.out.println(string.replaceAll("( )+", "\t"));
            	}
            	
            	count++;
            	
			}
			
        }
        
        reader.close();
        
        for (String string : statementEntries) {
			System.out.println(string);
		}
	}
}
package com.ancit.stmt.model.queries;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class RetrieveVoucherList {

	String query = "<ENVELOPE>" + "<HEADER>"
			+ "<TALLYREQUEST>Export Data</TALLYREQUEST>" + "</HEADER>"
			+ "<BODY>" + "<EXPORTDATA>" + "<REQUESTDESC>"
			+ "<REPORTNAME>Voucher Register</REPORTNAME>" + "</REQUESTDESC>"
			+ "</EXPORTDATA>" + "</BODY>" + "</ENVELOPE>";
	private BufferedReader br;

	public List<String> retrieveVoucherList() {
		List<String> companyNames = new ArrayList<String>();

		String result = new PerformQuery().performQuery(query, true);
		
		try {
			// First, create a new XMLInputFactory
			  XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			  // Setup a new eventReader
			  // convert String into InputStream
			InputStream is = new ByteArrayInputStream(result.getBytes());
			 XMLEventReader eventReader = inputFactory.createXMLEventReader(is);
			 
			 while (eventReader.hasNext()) {
			        XMLEvent event = eventReader.nextEvent();

			        if (event.isStartElement()) {
			          StartElement startElement = event.asStartElement();
			          System.out.println(startElement.getName().getLocalPart());
			        }
			 }
			 
		} catch (FactoryConfigurationError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XMLStreamException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

////		BufferedReader br = null;
//        try {
//            String sCurrentLine;
//            br = new BufferedReader(new StringReader(result));
//            while ((sCurrentLine = br.readLine()) != null) {
//                System.out.println(sCurrentLine);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (br != null) {
//                    br.close();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }

		return companyNames;
	}
	
	public static void main(String[] args) {
		RetrieveVoucherList retrieveVoucherList = new RetrieveVoucherList();
		retrieveVoucherList.retrieveVoucherList();
	}

}

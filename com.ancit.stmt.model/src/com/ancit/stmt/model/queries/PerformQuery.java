package com.ancit.stmt.model.queries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.ancit.stmt.model.StatementManager;

public class PerformQuery {
	
	String Url = StatementManager.getInstance().getTallyServerUrl();
	public String performQuery(String query) {
		
		String SOAPAction = "";
		String result = "";

		// Create the connection where we're going to send the file.
		try {
			URL url = new URL(Url);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			byte[] b = query.getBytes();

			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length",
					String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type",
					"text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			// Everything's set up; send the XML that was read in to b.
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(
					httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			String inputLine;
			

			while ((inputLine = in.readLine()) != null) {
				result +=inputLine;
			}

			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
public String performQuery(String query, boolean format) {
		
		String SOAPAction = "";
		String result = "";

		// Create the connection where we're going to send the file.
		try {
			URL url = new URL(Url);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			byte[] b = query.getBytes();

			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length",
					String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type",
					"text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			// Everything's set up; send the XML that was read in to b.
			OutputStream out = httpConn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(
					httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			String inputLine;
			

			while ((inputLine = in.readLine()) != null) {
				if (format) {
					result += inputLine + "\n";
				}
			}

			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}

}

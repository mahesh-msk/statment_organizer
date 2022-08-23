package com.ancit.stmt.exporter.csv;

import java.text.SimpleDateFormat;
import java.util.List;

import com.ancit.stmt.exporter.extnpt.IStatementExporter;
import com.ancit.stmt.model.EStatement;
import com.ancit.stmt.model.Entry;

public class CSVStatementExporter implements IStatementExporter {

	public CSVStatementExporter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String export(EStatement statement) {
		String fileContent = new String();
		List<Entry> entries = statement.getEntries();
		fileContent = "Date,Description,Amount\n";
		for (Entry record : entries) {
			
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			fileContent += (dateFormat.format(record.getDate()) + "," + record.getDescription()+" "+ record.getChequeNo()+ ","
					+ Double.toString(record.getAmount()) + "\n");
		}
		return fileContent;
	}

}

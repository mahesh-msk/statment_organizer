package com.ancit.stmt.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ancit.stmt.loader.parser.IStatementParser;
import com.ancit.stmt.model.queries.ExportEntryToTally;
import com.ancit.stmt.model.queries.ExportGroupToTally;
import com.ancit.stmt.model.queries.ExportNewGroupToTally;
import com.ancit.stmt.model.queries.PerformQuery;
import com.ancit.stmt.model.queries.RetrieveCompanyList;
import com.ancit.stmt.model.queries.TallyCategoryLoader;

public class StatementManager {

	private StatementManager() {

	}

	private static StatementManager manager;
	private EStatement statement;
	private Set<String> companies;
	private String tallyServerUrl;
	private RuleList ruleList;
	private List<Rule> rules;

	public Set<String> getCompanies() {
		if (companies == null) {
			companies = new HashSet<String>();
			loadCompanies();
		}
		return companies;
	}

	public static StatementManager getInstance() {
		if (manager == null) {
			manager = new StatementManager();
			manager.getStatement();
		}

		return manager;
	}

	public EStatement getStatement() {
		if (statement == null) {
			statement = new EStatement();
		}
		return statement;
	}

	public void loadStatement(IStatementParser parser, String filePath) {
		statement.getEntries().clear();
		parser.loadStatement(filePath);
		//autocategorize(null);
	}

	public void autocategorize(RuleList ruleList) {
		// TODO Auto-generated method stub
		List<Entry> entries = statement.getEntries();
		if (ruleList == null) {
			rules = this.getRuleList().getRules();
		} else {
			rules = ruleList.getRules();
		}
		for (Rule rule : rules) {
			Group searchGroup = searchGroup(rule.getCategoryName());
			for (Entry entry : entries) {
				if (entry != null) {
					if (entry.getDescription().contains(rule.getMatchString())) {
						if(searchGroup != null) {
						entry.setGroup(searchGroup);
						searchGroup.getEntries().add(entry);
						}
					}
				}
			}

		}

	}

	private Group searchGroup(String groupName) {
		List<Group> groups = statement.getGroup();
		for (Group group : groups) {
			if (group.getName().equals(groupName)) {
				return group;
			}
		}
		return null;
	}

	public void loadCategories(String companyName) {
		statement.getBankGroups().clear();
		statement.getGroup().clear();
		statement.setCompanyName(companyName);
		new TallyCategoryLoader().loadCategoryFile(companyName);
	}

	public void loadCategoriesFromFile(String filePath) {
		statement.getBankGroups().clear();
		statement.getGroup().clear();
		new TallyCategoryLoader().loadCategoryFromFile(filePath);
	}

	public void exportGroupToTally(Group bankGroup, List groupList) {
		for (Object object : groupList) {
			Group group = (Group) object;
			new ExportGroupToTally().exportGroupToTally(bankGroup, group);
		}

	}

	public void exportEntryToTally(Group bankGroup, Entry entry, Group group) {

		new ExportEntryToTally().exportEntryToTally(bankGroup, entry, group);

	}

	public void exportNewGroupToTally(Group group) {
		// TODO Auto-generated method stub

		new ExportNewGroupToTally().exportGroupToTally(group);
	}

	public void performQuery(String query) {
		new PerformQuery().performQuery(query);
	}

	public void loadCompanies() {
		companies.addAll(new RetrieveCompanyList().retrieveCompanyList());
	}

	public void saveStatement(String filePath) {
		try {
			FileOutputStream fout = new FileOutputStream(filePath);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(getStatement());
			// oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadEStatement(String filePath) {
		try {
			FileInputStream fin = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			statement = (EStatement) ois.readObject();
			// ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setTallyServer(String value) {
		this.tallyServerUrl = "http://" + value;
		// TODO Auto-generated method stub

	}

	public String getTallyServerUrl() {
		if (tallyServerUrl == null || tallyServerUrl.isEmpty()) {
			return null;
		}
		return tallyServerUrl;
	}

	public RuleList getRuleList() {
		if (ruleList == null) {
			ruleList = new RuleList();
		}
		return ruleList;
	}

	public void loadRuleList(String filePath) {
		// TODO Auto-generated method stub
		try {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ruleList = (RuleList) ois.readObject();
			autocategorize(ruleList);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

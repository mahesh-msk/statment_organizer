package com.ancit.stmt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RuleList implements Serializable {
	private List<Rule> rules;

	public List<Rule> getRules() {
		if (rules == null) {
			rules = new ArrayList<Rule>();
//			Rule rule = new Rule();
//			rule.setName("TNEB Bills");
//			rule.setCategoryName("EB");
//			rule.setDescription("Electricity bills in one category");
//			rule.setMatchString("TNEB");
//			rules.add(rule);
		}
		return rules;
	}
}

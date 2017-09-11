package com.jpmorgan.techtest.domain;

public class Sales {

	private Integer unitPrice;
	// The minimun product sale occurrence must be at least 1
	private Integer occurrences = 1;

	public Sales() {
	}

	public Integer getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(Integer occurrences) {
		this.occurrences = occurrences;
	}

}

package com.jpmorgan.techtest.domain;

public class Adjustment {

	private OperationEnum Operation;

	private Integer factor;

	public Adjustment() {
	}

	public OperationEnum getOperation() {
		return Operation;
	}

	public void setOperation(OperationEnum operation) {
		Operation = operation;
	}

	public Integer getFactor() {
		return factor;
	}

	public void setFactor(Integer factor) {
		this.factor = factor;
	}

}

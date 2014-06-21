package com.singlebyte.komma.test;

import com.singlebyte.komma.KommaField;

public class Sample {

	@KommaField(index = 0)
	private String value1;

	@KommaField(index = 1)
	private String value2;

	private String value3;

	public String getValue1() {
		return value1;
	}

	public String getValue2() {
		return value2;
	}

	public String getValue3() {
		return value3;
	}

	@Override
	public String toString() {
		return "Sample [value1=" + value1 + ", value2=" + value2+ ", value3="
				+ value3 + "]";
	}
}

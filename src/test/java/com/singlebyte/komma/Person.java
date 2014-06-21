package com.singlebyte.komma.test;

import com.singlebyte.komma.KommaField;

public class Person {

	@KommaField(index = 0)
	private String firstName;

	@KommaField(index = 1)
	private String lastName;

	@KommaField(index = 2)
	private String initials;

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getInitials() {
		return initials;
	}

	@Override
	public String toString() {
		return "Sample2 [firstName=" + firstName + ", lastName=" + lastName
				+ ", initials=" + initials + "]";
	}
}

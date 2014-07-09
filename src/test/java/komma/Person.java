package komma.test;

import komma.KommaField;

public class Person {

	@KommaField(value = 0)
	private String firstName;

	@KommaField(value = 1)
	private String lastName;

	@KommaField(value = 2)
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

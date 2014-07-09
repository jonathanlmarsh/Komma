package komma.test;

import java.lang.NullPointerException;

import junit.framework.TestCase;

import komma.KommaException;

public class KommaException_Test extends TestCase {

	public void testException() {
		String message = "Message";
		Throwable t = new NullPointerException();
		KommaException ke = new KommaException(message, t);
		assertEquals(message, ke.getMessage());
		assertEquals(t, ke.getCause());
	}
}

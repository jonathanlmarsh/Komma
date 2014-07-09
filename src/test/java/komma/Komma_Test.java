package komma.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import junit.framework.TestCase;

import komma.Komma;
import komma.Komma.KommaBuilder;
import komma.KommaException;

public class Komma_Test extends TestCase {

	public void testConsumerDefaults() throws IOException, KommaException {
		String testData = "one,two\nthree,four\nfive,six";
		InputStream stream = new ByteArrayInputStream(testData.getBytes("UTF-8"));

		KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, stream);
		Komma<Sample> komma = builder.build();
		List<Sample> data = komma.comsume();
		stream.close();

		assertEquals(3, data.size());
		assertEquals("one", data.get(0).getValue1());
		assertEquals("two", data.get(0).getValue2());
		assertNull(data.get(0).getValue3());
		assertEquals("three", data.get(1).getValue1());
		assertEquals("four", data.get(1).getValue2());
		assertNull(data.get(1).getValue3());
		assertEquals("five", data.get(2).getValue1());
		assertEquals("six", data.get(2).getValue2());
		assertNull(data.get(2).getValue3());
	}

	public void testConsumerPeriodSeparator() throws IOException, KommaException {
		String testData = "one.two\nthree.four\nfive.six";
		InputStream stream = new ByteArrayInputStream(testData.getBytes("UTF-8"));

		KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, stream);
		builder.separator('.');
		Komma<Sample> komma = builder.build();
		List<Sample> data = komma.comsume();
		stream.close();

		assertEquals(3, data.size());
		assertEquals("one", data.get(0).getValue1());
		assertEquals("two", data.get(0).getValue2());
		assertEquals("three", data.get(1).getValue1());
		assertEquals("four", data.get(1).getValue2());
		assertEquals("five", data.get(2).getValue1());
		assertEquals("six", data.get(2).getValue2());
	}

	public void testBuilderWithNullClass() throws UnsupportedEncodingException {
		String testData = "1,2";
		InputStream stream = new ByteArrayInputStream(testData.getBytes("UTF-8"));
		try {
			new KommaBuilder<Sample>(null, stream);
			fail("we should have caught an exception");
		} catch (IllegalArgumentException e) {
			// do nothing we wanted this
		}
		try {
			new KommaBuilder<Sample>(null, "stream");
			fail("we should have caught an exception");
		} catch (IllegalArgumentException e) {
			// do nothing we wanted this
		}
	}

	public void testBuilderWithNullString() {
		try {
			new KommaBuilder<Sample>(Sample.class, (String) null);
			fail("we should have caught an exception");
		} catch (IllegalArgumentException e) {
			// do nothing we wanted this
		}
	}

	public void testBuilderWithNullStream() {
		try {
			new KommaBuilder<Sample>(Sample.class, (InputStream) null);
			fail("we should have caught an exception");
		} catch (IllegalArgumentException e) {
			// do nothing we wanted this
		}
	}

	public void testBuilderWithList() throws IOException, KommaException {
		String testData = "one.two\nthree.four\nfive.six";
		InputStream stream = new ByteArrayInputStream(testData.getBytes("UTF-8"));

		List<Sample> data = new ArrayList<Sample>();
		KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, stream);
		builder.list(data);
		builder.separator('.');
		Komma<Sample> komma = builder.build();
		data = komma.comsume();
		stream.close();

		assertEquals(3, data.size());
		assertEquals("one", data.get(0).getValue1());
		assertEquals("two", data.get(0).getValue2());
		assertEquals("three", data.get(1).getValue1());
		assertEquals("four", data.get(1).getValue2());
		assertEquals("five", data.get(2).getValue1());
		assertEquals("six", data.get(2).getValue2());
	}

	public void testReadException() throws IOException {
		InputStream is = Mockito.mock(InputStream.class);
    Mockito.doThrow(new IOException()).when(is).read(Mockito.any(byte[].class), Mockito.anyInt(), Mockito.anyInt());

		try {
			KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, is);
			Komma<Sample> komma = builder.build();
			List<Sample> data = komma.comsume();
			fail("we should have caught an exception");
		} catch (KommaException e) {
			// do nothing
		}
	}

	public void testCloseException() throws IOException {
		InputStream is = Mockito.mock(InputStream.class);
		Mockito.doThrow(new IOException()).when(is).close();

		try {
			KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, is);
			Komma<Sample> komma = builder.build();
			List<Sample> data = komma.comsume();
			fail("we should have caught an exception");
		} catch (KommaException e) {
			// do nothing
		}
	}

	public void testPerson() throws KommaException, IOException {
		String testData = "jane,doe\njon,smith";
		InputStream stream = new ByteArrayInputStream(testData.getBytes("UTF-8"));

		KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, stream);
		Komma<Sample> komma = builder.build();
		List<Sample> data = komma.comsume();
		stream.close();

		assertEquals(2, data.size());
		assertEquals("jane", data.get(0).getValue1());
		assertEquals("doe", data.get(0).getValue2());
		assertEquals("jon", data.get(1).getValue1());
		assertEquals("smith", data.get(1).getValue2());
	}

	public void testPersonString() throws KommaException, IOException {
		String testData = "jane,doe\njon,smith";

		KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, testData);
		Komma<Sample> komma = builder.build();
		List<Sample> data = komma.comsume();

		assertEquals(2, data.size());
		assertEquals("jane", data.get(0).getValue1());
		assertEquals("doe", data.get(0).getValue2());
		assertEquals("jon", data.get(1).getValue1());
		assertEquals("smith", data.get(1).getValue2());
	}
}

package komma;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author jonathanmarsh
 *
 */
public class Komma<T> {

	private final Class<T> classType;
	private final InputStream inputStream;
	private final String str;
	private final String separator;
	private final List<T> list;

	private Komma(KommaBuilder<T> cb) {
		this.classType = cb.classType;
		this.inputStream = cb.inputStream;
		this.str = cb.str;
		// need to Pattern.quote to escape characters like the period, so "."
		// becomes "\\."
		this.separator = Pattern.quote(Character.toString(cb.separator));
		this.list = cb.list;
	}

	/**
	 * Consumes the stream and converts the data into a list with the rows
	 * represented by {@code <T>}.
	 *
	 * @return The List<T> representation of the CSV data found in the stream.
	 * @throws KommaException If an error is caught during consumption.
	 */
	public List<T> comsume() throws KommaException {
		List<T> rows = initializeList();
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			inputStreamReader = new InputStreamReader(getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			Field[] classFields = extractRowFields(classType);
			String row;
			while ((row = bufferedReader.readLine()) != null) {
				rows.add(splitRow(row, classFields));
			};
		} catch (Exception e) {
			throw new KommaException("Parsing Error", e);
		} finally {
			closeReader(bufferedReader);
			closeReader(inputStreamReader);
		}
		return rows;
	}

	private List<T> initializeList() {
		if (list == null) {
			return new LinkedList<T>();
		} else {
			return list;
		}
	}

	private InputStream getInputStream() throws UnsupportedEncodingException {
		if (inputStream == null) {
			 return new ByteArrayInputStream(str.getBytes("UTF-8"));
		} else {
			return inputStream;
		}
	}

	/**
	 * Closes the supplied {@code Reader}
	 *
	 * @param reader The {@code Reader} to close.
	 * @throws KommaException If an error is caught during closing.
	 */
	private void closeReader(final Reader reader) throws KommaException  {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				throw new KommaException("Error closing stream", e);
			}
		}
	}

	/**
	 * Splits a row at the {@code separator}.
	 *
	 * @param row One row of CSV data.
	 * @return The object the represents the row.
	 * @throws InstantiationException If an error is caught.
	 * @throws IllegalAccessException If an error is caught.
	 */
	private T splitRow(final String row, final Field[] classFields)
			throws InstantiationException, IllegalAccessException {
		final String[] fields = row.split(separator);
		return fillIn(fields, classFields);
	}

	/**
	 * Takes already split CSV values and inserts them into the row object
	 * {@code <T>}.
	 *
	 * @param fields The split CSV row.
	 * @return The row object <T>.
	 * @throws InstantiationException If an error is caught.
	 * @throws IllegalAccessException If an error is caught.
	 */
	private T fillIn(final String[] fields, final Field[] classFields) throws InstantiationException, IllegalAccessException {
		T row = classType.newInstance();
		for (Field classField : classFields) {
			populateField(classField, row, fields);
		}
		return row;
	}

	/**
	 * Retrieves the {@code KommaField} annotation.
	 *
	 * @param field The field to extract the annotation from.
	 * @return The {@code KommaField} annotation, or null if not found.
	 */
	private KommaField getFieldAnnotation(final Field field) {
		Annotation annotation = field.getAnnotation(KommaField.class);
		if (annotation instanceof KommaField) {
			return (KommaField) annotation;
		}
		return null;
	}

	/**
	 * Attempts to populate the field. First it tries to detect if there is an
	 * annotation, then it checks if the annotation is valid.
	 *
	 * @param classField The field to try and populate.
	 * @param row The row object that contains the field.
	 * @param fields The split CSV data.
	 * @throws IllegalArgumentException If an error is caught.
	 * @throws IllegalAccessException If an error is caught.
	 */
	private void populateField(final Field classField, final T row, final String[] fields) throws IllegalArgumentException, IllegalAccessException {
		KommaField fieldAnnotation = getFieldAnnotation(classField);
		if (fieldAnnotation != null && fieldAnnotation.value() >= 0) {
			int index = fieldAnnotation.value(); // get the index
			if (index < fields.length) {
				classField.setAccessible(true); // give me access!
				classField.set(row, fields[index]);
			}
		}
	}

	/**
	 * Gets all fields on the row object <T>.
	 *
	 * @param row The row object <T>.
	 * @return The fields found in {@code row}.
	 */
	private Field[] extractRowFields(final Class<T> classType) {
		return classType.getDeclaredFields();
	}

	public static class KommaBuilder<T> {
		private Class<T> classType;
		private InputStream inputStream;
		private String str;
		private char separator;
		private List<T> list;

		public static final char DEFAULT_SEPARATOR = ',';

		/**
		 * Constructor.
		 *
		 * @param classType The class type being used to represent a row of data in
		 * the CSV.
		 */
		public KommaBuilder(Class<T> classType, InputStream inputStream) {
			this.classType = classType;
			this.inputStream = inputStream;
			this.separator = DEFAULT_SEPARATOR;

			// protect ourselves and not allow null class
			if (this.classType == null) {
				throw new IllegalArgumentException("classType cannot be null");
			}
			// protect ourselves and not null streams
			if (this.inputStream == null) {
				throw new IllegalArgumentException("inputStream cannot be null");
			}
		}

		/**
		 * Constructor.
		 *
		 * @param classType The class type being used to represent a row of data in
		 * the CSV.
		 */
		public KommaBuilder(Class<T> classType, String str) {
			this.classType = classType;
			this.str = str;
			this.separator = DEFAULT_SEPARATOR;

			// protect ourselves and not allow null class
			if (this.classType == null) {
				throw new IllegalArgumentException("classType cannot be null");
			}
			// protect ourselves and not null streams
			if (this.str == null) {
				throw new IllegalArgumentException("str cannot be null");
			}
		}

		/**
		 * Sets the separator to be used when dissecting fields from a CSV row.
		 *
		 * @param separator The separator to use.
		 * @return The builder.
		 */
		public KommaBuilder<T> separator(char separator) {
			this.separator = separator;
			return this;
		}

		public KommaBuilder<T> list(List<T> list) {
			this.list = list;
			return this;
		}

		/**
		 * Builds the {@code Komma} object.
		 * @return The {@code Komma} object.
		 */
		public Komma<T> build() {
			return new Komma<T>(this);
		}

	}
}

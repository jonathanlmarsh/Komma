Komma
=====

CSV file parsing library uses annotations to map the CSV values to an object variable.

Usage
-----
Apply annotations to the object.

    public class Sample {
      @KommaField(index = 0)
      String valueOne;

      @KommaField(index = 1)
      String valueTwo;
      ...
    }

Then consume the CSV file.

    InputStream inputStream = ...
    
    KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, inputStream);
    Komma<Sample> komma = builder.build();
    List<Sample> data = komma.comsume();

Komma
=====

[![Build Status](https://travis-ci.org/jonathanlmarsh/Komma.svg?branch=master)](https://travis-ci.org/jonathanlmarsh/Komma)

CSV file parsing library uses annotations to map the CSV values to an object variable.

Usage
-----

Annotations take the following form:

    @KommaField({index})

Where `{index}` is the position of the value within the CSV row to populate into the variable.

Apply annotations to the object.

    public class Sample {
      @KommaField(0)
      String valueOne;

      @KommaField(1)
      String valueTwo;
      ...
    }

Then consume the CSV file.

    InputStream inputStream = ...

    KommaBuilder<Sample> builder = new KommaBuilder<Sample>(Sample.class, inputStream);
    Komma<Sample> komma = builder.build();
    List<Sample> data = komma.comsume();

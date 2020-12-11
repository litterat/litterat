
Litterat-XPL - Litterat eXtensible Presentation Language
------------------------------------------------


## Litterat XPL Features

Litterat XPL is a binary serialization format that is designed to be simple to use. Usage is very similar to the Java Serialization ObjectOutputStream and ObjectInputStream. XPL encodes the structure of the data types as part of the encoding using the Litterat Schema. This embedded schema ensures efficient encoding and allows readers to validate the schema prior to reading the data. A simple examples is as follows.

```.java
Point p1 = new Point(-37.2333f, 144.45f);

// Test writing out a Point.
byte[] buffer = new byte[150];
TypeOutputStream out = new TypeOutputStream(buffer);
out.writeObject(p1);
out.close();

TypeInputStream in = new TypeInputStream(buffer);
Point p2 = in.readObject();
```

Objects to be serialized are tagged using the Litterat-pep library. For example:

```.java
public class Point {

   private final int x;
   private final int y;
   
   @Data
   public Point(int x, int y) {
      this.x = x;
      this.y = y;
   }
   
   public getX() {
      return x;
   }
   
   public getY() {
      return y;
   }
}
```


## Dependencies

NOTE: Litterat-XPL is still under development and has not been published to Maven yet.

Litterat XPL is designed for Java 9+ and requires no other libraries.

Use the [Maven repository](https://mvnrepository.com/artifact/io.litterat/litterat) dependancy:


```.xml
<!-- https://mvnrepository.com/artifact/io.litterat/litterat-xpl -->
<dependency>
    <groupId>io.litterat</groupId>
    <artifactId>litterat-xpl</artifactId>
    <version>VERSION</version>
</dependency>
```

or for Gradle use:

```
// https://mvnrepository.com/artifact/io.litterat/litterat-xpl
implementation group: 'io.litterat', name: 'litterat-xpl', version: '0.5.0'
```

## License

Litterat-xpl is available under the Apache 2 License. Please see the LICENSE file for more information.


# Litterat #100DaysOfCode Challenge

Based on [www.100daysofcode.com](https://www.100daysofcode.com/) I'm taking the #100DaysOfCode challenge working on Litterat. This will act as a journal of progress. Litterat is a completely new Java serialization library designed to work with Java 11+ and play nicely with Java (i.e. not using unsafe or reading/writing directly to fields). 

## Day 8 - December 18 - Expanding and testing the array implementation

Spent a lot of time refactoring the PepArrayMapper MethodHandle based implementation. The array implementation has now
been tested with SimpleImmutable[], ArrayList<ArrayList<String>> and int[]. Combined they cover the main array based
data types. This can be expanded and testing can be expanded later without much effort. The PepMapMapper and PepArrayMapper shows that the array MethodHandle interface works well enough to continue.

Noticed that the introduction of the PepDataArrayClass as an extension of the PepDataClass ends up with some PepDataClass methods and information not being relevant to the PepDataArrayClass. Currently the toObject and toData MethodHandles don't seem to have any use. I'll need to explore if there's a use case for these, or if there should be a PepDataTupleClass so that less is in the base class.

Implementation and testing of the JsonMapper "just worked". Completed in a short time.

## Day 7 - December 17 - More on arrays...

Preparing List<String> test case and implementing the PepMapMapper. Hitting the issue early on that type erasure makes
it difficult to get hold of the fact that List<String> is a list of String. While Java erases the generic information
from the Class, it is available on [Parameter and Field](https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection) meta data. The PepContext interface was only designed with Class in mind, so it is going to need a redesign to allow using additional information when it is available.

Java reflection provides most of the information required by the PEP library in the Class object. It's only the situation where additional information is required from the ParameterizedType object. Expanding the interface to allow passing in additional Type information makes sense. Updated the PepContext lookup to use the Type instead of the Class to allow looking up parameterized type descriptors.

Refactored the PepMapMapper to deal better with arrays using the extra information. Tested List<List<String>> to
see if that would work correctly. Still more work required on arrays for PepArrayMapper, JSON and XPL.

## Day 6 - December 16 - Continuing to refactor arrays and lists

Another possible solution is to provide two interfaces for arrays and allow the underlying data structure to expose either of the interfaces. One interface based on index based accessor (e.g. an array) and the other based on an Iterator (e.g. a Set). From a client it would look like:

PepArrayClass arrayClass = (PepArrayClass) dataClass;

 // an int[] can't be cast to an Object[] so can't directly access values.
 Object arrayData = arrayClass.accessor().invoke(object);
 int length = arrayClass.length().invoke(arrayData);
 output.writeInt(length);
   
if (arrayClass.isIterator() ) {
  
   Object iterator = arrayClass.iterator().invoke(arrayData);
   for (int x=0; x<length; x++ ) {
      writer.invoke( arrayClass.get().invoke( arrayData, iterator) ); // writer accepts int, String, etc.
   }
} else {
   for (int x=0; x<length; x++ ) {
   		 writer.invoke( arrayClass.get().invoke( arrayData, x) ); // writer accepts int, String, etc.
   }
}

Based on the little gained (i.e. iterator not allocated for arrays), this original solution is more consistent. Will continue with that implementation.

### Collection constructors

Collections are also a problem because the serializer does not know the concrete class representation for the Collection ahead of time. A list has many implementations and some (e.g. unmodifiable list) requires the set to be created prior to construction.  It's not possible to select ArrayList for List for every field, as the implementation might be a different for different fields.

The solution needs to be that if a concrete class is provided (e.g. ArrayList) then use it, other wise fall back to a default implementation. The use also requires a way to override the default implementation. One possibility is to allow setting the implementation class on the @Field tag, or create another tag.  Another way to deal with this is to allow setting a specific bridge for the field. 

## Day 5 - December 15 - PEP Lists and Arrays

Exploring Lists and Arrays today. Currently PEP defines an interaction with Lists/Arrays using Object[]. The accessor for a field returns the Object[]. The constructor takes a count which returns an Object[]. The client code can then iterate over the array to write or read entries. This works, but isn't very efficient as it might allocate and throw away the Object[] as it creates a List<String> for instance. The other problem is that it requires autoboxing for int[] and more duplication. If int[] is the base case and Valhalla isn't available it requires method handles that will allow direct interaction with the Array or List. Reading/Writing an int[] might look as follows:

int[] intArray = create( length );
for (int x=0; x < length; x++ ) {
   add( intArray, in.readInt() );
}
return intArray;

int[] intArray = object.accessor();
for (int x=0; x< length; x++ ) {
   out.writeInt( get( intArray ) );
}

To fit within PEP, this should have the same set of MethodHandles as the List<String> which would be:

List<String> stringList = create( length );
for (int x=0; x<length(stringList); x++ ) {
   add( stringList, in.readString() );
}
return stringList;

List<String> stringList = object.accessor();
for (int x=0; x<length(stringList); x++ ) {
   out.writeString( get( stringList ) );
}



There's an implied Iterator concept being created in both of the above examples, but isn't referenced directly. The problem with using an Iterator interface is that it forces basic types to be autoboxed through the interface. One way would be to define a partner iterator. The interface could then be for int[]:

accessor(Object): int[]
iterator(int[]):IntIterator;
length(int[]):int
get(int[], IntIterator): int;
add(int[], IntIterator, int ): void;
constructor(int):int[];

This would work using MethodHandles equally for a List. For example List<String>.

accessor(): List<String>
iterator(List<String>):ListIterator;
length(List<String>):int
get(List<String>, ListIterator):String;
add(List<String>, ListIterator, String): void;
constructor(int): List<String>;

Implementation client for an array is then the same for both:

PepArrayClass arrayClass = (PepArrayClass) dataClass;
Object arrayData = arrayClass.accessor().invoke(object);
int length = arrayClass.length().invoke(arrayData);
output.writeInt(length);
Object iterator = arrayClass.iterator().invoke(arrayData);
for (int x=0; x<length; x++ ) {
   writer.invoke( arrayClass.get().invoke( arrayData, iterator) ); // writer accepts int, String, etc.
}

int length = in.readInt();
Object arrayData = arrayClass.contructor().invoke(length);
Object iterator = arrayClass.iterator.invoke(arrayData);
for (int x=0; x<length; x++ ) {
   arrayClass.add().invoke( iterator, in.read(array.type())); // in.read returns int, String, etc.
}
return v;

Part way through implementation. Not sure if this will work as expected, so may require changes to interface.

## Day 4 - December 14 - Add transport implementations.

Added implementations for byte[], streams and ByteBuffer transports. Nothing too complex.

__TODO__  There's still an issue with ByteBuffer transports throwing BufferOverflowException, BufferUnderflowException or ReadOnlyBufferException. For now these will be thrown to the caller. Not sure if these should be caught and re-thrown as IOException.

Thinking more about the roles of code, schema and data formats and how to separate the concepts and ensure they don't influence each other.
As an example an integer might represent a persons age. The setter:

```java
public void setAge(int age) {
   if (age < 0 || age > 150) {
   	 throw new IllegalArgumentException("invalid age");
   }
   this.age = age;
}
```

The schema is required to hold enough information to say if the data recorded in the data format is valid. It might record something like:

   field: { name:"age" type:"uint8" constraints: { min:"0", max:"150" }}
   
The data might be represented differently depending on the format. In JSON this would be a Number while it would be presented as binary in XPL. Where the integer was larger, for example, int32, it would could be presented as varint32, little endian or big endian. Another better example might be UUID which is converted to text in JSON and kept in binary for a binary file. 

Most schemas conflate the representation of the data in the schema because they are tied directly, however, it should be possible to keep these concepts separate. This way the representation can be decided later.


## Day 3 - December 13 - Added XPL test project

Added the litterat-xpl-test sub-project and created test cases which write and read the various PEP test classes. Implementation went incredibly smoothly and the PEP and Schema library did the job they were designed. A couple of fixes required in XPL for toData and toObject transformations.

Fixed up the TypeInputStream and TypeOutputStream constructors to take various combinations of byte[], streams and ByteBuffers. There's also the option to write custom transport implementations.

## Day 2 - December 12 - Optional tests

Not a lot of code done today. Added the ImmutableAtomTest to JSON serializer. Also added Optional<String> to the test case.

Responded to a message on the [Java Amber mailing list](https://mail.openjdk.java.net/pipermail/amber-dev/2020-December/006863.html). Still trying to work out which direction to take this project. There are many serialization formats and libraries available. For now Litterat is very conceptual project that still needs to evolve and find its feet.


## Day 1 - December 11 - Initial commit

This is the first day of the challenge, but there's been a lot of code written in the weeks prior. This is the first day of making the Litterat project public. Very rough around the edges and many things not yet working, but it's a start. Getting the PointTest working which writes and reads a Point class was the trigger to commit to github. It's very basic, but demonstrates the end-to-end project working.












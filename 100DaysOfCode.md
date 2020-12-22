
# Litterat #100DaysOfCode Challenge

Based on [www.100daysofcode.com](https://www.100daysofcode.com/) I'm taking the #100DaysOfCode challenge working on Litterat. This will act as a journal of progress. Litterat is a completely new Java serialization library designed to work with Java 11+ and play nicely with Java (i.e. not using unsafe or reading/writing directly to fields). 

Next steps list. A general list of things that could be done next in no particular order.

 - Deeper looker at defaults for Lists, Sets and other collections.
 - Possibly refactor PepDataClass to include PepDataTupleClass.
 - Implement the jvm-serializers performance test.
 - Investigate removing the primitive array bridge classes and replace with generated MethodHandles.
 - Review the schema language and look at schema annotations. 
 - Review schema arrays and look at multi-dimensional arrays as part of schema design.
 - Look at @Field name overrides and develop some rules/errors to ensure no name conflicts.
 - Field ordering for POJO.
 - Dates and timestamps.
 - Implement allowing public fields as record components.
 - Rename PEP to litterat bind.
 - Create PEP error examples and test edge cases.
 
 Documents to write:
 
 - Litterat PEP theory. Background and theory behind design.
 - Litterat PEP end user guide. User guide to using library & examples.
 - Litterat PEP serialization guide. For people writing serialization formats.
 
 

## Day 12 - December 22 - Atomic and Array type testing

Adding test cases for both all primitive types to PEP. This includes both atomic primitives and array primitives to make sure there's no edge cases. 

Found an edge case in the ImmutableFinder where long and double primitive data types take up two slots. The variable indexes in the byte code are not aligned with the parameter count. That was reasonably easy to fix.

The other issue that appeared is that the POJO parameter ordering is alpha based because order of methods or fields is not guaranteed in Java. There's an outstanding question on if field order is important for schemas or if this is a file format detail that should be annotated on the record/tuple. Not sure of the answer to this yet. Added to the 'next steps' list to explore later.

Implemented same tests for JSON reader and writer. There's still more work to be done on literat schema before implementing arrays on XPL.

Moved back to field ordering on POJOs and other records. Other serialization libraries have solved this using an annotation. Implementing a similar design as Jackson @JsonPropertyOrder which is easy enough to reorder. However, if any object has its fields reordered then the constructor should also have the fields reordered to match the field order. This needs to work for both Immutable, POJO and mixed classes. Implemented by performing ordering before building the PepDataComponent classes.

## Day 11 - December 21 - Build scripts and testing

Continued on today with working out the gradle build system to work nicely with testing and eclipse. All maven publishing has been disabled until more of the library is complete and tested. Both "gradle test" and "gradle eclipse" are both producing the correct output.

Adding gradlew gradle wrapper and configuring [BuildKite](https://buildkite.com) as a continuous integration pipeline. Also added jacoco coverage to the build. Not sure how or if it is possible to make coverage report visible in BuildKite.


## Day 10 - December 20 - Continue with Array bridges

Annoying Java library issue. Collections.unmodifiableList(x) returns a class that is private and can not be checked with instanceof. Documented [here](https://stackoverflow.com/questions/8364856/how-to-test-if-a-list-extends-object-is-an-unmodifablelist).

Implemented the UnmodifiableLinkedListTest which uses a bridge and bridge annotation. This was supposed to validate the need for toObject and toData on the PepDataArrayClass. However, the bridge is associated with the tuple of the class using the array. As such it didn't actually require calling toData/toObject on the array. This provides additional support for removing them from PepDataArrayClass.

The initial implementation of the UnmodifiableLinkedListBridge was converting from Object[] to Collection<?> to be as generic as possible. However, this requires that Object be defined in the Pep library. Object could be an Atom, Array, Record or Interface. For now, it's better that the user defines a more concrete class for the bridges.

Also noticed that the @Field annotation renaming won't work. It also requires deciding on precedence for if the annotation is added to multiple places and have different names. Most likely this should result in an error. Additional test cases required for these scenarios.

------

Spent many hours working on figuring out why Eclipse wouldn't execute all tests in a package. Resulted in a  bug being [raised on junit5 project](https://github.com/junit-team/junit5/issues/2500). Related to using similar package names in pep and pep.test modules. Also looked at getting tests working from gradle command line.


## Day 9 - December 19 - Array bridges

Noticed yesterday that the PepDataArrayClass has toObject and toData MethodHandles that needed to be reviewed. If you consider the idea that a class wants to include an UnmodifiableList using a LinkedList as the implementation for a class like:

```java
class ClassWithList {

  @Field( bridge=UnmodifiableLinkListBridge.class )
  private final List<String> list;

  @Data
  public ClassWithList( List<String> list ) {
     this.list = list;
  }

  public List<String> list() {
     return this.list;
  }

}
```

In this case the user defines a bridge like:

```java
class UnmodifiableLinkedListBridge implements DataBridge<String[],List<String>> {

  public String[] toData(List<String> list) {
     String[] dataArray = new String[list.size()];
     return list.toArray(dataArray);
  }
  
  public List<String> toObject( String[] data ) {
     List<String> list = new LinkedList();
  	for (int x=0; x< data.length; x++ ) {
  	   list.add( data[x] );
  	}
  	return Collections.unmodifiableList( list );
  }

}
```

There's likely many other use cases where a bridge for an array would be useful beyond this contrived example. This is a good example of where a bridge annotation is useful to specify the bridge required. Not sure if this should be part of the @Field annotation or its own annotation. For now, will try with adding to @Field annotation.

Got part way through the implementation for @Field bridge annotation. There's an issue with the field meta data on whether to keep both the data type and the actual type or just the data type information. To some degree the user doesn't care what the actual type is as it is the data type that is being interfaced. As long as the toData and toObject are being called correctly. It may also be possible to simplify other exceptional cases like Optional and List using a bridge rather than special cases in the resolver.

## Day 8 - December 18 - Expanding and testing the array implementation

Spent a lot of time refactoring the PepArrayMapper MethodHandle based implementation. The array implementation has now been tested with SimpleImmutable[], ArrayList<ArrayList<String>> and int[]. Combined they cover the main array based data types. This can be expanded and testing can be expanded later without much effort. The PepMapMapper and PepArrayMapper shows that the array MethodHandle interface works well enough to continue.

Noticed that the introduction of the PepDataArrayClass as an extension of the PepDataClass ends up with some PepDataClass methods and information not being relevant to the PepDataArrayClass. Currently the toObject and toData MethodHandles don't seem to have any use. I'll need to explore if there's a use case for these, or if there should be a PepDataTupleClass so that less is in the base class.

Implementation and testing of the JsonMapper "just worked". Completed in a short time. One interesting element of
JSON is that the tokenizer doesn't look ahead. The current interface to array creation is based on knowing the size prior to reading as that is required to allocate an array. The JSON reader currently needs to read the values and then create the array when converting toObject. A redesign of the array toObject interface would be required to try and improve performance.



## Day 7 - December 17 - More on arrays...

Preparing List<String> test case and implementing the PepMapMapper. Hitting the issue early on that type erasure makes it difficult to get hold of the fact that List<String> is a list of String. While Java erases the generic information from the Class, it is available on [Parameter and Field](https://stackoverflow.com/questions/1901164/get-type-of-a-generic-parameter-in-java-with-reflection) meta data. The PepContext interface was only designed with Class in mind, so it is going to need a redesign to allow using additional information when it is available.

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












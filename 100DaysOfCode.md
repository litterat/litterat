
# Litterat #100DaysOfCode Challenge

Based on [www.100daysofcode.com](https://www.100daysofcode.com/) I'm taking the #100DaysOfCode challenge working on Litterat. This will act as a journal of progress. Litterat is a completely new Java serialization library designed to work with Java 11+ and play nicely with Java (i.e. not using unsafe or reading/writing directly to fields). 

Next steps list. A general list of things that could be done next in no particular order.

PEP
 - Deeper looker at defaults for Lists, Sets and other collections.
 - Possibly refactor PepDataClass to include PepDataTupleClass.
 - Investigate removing the primitive array bridge classes and replace with generated MethodHandles.
 - Look at @Field name overrides and develop some rules/errors to ensure no name conflicts.
 - Dates and timestamps.
 - Create PEP error examples and test edge cases.
 - Investigate Union class type.
 - Rename categories as Record, Union and Array.
 
 Schema
 - Review the schema language and look at schema annotations. 
 - Review schema arrays and look at multi-dimensional arrays as part of schema design.
 
 Other
 - Implement the jvm-serializers performance test.
 
 
 Documents to write:
 
 - Litterat PEP end user guide. User guide to using library & examples.
 - Litterat PEP serialization guide. For people writing serialization formats.

## Day 31 - February 27 - Model changes

Refactored the litterat-model moving the types into the main package and writing some documentation. Got reaquainted with the litterat.io website and deployed a few changes via cloudflare.

## Day 30 - February 26 - Back to it.

After a month off, time to make some more progress. Renamed litterat-pep to litterat-bind. Renamed litterat-schema to litterat-model. This is a better reflection of the theory that was previously explored. The litterat-bind is the language binding and is how the model is implemented and realised in the language. The litterat-model is the abstract data model. At some point the model could be expressed as a schema syntax; and should be able to be expressed in different schema syntax such as JSON Schema or XML Schema.

## Day 29 - January 27 - More editing

Not a lot done today. A few edits and drafting an email to describe the work that has been done on theory.

## Day 28 - January 26 - What's missing

After reviewing the theory document, it is clear that the macros and atomic types sections needs more work. In particular the macros section needs to be thought through more. Macros may help with the definition of the meta types and atomic type definitions so its worth exploring the concept further.

## Day 27 - January 25 - More editing

Spent the day ~~being very productive~~ mostly procrastinating. :) Reviewing the atomic type restrictions and trying to write the dates and timestamps section.

## Day 26 - January 24 - First draft

Reading back over theory document. Feels like it could easily expand into a book by researching every small component. There's lots of gaps and would be nice to discuss how the model fits with existing schemas in detail, but that seems like overkill for now. Can easily go back and fill in gaps over time. Added section on macros and namespaces. Still need to write up sections on dates and timestamps.

## Day 25 - January 23 - More rewriting

Further rewriting and editing on the theory document. It will be nice when a first draft is done. :)
Today was brought to you by [triple j hottest 100](https://en.wikipedia.org/wiki/Triple_J_Hottest_100,_2020) and some late night listening to [Tame Impala](https://en.wikipedia.org/wiki/Tame_Impala) - [Slow Rush](https://en.wikipedia.org/wiki/The_Slow_Rush).

## Day 24 - January 22 - Slow progress on theory

More editing for the theory document. Worked on product types and a start on sum types. The quality and detail doesn't feel high enough, but will focus on good enough. One outcome from today is that litterat-schema should probably be renamed litterat-model. A schema should have syntax and be a physical representation of the model.

## Day 23 - January 21 - More editing

Made some good progress on the theory document introduction. A lot more work to do on the details. A lot more rewriting than I'd like, but nice to get the concepts written.

## Day 22 - January 19 - Consolidation

A lot of thinking about what's next. The investigation into ASN.1 and Algebraic Data Types rounds out the theory aspect of Litterat. However, the litterat-theory document is currently a big jumbled list of thoughts. Next step is some editing and fill in the gaps for the theory document. 

Fell down the Wikipedia hole again looking at various pages. Finding the information presented is not well presented or clear and needs a lot of work. Not a tangent I'm willing to tackle right now. More focus on editing tomorrow.

## Day 21 - January 18 - More on abstract syntax and ASN.1

Spent the day doing more research on ASN.1 and Algebraic Data Types. Can now reduce all data schema design down to the core concepts of Product Type (Record, struct, etc), Sum Type (Union, Choice, OneOf, etc), Arrays (Repeated elements, etc), and Atoms. Everything also goes back to the concept of an "Abstract Syntax" which has a basis in BNF. There's still a lot of work to explore each of the concepts, however, the direction of building an "Abstract Model" by defining abstract data types makes more sense. It might then be possible to import and convert between different schemas.

## Day 20 - January 17 - More on Atoms and Algebraic Data Types

Digging deeper into atoms like real, string and blobs. Spent some time off on a tangent looking at history and background behind Backus Naur Form to see if I can find any underlying theory for "or". There isn't much, but [Tagged Union](https://en.wikipedia.org/wiki/Tagged_union) is closest. But it can be said that the concept of a record breaks BNF "and" and distinct order of elements. An array replaces the * or repeated element concept of BNF. Also looking into Algebraic Data Types which include sum and product types which closely relate to Union and Records.

Interesting reading on [Algebraic Data Types](https://en.wikipedia.org/wiki/Algebraic_data_type) which state "Algebraic data types are highly suited to implementing abstract syntax". The page on [Abstract Syntax](https://en.wikipedia.org/wiki/Abstract_syntax) directly links to [ASN.1](https://en.wikipedia.org/wiki/ASN.1). The ASN.1 has similar goals to Litterat..

   "ASN.1 is visually similar to Augmented Backus-Naur form (ABNF), which is used to define many Internet 
   protocols like HTTP and SMTP. However, in practice they are quite different: ASN.1 defines a data 
   structure, which can be encoded in various ways (e.g. JSON, XML, binary). ABNF, on the other hand, defines 
   the encoding ("syntax") at the same time it defines the data structure ("semantics"). ABNF tends to be 
   used more frequently for defining textual, human-readable protocols, and generally is not used to define 
   type-length-value encodings."
   
Narrowing in on the concept of defining abstract data types for records (as Product Type) and Tagged Union (as Sum Type) and building out definitions from there.  Also need to define the interactions between received data/schemas and how it joins with receiver schemas to perform validation.

## Day 19 - January 16 - Records

Exploring the concepts of required, optional and default values for record fields. It might be that required, optional and default are all concepts that should be annotations that are kept localised rather than details that might be transferred as part of the meta data. Also started looking at numbers for atoms. 
 
## Day 18 - January 13 - Arrays and Tuples

More documenting litterat-theory rather than writing code. Added definitions for tuples and arrays and explored some of the concepts allowed in JSON Schema arrays. Coming to the conclusion that JSON Schema arrays are very powerful but are an outcome of starting with JavaScript and JSON first and building a schema that suits them rather than starting from a theoretical starting point. It also raises some good questions about how far a schema should implement validation rather than templating.

## Day 17 - January 12 - More theory

Spent the day continuing with the litterat-theory document. Wrote the section on Litterat design requirements. Started working on sections for choice and arrays. The section on arrays has turned out a lot more complex than originally expected. This will require some more thinking about the difference between a tuple and an array. More work is required on naming and definitions for the various types of data structures. There a lot more inconsistencies between various schema languages than expected. Currently mainly looking at XML Schema, JSON Schema, Protobufs and Avro.

## Day 16 - January 11 - Back to work 

After a couple of weeks away from the computer, it's good to be back hitting my head against
the problems started in December. Spent the day writing the Litterat schema theory document.
Lots of background reading on Lambda calculus, Context-free grammars, XML Schema and Backus-Naur form. A lot of the background written, but yet to attack the core elements of the design (tuples and arrays). 
 
## Day 15 - December 29 - CLDR

More research into date formats in particular for text based presentation. The [Unicode CLDR](http://cldr.unicode.org/translation/date-time-1/date-time-patterns) is a good place to start. It also looks like the Java 8 java.time date formatting is also [closer to CLDR](https://www.infoq.com/news/2017/02/java9-cldr-ldml/) but not exactly the same. It is also important to note that date formats for common interchange is different to localization. Localization should be left to the visualization and user interface domain rather than be coded into the data interchange domain. Having said that there will always be exceptions. It looks like reference to relevant parts of the Unicode CLDR is a good way to proceed with data formats in general.
 
## Day 14 - December 28 - Annotations

Continuing to think about the relationship of formating meta data and its relationship with the core schema data. For example, a date might be stored internally as a Date object. When serialized to a binary format it might be serialized as a long (milliseconds since epoch), while in JSON there might be a requirement to store it as an ISO string. It may not be good enough to specify a single text and binary format with multiple text formats and multiple binary formats being required depending on the target data. However, there's probably a default text and binary representation. Based on the requirement for dates, the core meta data associated with a date isn't defined by the format. It is that it is a Gregorian date with potential restrictions on range.

Also thinking about how to store the above formatting meta information. Given that the format of data is not an essential part of the meta data, it seems to fit that the format information is really an annotation on the meta data. From a code first point of view the annotation it would be useful to present these annotations through the PepDataClass. By adding an annotation on the annotation it is possible to filter and collect any required annotations through to the interface.

## Day 13 - December 23 - Other Atomic types in Java. Dates, timestamps, etc

In dealing with the separation between serialization and PEP, the question of where do atomic types format get specified. Types like UUID have a reasonably standard String format, but also have a binary format. Dates might also have a different format depending on data format. In regards to PEP, it is only important to know that a type is atomic and if there's a conversion. Any format decisions are left to another library. To that end, the first step is to identify all atomic types in the standard java library.

Not a lot of time today, so added a Date test case for PEP. This just confirms that Date objects are atoms in the context of PEP. More effort required to classify java.time and other objects.

## Day 12 - December 22 - Atomic and Array type testing

Adding test cases for both all primitive types to PEP. This includes both atomic primitives and array primitives to make sure there's no edge cases. 

Found an edge case in the ImmutableFinder where long and double primitive data types take up two slots. The variable indexes in the byte code are not aligned with the parameter count. That was reasonably easy to fix.

The other issue that appeared is that the POJO parameter ordering is alpha based because order of methods or fields is not guaranteed in Java. There's an outstanding question on if field order is important for schemas or if this is a file format detail that should be annotated on the record/tuple. Not sure of the answer to this yet. Added to the 'next steps' list to explore later.

Implemented same tests for JSON reader and writer. There's still more work to be done on literat schema before implementing arrays on XPL.

Moved back to field ordering on POJOs and other records. Other serialization libraries have solved this using an annotation. Implementing a similar design as Jackson @JsonPropertyOrder which is easy enough to reorder. However, if any object has its fields reordered then the constructor should also have the fields reordered to match the field order. This needs to work for both Immutable, POJO and mixed classes. Implemented by performing ordering before building the PepDataComponent classes. Added test cases for both immutable and mixed classes.

Finished off with detecting public fields to allow them to be part of a record. Added detection to the GetSetFinder. Required refactoring to use MethodHandles for getters/setters in ComponentInfo class.


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

```
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
```

Based on the little gained (i.e. iterator not allocated for arrays), this original solution is more consistent. Will continue with that implementation.

### Collection constructors

Collections are also a problem because the serializer does not know the concrete class representation for the Collection ahead of time. A list has many implementations and some (e.g. unmodifiable list) requires the set to be created prior to construction.  It's not possible to select ArrayList for List for every field, as the implementation might be a different for different fields.

The solution needs to be that if a concrete class is provided (e.g. ArrayList) then use it, other wise fall back to a default implementation. The use also requires a way to override the default implementation. One possibility is to allow setting the implementation class on the @Field tag, or create another tag.  Another way to deal with this is to allow setting a specific bridge for the field. 

## Day 5 - December 15 - PEP Lists and Arrays

Exploring Lists and Arrays today. Currently PEP defines an interaction with Lists/Arrays using Object[]. The accessor for a field returns the Object[]. The constructor takes a count which returns an Object[]. The client code can then iterate over the array to write or read entries. This works, but isn't very efficient as it might allocate and throw away the Object[] as it creates a List<String> for instance. The other problem is that it requires autoboxing for int[] and more duplication. If int[] is the base case and Valhalla isn't available it requires method handles that will allow direct interaction with the Array or List. Reading/Writing an int[] might look as follows:

```
int[] intArray = create( length );
for (int x=0; x < length; x++ ) {
   add( intArray, in.readInt() );
}
return intArray;

int[] intArray = object.accessor();
for (int x=0; x< length; x++ ) {
   out.writeInt( get( intArray ) );
}
```

To fit within PEP, this should have the same set of MethodHandles as the List<String> which would be:

```
List<String> stringList = create( length );
for (int x=0; x<length(stringList); x++ ) {
   add( stringList, in.readString() );
}
return stringList;

List<String> stringList = object.accessor();
for (int x=0; x<length(stringList); x++ ) {
   out.writeString( get( stringList ) );
}
```


There's an implied Iterator concept being created in both of the above examples, but isn't referenced directly. The problem with using an Iterator interface is that it forces basic types to be autoboxed through the interface. One way would be to define a partner iterator. The interface could then be for int[]:

```
accessor(Object): int[]
iterator(int[]):IntIterator;
length(int[]):int
get(int[], IntIterator): int;
add(int[], IntIterator, int ): void;
constructor(int):int[];
```

This would work using MethodHandles equally for a List. For example List<String>.

```
accessor(): List<String>
iterator(List<String>):ListIterator;
length(List<String>):int
get(List<String>, ListIterator):String;
add(List<String>, ListIterator, String): void;
constructor(int): List<String>;
```

Implementation client for an array is then the same for both:

```
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
```

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












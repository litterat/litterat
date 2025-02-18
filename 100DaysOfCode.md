
# Litterat #100DaysOfCode Challenge

Based on [www.100daysofcode.com](https://www.100daysofcode.com/) I'm taking the #100DaysOfCode challenge working on Litterat. This will act as a journal of progress. Litterat is a completely new Java serialization library designed to work with Java 11+ and play nicely with Java (i.e. not using unsafe or reading/writing directly to fields). 

Immediate issues
 - Revisit DataClass and data projections.
 - Record projections and if the projected class is listed as a DataClass
 - Field projections
 - ToData interface. Not particularly nice or flexible.
 - Interfaces instead of MethodHandles?
 - Re-look at array binding. Allow user defined wrappers.


Next steps list. A general list of things that could be done next in no particular order.

 litterat-core
 - Investigate if default values for fields belong in this library.
 - Dates, timestamps and other atoms. To be completed as needed.
 - Create bind error examples and test edge cases. To be completed as issues found.
 - Review model arrays and look at multi-dimensional arrays as part of schema design.
 - Investigate how Annotations might work.
 
 litterat-xpl
 - Review the lang package and SchemaResolver to match more correctly to the model.
 
 Other
 - Implement the jvm-serializers performance test.
 - Investigate how litterat could be used in a web service such as Helidon.
 - Implement a YAML front end.
 
 Documents to write:
 - Litterat bind end user guide. User guide to using library & examples.
 - Litterat serialization guide. For people writing serialization formats.

## Day 78 - August 18 - DataClassReference

Started debugging by adding methods to register classes using Typename and Class after finding issues on day #77. Found that the Record definitions being generated did not correctly populate the fields; the field type was set to null. The reason it was null was because an Atom is a Definition, not an Element. The DataClass hierarchy is not matching the Element hierarchy and the Typename was not correctly represented.  Pulling further on this thread lead to the realisation that DataClassProxy was very similar to Typename so after much deliberation renamed it to DataClassReference.

Adding DataClassReference to the DataClass objects has implications for how Records are generated. In particular the descriptor now needs to distinguish if a record field member should be DataClassReference or bring the actual definition. e.g. DataClassArray or DataClassUnion being defined in the Record itself instead of through a defined Typename.

## Day 77 - August 16 - Debugging XPL

Finished the first pass at refactoring XPL and started debugging. First run hit the issue documented yesterday. When looking up the TypeContext/TypeLibrary with just a Typename, the type is not yet registered. As the Java class loader may not have even loaded the class, it is impossible to know about it. Pulling at this thread could require investigating class loaders and if we can find classes from a package name. Probably worth pre-registering the classes required for now manually and add to the todo list.

## Day 76 - August 15 - Refactoring XPL

Continued the XPL refactoring. Required changes to the core TypeContext interface to make it possible to lookup a DataClass based on a Typename. It may still be required to allow a Typename to find a Class and allow two way name binding. It is either that or require a package is pre-loaded so that all names are known. Pre-loading the packages has the advantage that from a security view point the developer is in charge of what classes get bound.

Also made changes to the TypeContext to require a Typename when registering an atom. Not sure if this is the best way, but will do for now. Atoms in general will need to be revisited for all parts of the library.

Still need to refactor the SchemaResolver and then ensure existing tests pass.

## Day 75 - August 11 - Implemented Proxy and DataClassProxy

Implemented Proxy and DataClassProxy classes and fixed up the implementations for ArrayMapper, MapMapper and JsonMapper. All tests for core and json now run successfully.

Spent a little bit of time looking at XPL. It will take a bit longer to refactor.

## Day 74 - August 10 - Refactoring JSON and XPL projects

Continuing the refactoring. Moved all the bind tests into the core-test library. All tests working except for the one that uses proxy which has changed. I'll need to return to this test soon. Also refactored all the json-test classes. In a similar way all tests pass except for the proxy record test.

Started to move across all the XPL classes, but this is going to need a lot more work to refactor. XPL was based on the idea that it interacts with the model instead of the bind library. As such there's quite a bit of interactions which are using the wrong interface. The initial XPL design was also prior to working out the core meta data elements. Now that it done it should be aligned with those elements and the various instructions be based on those core elements. 


## Day 73 - August 9 - Connecting DataClass to meta data

Spent more time merging the DataClass to the meta data. The main part of the solution is adding definition method to DataClass  that returns the meta data definition. An interesting of creating the DataClassProxy class is that it doesn't currently have a corresponding definition in meta data. The DataClassProxy is an artifact of the bind code environment and not related to the schema. Currently the definition will return null, but an aim of the library is to minimise nulls. It might be worth adding an ignored meta data Proxy class. Something for later after doing more testing with the proxy concept.

Got stuck with the gradle/eclipse issue that keeps putting modules on the classpath and need to be manually moved to the module path. I also found that gradle/eclipse continue to have issues with module based test running in the same project when reflection is required. As it is not possible to export the test data classes the classes are not available to the method handles public lookup. The issue of which lookup type will need to be revisited. End result of hitting my head against this wall for a while is to split the tests back into their own package. The issue of getting the dependent project modules onto the module path is still a problem.

## Day 72 - August 8 - Working out relationship between TypeLibrary and TypeContext

One of the difficult aspects of the refactor has been working out the relationship between the TypeLibrary that holds the type definitions and the TypeContext that holds the binding MethodHandles. Ultimately, the serialization code only deals with the TypeContext. However, for a schema first code, it needs to be possible to import a schema and then bind that to classes. Initially, I was thinking of merging the two concepts into one so that the data model also held the MethodHandles. This might still happen, however, for now to keep options open it feels better to keep them separate. 

The TypeContextClassBinder has the job of creating the definitions and bindings between the TypeLibrary and TypeContext. It's going to take a bit of time to work out the difference between creating a Definition versus creating a DataClass. When doing reflection on a class the same tasks need to occur but the output is different. It makes sense that the code for both of these is the same to reduce duplication and errors. For now each type (Atom,Record,Union,Array) have been split out into their own Binder classes. The old DefaultResolver grew into 1300 line class which is going to take a while to pick apart and repurpose for the dual job of both Definiton and DataClasses.

During the refactor, another interesting problem that had been bugging me have also been solved. Some of the DataClass objects have a toObject/toData MethodHandles which is required because some classes have a ToData interface. This interface is annoying because it isn't on all classes so much of the time it called the identity function. To resolve the issue, I've created a DataClassProxy type. This way the proxy requirement is separate from other types. This will add a little more complexity to the serialization classes, but is a more correct reflection of the actual code. There's still some interesting things to explore with regard to if the data class or the target class will always know which object is required. In particular, when you have a schema first code situation, the definition will bind to the data class. The data class will need to know about the target class to ensure that the correct type is produced during deserialization. During serialization it is a little easier because the target class will correctly produce the data class to be serialized.

Finished the day moving everything around and the general structure is there for the combined library. The links between TypeLibrary and TypeContext are in place with DefaultClassBinder. Still need to add actual connection from DataClasses to Model objects, for example DataClassRecord having either a Typename or direct Record instance. Also need to refactor each of the binders to produce both the DataClass and the Model object. Quite a bit of progress but still quite a bit to go...


## Day 71 - August 4 - Continuing the refactoring

Brought across the test data from the bind library. Trying to put the test data into the same library this time. Using three different src folders for main/test/data. Hopefully this will simplify testing and release later. I'll need to figure out the process to access the src/data classes from litterat-json and litterat-xpl later.

Having trouble working out the main interface name and package name. Currently using io.litterat.library.TypeLibrary. The plan is for this to contain both data models and the binding interfaces. In a code first scenario the classes are used to generate the model, then the model and class is used to generate the binding interfaces. In a schema first solution, the schema is loaded into the TypeLibrary. The model and classes are then used to generate the binding interfaces. I should be able to use a lot of the DefaultResolver code for both generating the models and the binding interfaces. The class just needs to be separated. Not ready to commit anything yet.


## Day 70 - August 3 - Refactoring

Started the refactor by creating litterat-core. Added annotation, bind, code, model and atom classes. The bind classes will need the most work as they are tightly coupled with the original DataClass bind classes. Before they can be refactored a decision needs to be made about the type library. Bringing the model and bind libraries together should in theory make this easier. The idea is that the with code first development, the analysis produces a type library (model) and from that a binding is reflected back on the classes.

## Day 69 - August 2 - Planning the refactor

Given the structural problems discovered in the last week. It's time to refactor. At this stage I think this will involve combining the model and bind libraries into one. I don't think it makes sense to use them separately. This will roughly involve:

 1. Prepare by committing and making sure current tests pass.
 2. Create litterat-core. 
 3. Move over the central parts of the model code.
 4. Create generation of model from test classes.
 5. Move over the bind library.
 6. Generate the bind code based on the model.
 7. Refactor the main interfaces.
 8. Move over the test code and other example code.
 
This is not going to be a particularly fun task, but I think its required before the project can move forward.

## Day 68 - August 1 - Stepping back and re-thinking

Developed a presentation slide deck to describe the concept of building Litterat based on first principles. In the process realised that the ordering between the bind library and the model is wrong. In a code first development process it should be:

 Write code -> Extract Model -> Share schema -> Bind model to code.
 
 In a schema first development process:
 
 Get Schema -> Read schema into Model -> Generate code -> Bind model to code.
 
The problem is that the current bind library is going from code to bind model. This is making it difficult to develop the model library in the right way. I've been feeling blocked on the implementation and I think this is part of the problem.

## Day 67 - July 22 - Writing tests for the model

Created the litterat-model-test library and implemented an initial test. The test highlights the need to implement the model to bind validation. I might need to further implement a basic JSON Schema reader to model and shift the json reader/writer to depend on the model. This continues to highlight the similarities between the bind data classes and model types. The underlying issue is that the reader/writers need the bind data classes but not the model. There needs to be a clearer distinction between when to use the model, bind classes and/or some combination of the two.

Added some very basic implementations for various atom types including integer, real, boolean and string. The atom types need a lot more work.

## Day 66 - July 17 - More model binding

Continued to document and refactor the Litterat model. I had shifted the Atom class into its own package, but moved it back as it is an unsealed union of future atom types. Makes more sense for the Definition to be a sealed union of element and atom.

Also implemented BigInteger for unsigned long values for input/output in XPL. Also added BigInteger as a default atom in the bind library, 


## Day 65 - July 13 - Removed the Reference

Looking at the Litterat model again and noticed that the Reference class was a wrapper around the TypeName. When looking again at Backus-Nuar form the name of the rule and the name of the type in the rule definition are the same thing. Making the TypeName implement Element simplifies the overall model.

When thinking about the binding between code and model, there's two distinct paths that need to be considered. A code first path requires that the model is generated and added to the model based on the structure of the code. In a schema first path, the schema is first imported into the model and then model is bound to the code. 

## Day 64 - June 16 - Head banging

Spent some more time banging my head against the model binder.

## Day 64 - June 14 - Code first model binding

Looking at refactoring the code first model binding code. Trying to simplify and make it easier to read. The main problem is the concept of the difference between when to create new type names. For instance, if you have a Record that includes an array. It is possible to define the array inline as part of the record, or create a new definition for the array give it a name. In the end, this will need to be the developers choice, so additional annotations may be required.

Pulled on the thread and found that the way TypeLibrary registers and binds classes will need to be refactored. The problem is that currently, the TypeLibrary will attempt to create new definitions for any class. From a security point of view that's not a great idea as you could load jars and potentially inject other unexpected data classes into a stream. It would be better that the developer is in control and initially binds the classes to the TypeLibrary.

## Day 63 - June 11 - A few updates

Removed references to PEP in most of the documentation and code. The concept of projected embedded pairs was the basis for the early implementation. The concepts are still there but don't really correctly describe that the design. It's also not something that needs to be exposed to developers.

Also updated gradlew and got buildkite running on the new machine correctly. Builds are green again!

## Day 62 - May 30 - Refactoring XPL array reading and writing

I had a test case for XPL that involved a List<String>. The original implementation for XPL only allowed reading/writing array classes. By passing down the DataClassArray I was able to use the correct technique to read/write the list. Noticed that both the dataClass -> model and the model -> xpl.lang required more work to align the models.

## Day 61 - May 29 - Back at it

Over a month of being busy with other things. Good to get back at it.

Finished off a month ago with an infinite resolution loop. Resolved by not resolving the members of unions during resolution. This requires making DataClassUnion hold its member types as Class instead of DataClass. 

## Day 60 - April 24 - Naming and namespaces

The concept of naming and namespaces can be very complex. It represents another conceptual domain that is being put together with the rest of the system. The naming is also very closely associated with versioning. My investigation into versioning showed that it was probably better to not use versioning at all as it complicates the solution. 

It would be nice to completely remove the concept of namespaces for now, but it is likely they will be needed. For now the namespace is a simple string. The other concept close to namespaces is specification files. A single specification file could relate to a single namespace or reference/import multiple other namespaces.

A few changes to TypeName tonight to remove versioning and attribute which shouldn't be needed. Better to strip things away until needed later for now.

## Day 59 - April 23 - Slim down the data model

A conclusion from yesterdays thinking is that attempting to merge domains such as how atoms are specified and how records/unions are defined is the wrong approach. Each domain has its own design and trying to use one to define the other is incorrect. For now the main addition to the design is:

    definition: union( [ element, atom ] );
    
This separates the two domains but combines the naming approach. Definition in this instance is the right hand side of the rule. It allows the investigation into the various atom types and definitions to occur later. An entry is thus:

    entry: record( [ field( "name", identifier, true ), 
                     field( "definition", definition, true ) ] );
                    
Spent some time moving around the classes in the model library. The definition classes are ok with atoms and functions moved to other packages. However, the other area that needs work is the namespace and naming concepts.


## Day 58 - April 22 - Continuing on the data model

One clear issue with the model currently is that Atoms and concepts like Envelope and Encoding don't fit the model very well. The concept of using an Atom as a record and then having a single union for atom_attributes isn't fitting nicely. Here's another attempt:

    atom: union( boolean_def, integer_def, real_def, string_def );
    integer_def: record( [ field( "attributes" , integer_attrs, true ) ] );
    integer_attrs: union( ... )
    
This starts to get complex pretty quickly. It seems to make sense to use a union at the atom level. However, delving into the individual concepts of an integer brings up lots of questions. Questions that have up until now been avoided. :) So possibly time to revisit those issues.

The issue is how close to the physical representation does/should the modeling language go. Most schemas provide a limited set of known types and do not allow the introduction of new types. ASN.1 provides a generalised concept of INTEGER, but does not specify the implementation. When you add min, max or enumerations to the list of requirements, it requires adding instance values which is somewhat recursive and may require the concept of macro expansion.

Working backwards using an example of an enumerated type and a min/max range:

    myEnum: enum( [ uint8:0, unint8:10 ] );
    myRange: range( uint8:0, uint8:10 );

Both of these examples have an assumption of knowing the type. This is where the concept of a lambda might become useful:

    myEnum: enumOfType( uint8 ).of( [ uint8:0, uint8:10 ] );
    
There a few issues being interwoven here.

 1. Definition of an atom or type of atoms. For instance integers, real and string are different types of atoms.
 2. Definition of a subset of a type. These are things like int8, int32, utf8-string, etc.
 3. Restrictions on atoms. This is where the user wants to restrict a range on a type.
 
Added to this is the fact that plenty of languages and systems don't offer restrictions like in8 or even have separations between integer or real. However, plenty of languages and systems to have clear separations, so it is not easy to pull apart cleanly. It is likely that no matter what design choice is made it will be wrong.

One initial answer is not to attempt to combine the solution for atom definitions with the solution for schemas. They seem to be different enough that providing different solutions for each would make some sense. This would allow the schema language to focus on structure, while the atom language can focus on types. It is also a nice way to defer the problem. This makes the following a complete set of base structures:

    definition: record( [ field( "name", type_name, true ), 
      			   	    field( "definition", element, true) ] );
    element: union( [record, array, union, type_name] );
    record: record( [ field( "fields", array( field ), true ) ] ); 
    array: record( [ field( "type", type_name, true ) ] ); 
    field: record( [ field( "name", identifier, true ), 
                  field( "type", type_name, true ), 
                  field( "required", boolean, true ) ] );
    union: record( [ field( "members", array( type_name ), true ) ] );
    type_name: identifier;
    identifier: string;
       
This design restricts fields to only use a type_name as the type. This is the same for unions. A possible relaxation of that rule would be:

    definition: record( [ field( "name", type_name, true ), 
      			   	    field( "definition", element, true) ] );
    element: union( [record, array, union, type_name] );
    record: record( [ field( "fields", array( field ), true ) ] ); 
    array: record( [ field( "type", element, true ) ] ); 
    field: record( [ field( "name", type_name, true ), 
                  field( "type", element, true ), 
                  field( "required", boolean, true ) ] );
    union: record( [ field( "members", array( element ), true ) ] );
    type_name: identifier;
    identifier: string;
    
Another possible change is to not reference type_name directly in the element union. Mixing records with atoms for Java is not easy, so adding the reference concept back in creates:

    definition: record( [ field( "name", type_name, true ), 
      			   	    field( "definition", element, true) ] );
    element: union( [record, array, union, reference] );
    record: record( [ field( "fields", array( field ), true ) ] ); 
    array: record( [ field( "type", element, true ) ] ); 
    field: record( [ field( "name", identifier, true ), 
                  field( "type", element, true ), 
                  field( "required", boolean, true ) ] );
    union: record( [ field( "members", array( element ), true ) ] );
    reference: record( [ field( "type", identifier, true ) ] );
    identifier: string;
    
This is probably a good design as a starting point that does not attempt to solve all the other problems relating to atoms for now. It aligns closely with the bind library and allows implementing the core capabilities. The only non-defined type is "string" which will be a fixed type for now.

## Day 57 - April 20 - Investigating the data model

The litterat data model is the internal representation of a schema. Ideally, this will be able to capture the concepts of multiple schema syntaxes. Using the concept of algebraic data types and the litterat theory this results in the concepts of Record, Field, Union, Array, and Atom as has been implemented in the bind library. However, the bind library connects these concepts with the Java language, the model connects them with schemas. Schemas add type names and namespaces as well as various concepts introduced by the Schema itself.

The existing model has unions for Definition and Element and these can get a bit confusing. Here's a first attempt at creating definitions for these using the litterat concepts.



    definition: record( [ field( "name", type_name, true ), 
      					 field( "definition", element, true) ] );
 
 This is a definition of the core concepts of element, record, array, field and union:
 
    element: union( [record, array, union] );
    record: record( [ field( "fields", array( field ), true ) ] ); 
    array: record( [ field( "type", type_name, true ) ] ); 
    field: record( [ field( "name", identifier, true ), 
                  field( "type", type_name, true ), 
                  field( "required", boolean, true ) ] );
    union: record( [ field( "members", array( type_name ), true ) ] );
                   
I think atoms need to be revisited; this is a mix of old and new concepts for atoms.

    atom: record( [ field( "attributes", array( atom_attributes ), true ) ] ); 
    atom_attributes: union( encoding, integer_attributes, float_attributes );
    integer_attributes: union( atom_integer, atom_bigendian, atom_littleendian, atom_signed, atom_unsigned ) 
    type_name: identifier; 
    identifier: string; 
    string: ## todo is this an atom or array?
        

## Day 56 - April 18 - Update to amber mailing list.

It seemed to be a good time to pause and review what has been done and update the amber mailing list. Spent the last few days drafting (a message)[https://mail.openjdk.java.net/pipermail/amber-dev/2021-April/007001.html]. 

The next phase is to return to the litterat.model and work on the binding between Java bind and the model. From there it should be possible to look at exporting to a schema.

## Day 55 - April 13 - Update to Java 16 and create Java record test

Started last night and completed in the morning. While the main library is based on Java 11, I've changed the target for testing to Java 16. This allows both in built records and sealed interfaces/classes to be tested. Added a record based test case, which didn't need any additional work other than checking is Class.isRecord is true as everything else works the same.

Added test case for sealed interfaces. For java 16 this requires that enable-preview is set before running. This will be cleared by Java 17 when sealed interfaces are final. Added sealed abstract example which worked without library modifications. Also added to test case a superfluous @Union annotation on abstract class which is ignored.

## Day 54 - April 12 - Break the Union/Record loop

Found a class resolution loop where a sealed Union will resolve included Record types which will in turn attempt to resolve the Union type. If a Record finds an interface or abstract base class with a Union record it will need to check if it explicitly includes Records and not attempt to resolve it if it does.

## Day 53 - April 11 - Renamed @Data to @Record

Took the step to rename @Data annotation to @Record. This is feeling like the right decision as it aligns with the Java 16+ record type. It is clear to me now how Union maps directly to sealed interfaces and forcing the use of @Union instead of reusing @Data for both makes it clearer. @Data was too ambiguous. Using @Record is clear and is really about making older classes behave the same way as a Java 16+ record class. By aligning and homogenising the old and new ways of defining data it creates a simple interface to interact with both.

The bind library is effectively feature complete. While the data annotation concept is still on the feature list, it isn't completely clear if that should be in this library or not. It might be better to attempt to implement this as part of the litterat-model library first and then see if it will fit back into the bind library later.

Renamed DataOrder to FieldOrder to better describe what it does.

Create a new test case for @Union with sealed types. Found an infinite loop where the resolver attempts to find the DataClass for the union members and the member records attempt to resolve the union data class. This will be a tricky one to resolve. Something for another night.

## Day 52 - April 10 - Embedded union filtering

Implemented the embedded union constructor and accessor filtering. This checks values against the expected data types allowed in a union. Also added the concept of sealed unions. This mimics the concept of sealed interfaces.
If a Union annotation is provided with a list of expected types then default behaviour is that no addition types can be added.

## Day 51 - April 9 - Implementing embedded union

Continued writing test case and implementation for embedded union. Currently reads/writes but performs no validation on reading or writing in the constructor or accessor method handles.

## Day 50 - April 8 - Decision time on embedded unions

For embedded unions, the initial implementation will be with Object as that aligns closest to current design. The other options can be added potentially later.

Created the Union annotation and implemented the resolver to pick it up. Did a few checks to ensure any member types are assignable to the class type used (e.g. Object). It is possible this mechanism could be abused. Will need to work that out in the future.

Also switched to using Union annotation on interfaces and abstract class test cases. Also thinking about renaming @Data annotation to @Record annotation. That would align better with Java 16+ record types. 

## Day 49 - April 7 - Anther look at embedded unions

Taking another look at a design for embedded unions. Ideally, Java would provide a union
type allowing a constructor to look like:

    MyClass( int | String identifier, int someValue );
    
This would enforce that an identifer can be an int or String type, but nothing else. Unfortunately, without that option there's a few paths worth exploring. The first is to use Object and add a Union annotation. Something like:
 
    MyClass( @Union( [ Integer.class | String.class ] ) Object identifier, int someValue );
     
Using an Object would require that int.class be promoted to Integer.class to be compatible with Object. Similar to "Field.required", it is now difficult to enforce that identifier will only be one of these class types. The developer is free to set identifier to any class and we're reliant on the developer doing the right thing. Writing can be checked prior to constructor and reading could also be checked. The other option is to split this into two fields:

    MyClass( @Field( name="identifier") Integer intIdent, @Field( name="identifier") String strIdent, int someValue );
    
This improves type safety and enforces that only the required types can be used. From a DataClassField this would still be represented as a single Object type. It requires some work to choose the correct constructor and accessor. For instance the accessor would need to be built up with MethodHandles to mimic:

    if (Objects.nonNull(o.intIdent()) 
       return o.intIdent();
    else
       return o.strIdent();

Another option is for the developer to provide a class which provides stronger type safety for a union type. There's various implementations of this covered on Stackoverflow, however (this one)[https://stackoverflow.com/questions/48143268/java-tagged-union-sum-types/48143514#48143514] is quite good. This would need further exploration on how to specify and annotate in a generic enough way to allow for different implementations.

## Day 48 - April 6 - Add support for isRequired to Field annotation and DataClassField

Another common issue with fields is the question of if they are required or not. By definition primitives are required and cannot be null, however, all classes derived from Object can be null. In the past there has been a non standard @NotNull annotation. There's debate on if the fact a field is required or not should be specified in the data specification, but for a Class implementation a field is either required or not. Java provides Objects.requireNonNull as a way to enforce non null values, but this isn't easily found using reflection. Adding 'isRequired' to the field annotation seems like the most straight forward solution. By default a field isRequired if it is a primitive and not required for Optional or Object based classes. 

One option is to enforce required fields by calling Objects.requireNonNull on each value before calling the constructor. This has the nice property that it will enforce data to align with what is specified. The problem is that this does not enforce the developer to play by the same rules. If the developer calls Objects.requireNonNull in the constructor then the call would be made twice. Unfortunately, without rewriting the bytecode of the constructor(s), this means that isRequired would only be a hint rather than an enforcement. The 'isRequired' does end up being a feature of many schemas so as a code first schema solution the hint is worth keeping. A possible solution for the future is to add an option to generate Objects.requireNonNull if an attribute (e.g. message) is set in the field. However, this doesn't quite feel right. It is more likely that if generating Java template code from a schema that the Objects.requireNonNull would end up being generated.

Setup some test cases to investigate the use of @Field annotations for both changing the name of a field and setting the isRequired flag. Its possible to set the @Field annotation on one of public/private fields, getter/setter or constructor parameter. It will require some more negative test cases in the future.

## Day 47 - April 5 - Complete implementation of isPresent for fields and primitive optional classes

Completed changing the interaction with fields using isPresent MethodHandle. Found that the XPL code is too reliant on null values and this will require a bigger refactor later. Focus now is to continue to get the bind library correct. 

After adding isPresent, I expected adding support for OptionalInt, OptionalLong and OptionalDouble wouldn't be too difficult. This worked without much trouble for accessing values. The isPresent proxied to the optional value and the accessor proxied correctly to the get method. The problem is on the other side, setting values and constructors. Optional are by design final and can not be mutated, so to some degree having a setter makes little sense and could potentially not be included. However, the constructor relies on a method handle with a parameter for each field. There's no such thing as not setting a primitive value as a parameter in a constructor, so options are either expose these Optional values or use nullable classes such as Integer and convert it to a OptionalInt. This poses a problem that the constructor would use Integer and the accessor would use int. Not a great solution. Unfortunately promoting the values to boxed versions is the best way forward for both accessor and constructor; OptionalInt <-> Integer, etc. This required some detailed MethodHandle work, and is working correctly. Given that these types are not likely to be used that often this seems like a good compromise.

Discovered some issues with mixing getters/setters with immutable constructors where fields are found twice.
Added additional test cases for OptionalPrimitives to test immutable, pojo and mixed fields.

## Day 46 - April 4 - Implement AbstractUnion test case

Implemented the abstract union test case. This required implementing the ability for ImmutableFinder to be able to find record parameters in super classes. ImmutableFinder and DefaultResolver are getting a bit unwieldy and could do with a rewrite at some point.

The embedded union is probably the final item worth implementing. The likely implementation will be based on using Object as the Java type which maps to a DataClassUnion in the field. A MethodHandle which checks the type and sets the correct value will need to be generated. The accessor will need to take the first non-null value it finds in the union.

Found another interesting edge case with the behaviour of OptionalInt, OptionalLong and OptionalDouble. These correctly avoid autoboxing and don't support orElse(null) or ofNullable(Integer). Optional is currently being treated as a nullable field while accessing/setting the value and MethodHandles are used to wrap/unwrap the value. However, this doesn't work with OptionalInt etc as boxing values to Integer would be required. One possiblity is to provide an isPresent or required/optional on every record field. This would change the way serialization needs to interact with all fields. The existing Mapper looks as follows:

```java
DataClassField[] fields = dataRecord.fields();
for (fieldIndex = 0; fieldIndex < dataRecord.fields().length; fieldIndex++) {
	DataClassField field = fields[fieldIndex];

	Object fv = field.accessor().invoke(data);

	// Recursively convert object to map.
	if (fv != null) {
		DataClass fieldDataClass = field.dataClass();
		fv = toMap(fieldDataClass, fv);
		map.put(field.name(), fv);
	}

	
}
```

This could be potentially changed to:

```java
DataClassField[] fields = dataRecord.fields();
for (fieldIndex = 0; fieldIndex < dataRecord.fields().length; fieldIndex++) {
	DataClassField field = fields[fieldIndex];

    if (field.isPresent(data)) {
		DataClass fieldDataClass = field.dataClass();
		Object fv = toMap(fieldDataClass, field.get(data));
		map.put(field.name(), fv);
    }
}
```

By removing the null check it means that the field is likely accessed twice for null fields. In the above case the boxing to Integer would still occur, but only in the client library. The isPresent could also be optimised to return true for primitive types.

## Day 45 - April 1 - Implement InterfaceUnion test case

Implemented the interface union test case and implementation for PepArrayMapper and PepMapMapper.

## Day 44 - March 31 - Test cases for abstract and interface unions

Implementation of examples and test cases for abstract and interface based unions. The embedded union continues to sit in the too hard basket.

## Day 43 - March 30 - Sort out github access

Finally got around to sorting out github access after turning on 2fa. Next up is to review tasks. Refactored the DataClass to remove isAtom type methods in favour of using instanceof. Stopped DataClassRecord from extending DataClassAtom.

## Day 42 - March 29 - Complete array refactor

Completed the array interface refactoring. This removes the need for individual bridge classes
in favour of using constructed MethodHandles. It's still not a perfect solution, but puts all the generation code in one place; of which is more complex.

## Day 41 - March 28 - Refactoring array MethodHandles

Removing the various ArrayBridge classes by using MethodHandles to generator the access patterns. Made a reasonable amount of progress. Able to generate MethodHandles for constructor, size, iterator and get. Still have put to do and update testing.

## Day 40 - March 22 - Refactoring bind classes

Refactored DataClassComponent to be DataClassField and added a DataClassAtom class. The classes are now aligning better with the theoretical model. There's more follow on changes that will be required as the union type is better integrated. More work is required on the difference between the Java type class and the data class, as it is not always clear which is which. The naming and which one fields reference needs to be clearer.

## Day 39 - March 21 - Clarity on union problem

Finally coming to a landing on how to treat the mismatch between Java parent classes mapping to Records and Unions. Given the example of a Vehicle which has two subclasses Car and Truck, then if there's a List<Vehicle> then serialization should ignore Car and Truck object types and just serialize Vehicle as a record. This is most likely not what the programmer will want, but it won't take long to realise that Vehicle should be an interface rather than a base class. The concrete example is:

```java
class Vehicle {
   private final String make;
   private final String model;
   private final int year;
   
   ... constructor and getters ...
}

class Car extends Vehicle {
   private final int horsePower;
}

class Truck extends Vehicle {
   private final int numberOfAxles;
}

class Manifest {
   private final Date manifestDate;
   private final List<Vehicle> vehicles;
   
   ... constructor and getters ...
}
```

When serializing Manifest, because vehicles is a List<Vehicle> and Vehicle is a Record type then the solution is to not write out hoursePower or numberOfAxles from Car and Truck types. This will result in a schema like:

    vehicle: record( string make, string model, int year );
    manifest: record( date manifestDate, vehicles array( vehicle ) );

During Class binding when Car or Truck are found a warning should be output as the extend another Record type and have no interface or abstract parent class.

The other issue of an embedded union still has no great solution if/until Java was to create an actual Union type. After searching on how JAXB deals with this, I found [this](https://www.eclipse.org/eclipselink/documentation/2.5/moxy/special_schema_types002.htm) example where a union of String and Decimal used an Object type. This is probably the best short term option. This is a lower priority capability for now.

Another task on the list was 'Decide if DataClassComponent needs a boolean isRequired attribute'. Given the litterat model Field object has this attribute it isn't needed in the bind library. The bind library is the mapping to the language and the Java language doesn't have this concept so it should not be included.

## Day 38 - March 20 - Back to Amber

Captured some of the problems and [went back](https://mail.openjdk.java.net/pipermail/amber-dev/2021-March/006971.html) to the Java Amber dev mailing list. The main question I'd like answered is if there will be a union type in the future. Writing the post has helped to get some of my thoughts in order.

## Day 37 - March 17 - More research into union mismatch

Listened to the [Inside Java podcast episode 14](https://inside.java/2021/03/08/podcast-014/) where Julia Boes and Chris Hegarty discussed Java record serialization. This got me to take another look at records and how they map to the theory. An important point about records is that they are final and cannot be extended. This means the Vehicle example below is not possible to model with Records without introducing an interface. The other point is that the [Sealed classes JEP360](https://openjdk.java.net/jeps/360) discusses using sealed interface with records. This is a great example of a union with different record objects. The example from the JEP:

```java
public sealed interface Expr
    permits ConstantExpr, PlusExpr, TimesExpr, NegExpr {...}

public record ConstantExpr(int i)       implements Expr {...}
public record PlusExpr(Expr a, Expr b)  implements Expr {...}
public record TimesExpr(Expr a, Expr b) implements Expr {...}
public record NegExpr(Expr e)           implements Expr {...}
```

Another nice feature that will be interesting for serialization is currently a [draft JEP for frozen arrays](http://openjdk.java.net/jeps/8261099). Combined with Records and sealed interfaces it allows the creation of a fully 'final' object graph.

This still doesn't solve the Vehicle example. If this problem was written with records it starts with:

```java
public record Vehicle(String make, String model, int year);
public record Manifest(Date manifestDate, Vehicle[] vehicles);
```

which translates to the schema:

    vehicle: record( string make, string model, int year );
    manifest: record( date manifestDate, vehicles array( vehicle ) );

After introducing additional Vehicle types:

```
public sealed interface Vehicle permits GenericVehicle, Car, Truck {..}
public record GenericVehicle(String make, String model, int year);
public record Car(String make, String model, int year, int doors, int horsePower);
public record Truck(String make, String model, int year, int wheels, int numberOfAxles);
public record Manifest(Date manifestDate, Vehicle[] vehicles);
```

Lets assume that there's a big code base and it is easier to change the name of the record Vehicle to GenericVehicle than it is to give a new interface name.

The above translates to the schema:

    vehicle: union( genericVehicle, car, truck );
    genericVehicle: record( string make, string model, int year );
    car: record( string make, string model, int year, int doors, int horsePower );
    truck: record( string make, string model, int year, int wheels, int numberOfAxles );
    manifest: record( date manifestDate, vehicles array( vehicle ) );
    
This all works, however, the problem now is that data written with the original code needs to be able to read by the new code. There needs to be some way to tell the genericVehicle to alias the original vehicle type. The simple answer is to add another rule for vehicle:

    vehicle: genericVehicle;
    
Reading old data simply matches against the old rule and new data can match against the union. This solution gives more weight to the idea of just not allowing extends for data classes.


## Day 36 - March 15 - Union base classes

Another interesting example of unions not mapping well to Java is base classes. Take for example:

```java
class Vehicle {
   private final String make;
   private final String model;
   private final int year;
   
   ... constructor and getters ...
}

class Manifest {
   private final Date manifestDate;
   private final List<Vehicle> vehicles;
   
   ... constructor and getters ...
}
```

We can this this maps to a grammar something like:

    vehicle: record( string make, string model, int year );
    manifest: record( date manifestDate, vehicles array( vehicle ) );
  
However, if a developer chooses to extend the vehicle class and create subclasses like Car and Truck you end up with:

```java
class Car extends Vehicle {
   private final int doors;
   private final int horsePower;
}

class Truck extends Vehicle {
   private final int wheels;
   private final int numberOfAxles;
}
```

If Vehicle is not abstract or doesn't protect the constructor, it means all three classes of Vehicle, Car and Truck can now
be used. When it comes to mapping to a data model, Vehicle is now both a record and union. Using a grammar notation you might have:

    vehicle: union( record( string make, string model, int year), car, truck );
    car: record( string make, string model, int year, int doors, int horsePower );
    truck: record( string make, string model, int year, int wheels, int numberOfAxles );
    manifest: record( date manifestDate, vehicles array( vehicle ) );
  
It's tempting to introduce an "extends" concept to remove the repeated elements. It's also an interesting idea to split the
vehicle into a union and define another entry for something like vehicle_base. There's no exact match so something has to change in the theory or to throw an exception when this situation is encountered. 

These problems brings back some subtle differences between serialization and data interchange. Serialization is about extracting data from an existing object graph, while data interchange is about sharing and agreeing the structure of data. There's a lot of overlap and ideally the two converge, however, in situations like the above a choice needs to be made about which is more important.

Combined with the concept of embedded unions, it shows a few ways in which object oriented designs don't translate well to the simplistic model of records and unions currently defined by the theory. In the case of embedded unions they don't translate well to Java classes, while, base classes do not translate well from Java classes to theory. 
  

## Day 35 - March 10 - Anonymous/Embedded unions

For embedded unions a record type will contain only one value of two possible types as well as other record field values. This could be represented as:

    my_record: record( fieldA int, fieldB union( int | string ) );
 
In this made up syntax the type my_record has two fields. fieldA is and int and fieldB is either an int or a string. This could be represented in Java (or other class based system) as:
 
 
 ```java
 class MyRecord {
 
   int fieldA;
   int fieldBInt;
   String fieldBString;
 }
 ```
 
When creating a MethodHandle constructor or getter for the DataClassRecord, what's the preferred constructor parameters and what special considerations does an implementer need to perform to call the constructor?

    option 1: constructor( int fieldA, int fieldBInt, String fieldBString );
  
For the first option the caller must check each field and if a union is found expand the types.

    option 2: constructor( int fieldA, int fieldBInt )
            constructor( int fieldA, String fieldBString )
            
For the second option the caller must find the correct constructor in a list. The number of constructors would multiply for each union type provided in the record structure.

    option 3: constructor( int fieldA, Object/? fieldB )
  
For the third option, an object is specified and potential autoboxing is required. It also adds to the complexity of the constructor to correctly set the right field. It is worth checking if the MethodHandle signature polymorphism allows fieldB to be either an int or String without autoboxing.

The third option is preferred as it is closer to the in data representation and aligns better with the DataClassComponent. It does mean that DataClassUnion would be used for Interfaces and Abstract parent classes and require another DataUnion type to represent anonymous unions.

The other issue is how to implement a getter. Even if its possible to return an int or String from a synthetic getter the value can not be assigned to a variable. The value could be passed directly to another MethodHandle as an input.

Researching Union type and found the [serde.rs](https://serde.rs/) serialization framework for Rust. A good example of mapping a union type to JSON with options of "Externaly tagged","Internally tagged", "Adjacently tagged", and "Untagged" as ways to represent a union. Four different ways to map shows that there's a mismatch between the type systems ability to represent the concept. It's also interesting that serde.rs uses "Enum" as the keyword for a union type. There's a lot of mismatch between keywords and their meaning between languages and protocols.



## Day 34 - March 8 - Exploring union type

DataClassUnion represents a union type and is one of the more difficult concepts to map between data and object oriented models. A union type can be represented as either an interface, abstract class, or an embedded union. An embedded union is where a class has two or more fields where only one value can be present. Embedded unions complicate the concept of Record constructors and requires a more thought going into how to set/get the union value through a projected embedded pair type interface.

## Day 34 - March 4 - More work on the DataClass

Need to be more careful with casting the DataClass after separating out the DataClassRecord.

## Day 33 - March 3 - Refactoring the PepDataClass

Refactored the PepDataClass to DataClass and separated the DataClassRecord, DataClassArray and DataClassUnion. This is aligning better with the Litterat theory. The union type is going to be more difficult to implement.

## Day 32 - March 2 - More refactoring

Refactored the class names in litterat-bind to fit the naming model better. Starting investigating how union types might fit within Java.

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












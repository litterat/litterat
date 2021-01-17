

# Litterat Theory

This is a collection of notes on theory behind Litterat and provides a skeleton for design and architecture for the solution.

## The problem

Data interchange formats and presentation protocols conflate many problems into a single solution. This gives rise to fads of particular formats coming in and out of favour or suitable for one system, but not another. Fads have included CORBA, XML, JSON, and more recently YAML. In addition, more recently, there's been more binary equivalents to XML and JSON such as EXI and BSON which provide more compact formats of the same. Of course there's many other formats such as AVRO and Protobufs which provide their own end-to-end solution. While many formats have a large amount in common, each need to make literally hundreds of tiny implementation decisions which causes them to diverge and make them incompatible.

The idea behind Litterat is to look at data presentation as three distinct parts:

 * Programming language interface: Develop a programming paradigm that allows both code first
   and generated data classes/structures and code to represent data. It should be possible to
   use many of the same code elements across many different formats and protocols.
   
 * Format independent schema: XML Schema, JSON Schema, AVRO Schema and many other formats define 
   their own schema which in many cases describe the same concepts. The Litterat schema is a
   data structure rather than a format. In this way it should be possible to import and
   normalise multiple schemas and provide standard ways to adapt any incompatibilities.
   
 * Data formats: With the language interface and format independent schema defined, it should 
   be possible to read/write numerous different data formats. This allows developers to select
   one or more format depending on the system requirements. Litterat will also include its own
   binary format that aligns with concepts of the Java serialization format (i.e. contain
   encoded meta data at the time it is first written).
   
 * Data engine: A final aim is to produce a simple data engine (AST) that can be used to 
   read and write the data formats.
   
This document is mainly concerned with the concept of a format independent schema.
   
## What is Data

Before the Litterat components can be built, there must be a good understanding of what is data and in particular in the context of data interchange formats. Data interchange is specifically dealing with computer to computer interaction. Computer to human interaction must deal with data localization which is out of scope. Given that, we define data interchange:

    "data is any array of bytes (8-bit) that is transferred in a contiguous block."

There's a few things to note about this definition:

  * While 8-bit bytes have not always been the standard, most computer and networking equipment is based around this standard.
  * It is possible to define data structures using pointers between points of the data, this is considered out of scope. 
  * There's no stipulation here on whether it is binary or text. Both binary and text similarly constrained by being an array of bytes.
  
 
Using this simple definition it becomes possible to define the basic building blocks that can fit within the basic construct. The building blocks are common in all data formats and include atoms, arrays and tuples. 

  * __atoms__: An atom is any arrangement of bits (text or binary) that represents a a data value that is represented as a single value.
  * __tuple__: A tuple is any arrangement of values being atoms, tuples or arrays in a sequence. Requirements for order are not specified here or if this is represented as text or binary.
  * __array__: An array is any arrangement of repeating elements of a value (atoms, tuples or arrays).
  * __identifier__: An identifier allows the type of a value to be identified, such as declaring the following value is an integer or more complex element.

In addition to the basic building blocks it's possible to have more complex structures such as maps and sets. More complex tuples such as what is defined by XML with both ordered elements and unordered attributes are possible. There's a lot to be explored using just the above core concepts, so the more complex areas are to be explored later.


### The need for metadata

Before jumping into the various building blocks of data, its worth pointing out the need for metadata. Metadata or data schemas are what give meaning to data so that developers can build software to interpret and make use of the data. Take for example the data:

    0x00 0x00 0x00 0x01 0x00 0x00 0x00 0x02
   
I could provide this simple 8-byte data array and also provide a text document which states:

    'Data is two big-endian 32-bit numbers representing x and y on a graph'
  
For all but the smallest projects this might be enough. However, as the need for more complex data grows so does the specification. The complexity of turning specifications into code become more complex and relies on written language interpretations. A metadata specification allows code to be auto-generated and specifications to be more clearly communicated with less room for error.

The above example can the be communicated as a record:

```
  record {
    x uint32;
    y uint32;
  }
```
  
What might not be completely clear here, is that as soon as you pull at this thread of building a metadata specification, it comes with a large list decisions. Decisions about naming, types of structures, types of atomic types. XML has ended up with DTD, XML Schema and Relax NG as competing schema languages. There's specifications for JSON Schema, YAML Schema and various others. Each of these schema languages start with a data format and create a schema using the structures that are allowed in the data format. Litterat schema aims to start with the schema and core concepts of data and allow multiple formats to be written.

So the purpose of metadata schemas are mainly focused on:

 * Define and describe the structure of data in a common unambiguous way to allow interchange of data between systems.
 * Allow the metadata to be used to validate if a set of data complies.
 * Allow the metadata to be used to generate code (dynamic or static) for reading/writing data
 * Provide a mechanism to version control and pick up changes in data structures.
 


### Types and Type Systems

Pulling on the thread of adding metadata to data quickly lands on the need to define types and type systems. It is very easy to get lost in lambda calculus and other type systems which define all concepts from first principles.  They also offer complete turing complete computational solution. For the purpose of Litterat metadata, there's a few very basic theories we can borrow. These are also concepts found in other metadata systems.

The core concept of a data metadata system is that it is declarative. The aim is to create a solution that builds a template of all possible allowed definitions of a set of data. Using the point example again:

```
  point: record {
    x uint32;
    y uint32;
  }
```

The above defines a 'point' as a record that contains x,y values with ranges from 0 to 2<sup>32</sup>. There's quite a lot going on in this simple example.

  * __type definition__: The main concept above is that there's a production that defines a name with a definition.
  * __type name__: There's an implication here that 'point' is a type name that is unique within the given solution. There's also the concept of name spaces to be considered.
  * __type reference__: X and Y values are referencing the another type uint32 which are assumed to be defined elsewhere. 
  * __record structure__: There's a concept of a named-tuple that allows the creation or a record that has multiple named elements.
  
All these concepts should be defined explicitly so that different programming languages can implement solutions that function in the same way. The concepts are all also inter-related, so it is difficult to pull them apart without referencing the others. In addition, by defining one or more, it is effectively defining the type system.

### Examples of Schemas

There's quite a few schema solutions that are tied to specific data formats. This is all good reference material and referenced in the exploration of different elements of schemas.

  * [Amazon Ion Schema](https://amzn.github.io/ion-schema/docs/spec.html)
  * [Apache AVRO](https://avro.apache.org/docs/current/spec.html)
  * [CapnProto](https://capnproto.org/language.html)
  * [JSON Schema](https://json-schema.org/draft/2019-09/json-schema-core.html)
  * [Protocol Buffers](https://developers.google.com/protocol-buffers/docs/proto3)
  * [XML Schema](https://www.w3.org/TR/xmlschema11-1/) 
  
### Litterat Schema Requirements

After exploring the background and need for metadata and how its inter-relatedness makes it difficult to describe partially, it's a good idea to step back and build a set of requirements for the Litterat Schema.

 * __Format agnostic__: The core concept is that Litterat schema is format agnostic. It specifies the structure of data without specifying how it will be presented. It should apply to both binary and text based formats.
 * __Usable__: The schema should be usable by developers. The aim is not to create an extension of lambda calculus or other theoretical type system.
 * __Declarative__: The schema is declarative which is already familiar with many developers.
 * __Embeddable__: It should be possible to embed a schema as part of a selected data format.
 * __Composible__: It should be possible to take any declaration component individually, instead of requiring a whole schema.
 
The first four concepts are self explanatory, however, the concept of composibility is worth further discussion. It will also have a strong influence on the structure of the Litterat schema solution and embeddability. Finally, it also helps both version control, however, its main purpose is compatibility validation. 

A core purpose of a metadata schema is to allow a receiver to validate if the data conforms to the structure of data it is able to accept. What that means is that the sender may have written the data with a different version of the schema than what the receiver understands. However, if the data also included the schema, it isn't very useful to just compare the internal and external schemas. While portions of the schema might be different, the data might validate against the portions of the schema that are the same. The data might then be rejected even though the data could be processed. Composibility of the schema is the idea that the data might only contain the metadata for the parts of the schema that is encoded in the data. When the data is received, the receiver can validate that only the portions of schema are matching. The concept of composibility means that a Litterat schema would act like a dictionary of terms rather than monolothic schema documents.

A good example of where composibility would be useful is enumerated types. Take for example:

    status: enum( GOOD, BAD );
   
A sender has a definition of "status" that can either be GOOD or BAD. However, a receiver has defined status as:

    status: enum( GOOD, BAD, UGLY );
   
It would be easy to suggest that as the definitions do not match the data should be rejected, even though the senders data fits easily inside the definition of the receiver. If we modify the definition to be composible:

    status: enum( GOOD );
    status: enum( BAD );
    
and the receiver as:

    status: enum( GOOD );
    status: enum( BAD );
    status: enum( UGLY );
   
The sender might only embed the definition of the type being used. This allows the receiver to validate the data and accept it. It also allows data to flow in the opposite direction as long as enum( UGLY ) is not used in the data stream. This also removes the need for explicit version control. As long as a data stream contains a partially embedded schema and conforms to the receivers internal schema the data can be processed.

There's implications in the above for the rest of the schema design and how data can be validated. This is that if each element of the schema is composible then any data with embedded data can be validated using the equivalent of a tree walk algorithm that compares the sender and receivers partial schemas. It would be data format specific on whether enough embedded information is included to perform the required comparisons.

It's worth noting that the definitions of the type system do not need to be defined using individual definitions as above. The first definition, "status: enum( GOOD, BAD )" could easily be decomposed into individual definitions internally. 

Using the above requirements we can start to define the main building block of the Litterat Schema as a context-free grammar abstract syntax tree using a set of production rules with the format:

    <type name>: <definition>
   
Expanding on the example above, we can formally define validation between two schemas as finding the subset of rules that match between two grammars. Let's use another simple example:

    shapeList: arrayOf( shape );
    shape: oneOf( circle, square );
    circle: tuple( x, y, radius );
    square: tuple( x, y, width, height );
   
If a sender was to transmit:

    circle( x=1, y=2, radius=10 )
   
the full schema is not required as only a circle was used, as such the sent grammar can be pruned to become:

    shape: oneOf( circle );
    circle: tuple( x, y, radius );
   
The receiver can now perform a simple comparison (tree walk) to verify that the sent grammar can be overlaid on the receivers grammar. This could also be applied to tuples, however, would require default values to be provided for missing components. For example the receiver might define circle as:

    circle: tuple( x, y, radius, name=default("unknown") );
   
This signifies that while "name" could be provided, if it is not provided a default value is set. In this case the sender grammar can still be matched. If we break the tuple up as individual production rules, it is shown that each link is either required or optional.

    circle: tuple(x) required;
    circle: tuple(y) required;
    circle: tuple(radius) required;
    circle: tuple(name=default("unknown") not required;

This also shows what has been known about many other schema systems; adding new optional elements to a schema allows for backward compatibility. However, it is not always possible to make non-breaking changes, so some type of version control is required. Take for example schemas that have defined square as:

    square: tuple( x, y, width, height );
    square: tuple( x1, y1, x2, y2 );
   
It isn't enough to alias particular values, as each would require different ways to construct an object. While it might be useful to add a note that the first production is preferred (or is version 2), it doesn't effect the grammar matching requirement. The need for versioning should be explored further, however, from this simple example, adding versioning does not assist the solution. 

However, when sending data, there might be a preference to use one of over the other. As there's no versioning, in this instance an annotation would be useful.

    square: tuple( x, y, width, height );
    square: @deprecated tuple( x1, y1, x2, y2 );

An annotation would not form part of the production rule in the sense that it does not need to be matched, but might provide additional information useful for the sender or receiver. Annotations like their Java counter-part allow information that would otherwise pollute the production rule to be placed along side the rule. A description might also be an example of an annotation.

Another set of breaking rule might be something like:

    shape: oneOf( circle, quadrilateral );
    qaudrilateral: oneOf(square, rectangle);
    circle: tuple( x, y, radius );
    square: tuple( x, y, width );
    square: @deprecated alias( rectangle );
    rectangle: tuple( x, y, width, height );
    
The sender with an older schema sends:

    shape: oneOf( square );
    square: tuple( x, y, width, height);    
    square( x=1, y=2, width=10, heigth=10 );
    
The receiver is still able to collapse the new schema to match the older data. The shape and quadrilateral are collapsed and the alias to rectangle is matched. There will of course be many examples of breaking changes, however, through the use of multiple rule matching and substitution it should be easier to define backward compatibility.

Another property that is implied by the above example is that there can be multiple definitions for a type name. Any reference for type substitution does not specify the rule to be matched. It would be up to the implementation and data format to verify if the correct rule can be matched.

Using the above exploration of production rules, it is possible to put together a few more of the rules around the solution.

   1. A schema is made up of a set of type definitions (production rules). 
   2. A definition is declarative and provides a template for matching data.
   3. A type definition may include annotations which do not form part of the rule and can be used by sender/receiver for other purposes.
   4. There can be multiple type definitions with the same name.
   5. There's no top type definition. A schema is complete if matching rules can be found for all non-terminal rules.
   6. Each rule must be composible allowing partial matching. A composible rule element can be optional.


The above provides a rough set of principles which will need to be formalised further later. However, with these as a rough guide it is time to move on to the definitions and how data should be defined using these rules. 



### Choice, Union, OneOf or Any

XML Schema uses choice, Protobufs and JSON schema use OneOf, and Avro uses Union. These all effectively allow the same thing, an ability to identify and constrains to a list of types. The concept of Any does what it suggests and allows any value.

XML Schema conflate the concepts of choice and arrays allowing minOccurs and maxOccurs as attributes. To keep rule definitions simpler, this option will not be included.

The concept of choice can mean different things depending on the situation:

  * Nullability. As an example Avro uses Union to define nullability of a field. e.g. union ["null","string"].
  * Interface. As the example of shapes above, a choice could map to an interface at the language level.
  * Unrelated types. In protobufs and XML schema a choice can map to two different getters/setters for completely unrelated fields at the language level.

However, ignoring how these are defined by the language, a choice at the data level is that only one of a list of types is provided. Language related implementations can be assisted by annotations or information external to the core. XML Schema and JSON Schema also allow more complex choices between partial anonymous types. For instance:

   some_type : choice( array( foo ), tuple ( bar, array (foo)) );
   
A choice is therefore an array of schema elements.

### Arrays

An array specifies a collection of elements. An array can contain either a single type of repeated elements or can contain a mixed set of elements. The differences between existing schemas is quite surprising on how much they diverge. XML Schema embeds the concept of repeated elements by adding minOccurs/maxOccurs attributes. Protobufs simply allows a "repeated" keyword. Avro allows specifying the type of array only, and JSON is in its own world.

JSON Schema allows a complex set of constrains for [arrays](https://ajv.js.org/docs/json-schema.html#keywords-for-arrays). Like many other schemas, this includes min/max items to constrain the number of items. However, it also includes the ability to constrain to uniqueItems which should invalidate based on content. And includes min/max contains which allows enforcing the number of items of a particular type are present while ignoring other items. There's also a few other constraints which creates a very flexible way of validating the contents of an array. It's a really good example of building a schema based on a data format. 

It's worth exploring a few of these ideas to see if they should apply to Litterat. It is also a good example to show that building a schema solution that covers multiple data formats can not re-create the complexities and nuanced decisions of all other schema languages.

  * minItems/maxItems: This restricts the number of items in an array. This is equivalent to XML Schema minOccurs and maxOccurs. 
  
  * items: This allows specifying specific types onto an array. This is interesting as it blurs the lines between a tuple and an array. By specifying the order and types of an array, an unnamed tuple is defined. additionalItems is another option used with the items concept, it allows additional items in the array beyond what is specified by the items list. As the JSON format allows putting any types into an array, this just opens the door for added items beyond what is required. The items attribute is also used to constrain values to a specific type.
  
  * contains/minContains/maxContains: The contains attribute makes an array valid if it contains at least one item that is valid. This is quite a strange restraint that has a different purpose to the items constraint.
  
  * other: JSON schema allows very complex evaluation rules that contain sub schemas, if/then/else based on data values and a variety of other constrains.
  
The idea of constraints based on uniqueItems, if/then/else conditions and a wide variety of other concepts raises the question of how far should a schema specification go in performing the data validation. There has to become a line where procedural language concepts enter that go beyond a data template.

### Anonymous data

Another interesting element to JSON Schema arrays is that it allows for anonymous elements. By saying that the first n-elements are typed and named and then adding the attribute additionalItems is true, then any random other items can be added to the list. This seems to be like providing half a specification and saying "stuff goes here".

Leaving this here as another area worth finding use cases. However, I'd say most use cases can be resolved using the "any" concept.

### Tuples, n-tuples, arrays,

The way JSON Schema allows complex validation of array values makes it worth taking a side step to investigate a bit further the underlying structure of tuples and arrays. JSON Schema was likely developed in the way it was in part because of the way JavaScript treats an array as an ordered set of any type of object. This is quite different to Java where an array is an ordered
list of a specific type.

Before going further it is worth making some definitions.

  * [tuple](https://en.wikipedia.org/wiki/Tuple): A finite ordered list (sequence) of elements.
  * n-tuple: An ordered list (sequence) of n elements (n is non-negative integer).
  * named-n-tuple: An ordered list of n elements where each element is labeled with a name.
  * typed-n-tuple: An ordered list of n elements where each element is assigned a type.
  * [record](https://en.wikipedia.org/wiki/Record_(computer_science)): An ordered list of n elements having both a name and type assigned to each element.
  * [array](https://en.wikipedia.org/wiki/Array_data_type): A dynamically sized finite ordered list (sequence) of elements of a specific type.


The Wikipedia search (obviously not very extensive) did not turn up very good definitions, so I've added a few of my own to make the differences clearer. The Array data type has a strong association with programming language implementations and as already stated does not specify a common type for some languages. For the purposes of further discussion I'm using the above definition.

A specific use case comes to mind when thinking about how developers often overlay the concept of a named-n-tuple over an array. That is an array of points:

    values = [ 1, 1, 2, 2, 4, 4 ]
    
In C, it is possible to overlay a struct with x,y values and then address the values in the array. Excuse the bad syntax, but you get the idea.

    point[] points = (point[]) values;
    points[0].x;
    points[0].y;

However, if a developer were to define the data as part of a schema it might be defined as:

    points: array( point );
    point: record( integer x, integer y);

Using the schema definition a developer could choose to implement the physical layout as an array of integers or as an array of point or using just integers. 

    values = [ point(1,1), point(2,2), point(4,4) ]
    
The point (no pun intended) is that the layout in memory or as data is an implementation detail rather than a modification of the underlying structure of the data. There's still a potential problem that developers may want to create unions on the same data structure. For instance:

    points: array( pointType );
    pointType: oneOf( 2dPoint, 3dPoint );
    2dPoint: record( integer x, integer y);
    3dPoint: record( integer x, integer y, integer z);
    
Returning to JSON Schema, it would allow schema choice based on another value:

    pointTypeEnum: enum( 2DPOINTS, 3DPOINTS );
    pointsRecord: record( pointTypeEnum type, if( type == 2DPOINTS, array(2dPoint), array(3dPoint)));
    
Adding this type of "if" construction to a schema is doable, however, the question is if the added complexity is worth adding. JSON Schema also allows creating pseudo record structures on top of a JSON array. It is obviously doable, but is it sensible. Something to add to the future discussion list.

### Substitution

Another topic which might be a little easier to resolve is the concept of substitution. JSON Schema makes it possible to create anonymous records. For example:

    pointType: oneOf( [ record( integer x, integer y), record( integer x, integer y, integer z) ] );
    
Structurally, this is the same as the previously defined pointType. This would push requirements on how the "oneOf" is implemented at the data format level, but in theory, names exist to assist the developer, not the format. As long as the correct branch can be matched no names are required. It might be problematic at the programming language level too where accessing the data might need specific methods for each. 

The main outcome of this is that the structure should allow that anywhere an name can be placed the definition can be substituted directly as the name is just a reference.


### Records

For the purposes of data interchange we can define a Record as being a named and typed set of values in a tuple. This is the most common and has the most similarity between schema solutions. Solutions generally include:

  * named and typed fields: A record includes a list of named and typed fields.
  * optional: A field may be optional.
  * default: A field may have a default value.
  * order: There may be a specific order to the fields.
  * duplicates: A field name is unique within the context of a record.

Using the concept of composability, a record could be specified using a list of fields:

    point: record( integer x);
    point: record( integer y);
    
Using this concept, the idea of ordering of fields is a data format specific or language implementation requirement.

The concept of making a field required or optional has been discussed numerous times by other schema/protocol solutions [here](https://capnproto.org/faq.html#how-do-i-make-a-field-required-like-in-protocol-buffers), [here](https://stackoverflow.com/questions/31801257/why-required-and-optional-is-removed-in-protocol-buffers-3) and [here](https://github.com/protocolbuffers/protobuf/issues/2497). This is a strong argument for not having required fields:

   Many required fields were "obviously" required until... they weren't. Let's say you have an
   id field for a Get method. That is obviously required. Except, later you might need to 
   change the id from int to string, or int32 to int64. That requires adding a new muchBetterId 
   field, and now you are left with the old id field that must be specified, but eventually is 
   completely ignored.

The problem with this is that older solutions will not be able to accept newer data if a field has changed id from int to string. Managing change is particular difficult for distributed systems and there isn't a silver bullet to solve this. However, the solution of making everyting optional seems to offer no guarantee of anything regarding the specification.

For the purposes of Litterat, I'll assume that if a field is specified then a value is required unless a default value is supplied. Exploring this idea with the previous example:

    circle: record(integer x);
    circle: record(integer y);
    circle: record(integer radius);
    circle: record(string name=default("unknown");

In the above example, x,y and radius are all required, while name has a default value and is not required. 

There's also the question of not requiring a value. Java has the concept of Optional as distinct from null. A contact record where every field is optional is a good example of not requiring a value for fields. All fields are optional and are not "null", they are not required to be present:

    contact: record(optional string firstName);
    contact: record(optional string lastName);
    contact: record(optional string mobilePhone);
    contact: record(optional string email);

Using the two examples above, there's clearly a distinction between a default value and optional and both concepts have value.

The concept of a field being required is more to do with the receiver of a record than the actual data representation itself. In the "circle" record above, the receiver indicates that x,y and radius are required values. If it received a record without anyone of those values it would be unable to construct a circle. However, there's no value adding default values or optional flags as part of a transferred data schema.


    circle: record(integer x, integer y, integer radius, string name );
    circle( x=1, y=1, radius=5, name="here" );

    contact: record( string firstName, string mobilePhone )
    contact( firstName="david", mobilePhone="123 456 789" );
   
In the "circle" case, name is included as part of the data. The default value of "unknown" is only relevant to the receiver if the field is not present, as such it makes sense not to include it in the data schema. In a similar way, the "contact" example, firstName and mobilePhone have been included and can be matched. The fact that the data doesn't include other optional fields does not matter. The fact that the fields are optional to the receiver is also irrelevant.

Based on the above, optional and default values are both annotations of a field rather than being a core piece of data. They provide an ability for the receiver to make better decisions on the received data, but are not part of the transferred schema.

Another concept that the above hints at is what a receiver should do when it receives an unknown field value for a record. for instance:

    circle: record(integer x, integer y, integer z, integer radius );
    circle( x=1, y=1, z=1, radius=5 );
   
In the above example the data includes a value z. JSON schema provides the boolean option "additionalProperties" which states if these should be allowed. This is once again an option for the receiver on if it should accept data with additional properties. In some cases the receiver is happy to accept and process the reduced set of data and make assumptions based on that data. However, it is likely that in many situations the user will be unwilling to accept data with unknown fields as it is possible the meaning of the fields may change with the addition of those fields. This is another example of where an annotation might be useful. It's really up to the receiver and the specific use case on how to deal with the data.

The ability to process "additionalProperties" is a slippery slope to saying that a record is just an arbitrary map of key/value pairs. A map is often a useful concept for some types of data, but its not the intention of "additionalProperties" to be a gateway to allowing the receiver the access and process additional values. If a field is not recognised but "additionalProperties" is enabled then the additional fields should be dropped. If a map is required, that can be created as its own data structure.

The concept of anntoations doesn't appear often in schemas (it exists in [CapnProto schema spec](https://capnproto.org/language.html)). An annotation can be used as a way of separating the implementation specific information from the pure data specification. A close approximation is XML attributes which exist separate to the data. XML attributes can often include attributes which were not originally specified in the data schema. The example earlier used @deprecated as a way of adding information to the schema without changing it. Adding annotations to the schema is a reasonably simple exercise. One concept to further explore is the ability to add annotations to arbitrary data.

In summary a field could have @optional and @default( value ) as annotations. A record can have an @IgnoreAdditionalFields (note: needs a more concise name). Annotations are not transferred as part of any data meta data and only included for end points to assist in processing and matching data to schemas. A record is defined as an un-ordered set of fields. A few samples:

    
    circle: @IgnoreAdditionalFields record( [ integer x, integer y, 
                        integer radius, @default("unknown") string name ] );

    contact: @IgnoreAdditionalFields record( [ 
                        @optional string firstName, 
                        @optional string lastName ] );
    


### Atoms

An atomic type or atom is any data which can not be further broken down into logical component parts. An atom is a conceptual idea rather than an implementation. For instance the integer 42 can be represented in a wide variety of ways; as binary using signed, unsigned or variable length values or as text. A Gregorian date or timestamp can also have a wide variety of both binary and text based formats. An atom can also have numerous language implementations and in-memory representations. This results in an atom being conceptual rather than having a physical representation. 

This idea that an atom is conceptual rather than material is useful, but it also complicates the concept of how to specify an atom. It means that an atom as a conceptual element needs to be specified by using some other external specification or system. For instance a date is from the Gregorian calendar system, a boolean is from boolean logic and an integer is from number theory. While these concepts are already deeply embedded in languages already, it is useful to step back and separate the conceptual from the implementation. Once that is done, we can get a step closer to proper separation of conceptual and data formats.

An outcome of the above is that the concepts that need to be defined by meta data are likely different for each conceptual type. The following is an exploration of some of the basic types.

#### Boolean

This is the most simple to define. The XML Schema specification provides a good definition.

    boolean represents the values of two-valued logic. Boolean has the value space of {true, false}.
  
From a meta data point of view there's nothing else we can say about a boolean data value other than it is a boolean. The values "true" and "false" will also need to be defined.

Maybe something like. This is recursive definition, but using @atom annotation allows breaking endless loops: 

    boolean: @atom OneOf( true, false );
    true: @atom boolean(true);
    false: @atom boolean(false);
    


#### Integer (Decimal)

It's very easy to connect the concept of integer with the implementation of integer in hardware as a value 32-bit int, or signed 16-bit short. However, as a data language it is useful to attempt to describe the an integer more universally. For a given application, an integer might represent a persons age; in this case a valid range might be 0 to 150 (leaving some room for improvements in longevity).  In an implementation that might be mapped to a uint8, or a signed int depending on the language. Having said that, we're always implementing on a base 2 system, so as developers we're more likely to specify uint8 as a constraint than 0 to 150.

XML Schema has options of totalDigits, fractionDigits, pattern, whiteSpace, enumeration, maxInclusive, maxExclusive, minInclude and minExclusive as constraints on the integer type. The concept of starting with an "integer" type and then applying constraints to reduce the rang is a good basis. Ideally, it would be useful to be able to specify the range, min/max values without recording the actual min/max values like XML Schema. e.g.

```
  <xs:simpleType name="short" id="short">
    <xs:annotation>
      <xs:documentation source="http://www.w3.org/TR/xmlschema11-2/#short"/>
    </xs:annotation>
    <xs:restriction base="xs:int">
      <xs:minInclusive value="-32768" id="short.minInclusive"/>
      <xs:maxInclusive value="32767" id="short.maxInclusive"/>
    </xs:restriction>
  </xs:simpleType>
```

Maybe something like:

    short: @atom integer( restriction = [ twoCompliment, precision( 2, 16 ) ] );
     
Possible integer restrictions include:

    minimum: Minimum value (inclusive). Requires a base type able to hold minimum value.
    maximum: Maximum value (inclusive). Requires a base type able to hold maximum value.
    precision: A precision specification with base and power. e.g. precision( 2, 8 ) is base 2 and power of 8 which is equivalent to unsigned 8-bit integer.
    twosComplement: Signifies that precision is based on twosComplement implementation.
    enumeration: A list of allowed values.
    
All integer types are going to be @atom types, as such, the meta data is more to assist developers implementing atoms rather than being useful at runtime.
    
#### Real & Floating point

Real numbers are most commonly implemented as an IEEE Floating point number, but could be implemented using fixed-point representation or other representation. If the value is made up of components rather than being an atom it should be represented using a record rather than an atom.

Possible real restrictions incldue:

    minimum: Minimum value (inclusive).
    maximum: Maximum value (inclusive).
    standard: This could include ieee756-single for a float.
    enumeration: A list of allowed values.

In a similar way to integers, all real types are specified using meta data. However, any meta data is to assist developers implementing rather than being particularly useful at runtime.

    float: @atom real( restriction = [ standard( ieee756-single ) ] );
    
Even though common reprsentation is ieee756, there are a wider variety of implementations that could be useful depending on the situation.

An interesting property of the implementation of real numbers as text or ieee756 format is that they both have different ways of implementing precision.  As such, you won't get exact conversions going between back and forth between text and ieee756. 

Something to explore is that both integer and real are both numbers. It may be better to use a set of restrictions on a number type rather than keeping them separate. However, the implementation is different enough that keeping the separate probably makes more sense.

It needs to be possible to further restrict a already defined real atom type. For instance, a user might want to say that a latitude is a float with minimum -90 and maximum +90. 

#### Strings and encodings

It is becoming near on universal that a "string" in many data formats means a unicode encoded array of characters (JSON, XML, CapnProto, Avro, ProtoBuf, etc). For this reason it is worthwhile following the trend. As Unicode can include multiple characters the length of a string may be different to the number of bytes that a string uses.

Common restrictions for strings include:

    length: Fixed number of character.
    minLength: Minimum number of characters.
    maxLength: Maximum number of characters.
    pattern: A regular expression that needs to be matched for characters.
    enumeration: A list of allowed values.
    
One thing that needs further research is a standard for regular expressions that has a common implementation across systems.


    
#### Enumerations





#### Gregorian Date

#### BLOB





    

  
  
It is important that every definition is also defined within the grammar. Each definition must also be either fully composible (might be some exceptions which are yet to be explored). While text based syntax is not important for the schema, it is required as a way to communicate the schema definition itself. 


A <type name> is any utf8 encoded string with an optional namespace component. For the purpose of discussion a namespaced type name has the format like "namespace.name" using dot notation as separator.

A <definition> is any valid definition defined by the schema. 

To go beyond these basic requirements, the definitions for atoms, arrays and tuples need to be defined as part of the solution. Before defining these it is worth exploring each of the concepts individually.

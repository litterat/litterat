

# Serialization Theory

[Serialization](https://en.wikipedia.org/wiki/Serialization) is the act of transferring formatted structured information between systems or to/from storage (e.g. files, messages, etc). An outcome of serialization formats is the need to create specifications that allow different systems/programming languages to understand and process data. Specifications come in both informal (written documents) and formal (schemas and interface definition languages (IDL)). In the OSI seven layer model of communication, serialization is the presentation layer.

There's a [huge variety of different](https://en.wikipedia.org/wiki/Comparison_of_data-serialization_formats) solutions for serialization. Serialization formats and presentation protocols often conflate many problems into a single solution. Each new data format and schema are often developed as a need to solve a localised problem. The new data format might focus on attributes such as speed, compactness and human readability. This gives rise to many solutions with particular formats coming in and out of favour. Common data formats include [XDR](https://en.wikipedia.org/wiki/External_Data_Representation), [XML](https://en.wikipedia.org/wiki/XML) and [JSON](https://en.wikipedia.org/wiki/JSON), but are also embedded in solutions like [CORBA](https://en.wikipedia.org/wiki/Common_Object_Request_Broker_Architecture). There's many other formats and systems that embed formats such as [AVRO](https://en.wikipedia.org/wiki/Apache_Avro) and [Protocol buffers](https://en.wikipedia.org/wiki/Protocol_Buffers). 

This is a collection of notes on theory underlying data serialization formats and specifically abstract data models and data schemas.  I've written it because I've found it difficult to find good sources of information on the science that underlies serialization. The basis for this document is that behind all data formats and schemas there's a common thread of ideas and concepts. By having a clear understanding of those concepts it should become easier to spot the concepts in different implementations. In addition, having a clear understanding of these concepts will also help make better decisions on designing future data formats and schemas. It should be considered a living document, so if you find any errors or omissions please let me know.


## What is Serialization

Serialization is usually [described](https://en.wikipedia.org/wiki/Serialization) as the process of translating a data structure or object state into a format that can be stored (for example, in a file or memory data buffer) or transmitted (for example, across a computer network) and reconstructed later (possibly in a different computer environment). To start, it is worth breaking this definition down into the various components:

__"Translating a data structure of object state"__  There's a chicken and egg problem here of whether you start with some data already stored in a format, or you start with a data structure in memory. The most important point here is that the data is structured, rather than unstructured data (texts or documents).
   
__"into a format that can be stored or transmitted"__  What is inferred is that a format is a byte array of either binary or text. Structured data is interested in writing atoms (An atomic value is the smallest unit of information (e.g. boolean, number, string, etc) into a "serialized" (to arrange something in a series) format. 
   
__"and reconstructed later"__  To reconstruct the structured data back into a program it must be read and understood. This requires that the reader understands the structure of the data. What is inferred is that either the format includes metadata about the format or that the reader has prior knowledge of the data format.
   
The definition could be changed to:

__The process of arranging atomic values in a series to a data format (file, message, memory, or communications channel) in a way that the information can be read later.__

The definition could be improved further, but the important details are that a serialization data format is the arrangement of atomic values with an ordering that can be understood and read later. There's a lot of scope in this definition for a wide variety of formats and solutions. However, it also constrains the problem space quite nicely.

### The need for metadata

Before jumping into the various building blocks of data, its worth pointing out the need for metadata. Metadata or data schemas are what assigns structure to data so that developers can build software to interpret and make use of the data. Take for example the serialized data:

    0x00 0x00 0x00 0x01 0x00 0x00 0x00 0x02
   
I could provide this simple data array and also provide a text document which states:

    'Data is two big-endian 32-bit numbers representing x and y on a graph'
  
By combining the data and the description, a developer has all the information required to read the data and understand its meaning. For all but the smallest projects informal text documentation like this might be enough. However, as the need for more complex data grows so does the specification. The complexity of turning specifications into code become more complex and relies on written language interpretations. A metadata specification allows code to be auto-generated and specifications to be more clearly communicated with less room for error.

The above example can the be communicated as a record:

```
  record {
    x uint32;
    y uint32;
  }
```
  
What might not be completely clear here, is that as soon as you pull at this thread of building a metadata specification, it comes with a large list decisions. Decisions about naming, types of structures, types of atomic types. XML as an example has ended up with DTD, XML Schema and Relax NG as competing schema languages. There's specifications for JSON Schema, YAML Schema and various others. Each of these schema languages start with a data format and create a schema using the structures that are allowed in the data format.

So the purpose of metadata schemas are mainly focused on:

 * Define and describe the structure of data in a common unambiguous way to allow interchange of data between systems.
 * Allow the metadata to be used to validate if a set of data complies.
 * Allow the metadata to be used to generate code (dynamic or static) for reading/writing data
 * Provide a mechanism to version control and pick up changes in data structures.

An alternative approach is to include elements of the metadata as part of the data format. The same example could be written using JSON;

```
  {
    point: {
        x: 1,
        x: 2 
    }
  }
```

This will be further discussed later, but this is a good example of trade offs between size of the file format with other attributes such as human readability and complexity. There's also an inference here that certain details are always present in some form:

  * Atom value: In this case the value 1 and 2 is the raw data.
  * Atom type: Each atom value has a specified type; integers in this case.
  * Atom meaning: Whether formally or informally specified, each value is given a name and meaning.
  
It is possible to reduce the size of the file by keeping the metadata (type and meaning) external to the data. Some formats will also make partial trade offs and include the type and value but not the name/meaning of the data (e.g. [BER](https://en.wikipedia.org/wiki/X.690#BER_encoding) format that have [Type Length Value](https://en.wikipedia.org/wiki/Type-length-value) structures). 

All serialization solutions (format and metadata) must have enough information present (formally or informally) to read and understand the structure of the data. There's many choices of where to put that information, but it must go somewhere to make a format useful. For the purpose of discussion, we'll say a serialization solution is "data/schema complete" when both the schema and data are both available. Note that there's a difference between  __understanding the structure__  versus  __understanding the meaning__  of the data. Assigning meaning "the point is for display on an graph and represents ..." is a different problem and out of scope for this discussion.

For the rest of this document, we're interested in studying these "data/schema complete" solutions. To do this, we must first look at schemas. With a strong understanding of schemas and the structures that underly the data, we'll have a strong basis to define data systems. If there's a chicken and egg problems between data and schemas, it is definitely that the schema should come first (although that is not often the case in practice).


## Schema theory and background

All schemas whether they intended to or not will have a heritage dating back to [ASN.1](https://en.wikipedia.org/wiki/ASN.1) (1984) and [Backus-Nuar Form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form) (1960). ASN.1 is a standard interface description language for defining data structures in a format independent way which can then be encoded using a variety of formats ([BER](https://en.wikipedia.org/wiki/X.690#BER_encoding), [CER](https://en.wikipedia.org/wiki/X.690#CER_encoding), XML, JSON, etc).  Given how long ASN.1 has been around, it is surprising it is not in wider use. [Some issues](https://tools.ietf.org/html/draft-yu-asn1-pitfalls-00) with ASN.1 include that it was difficult to get hold of the specifications and open source implementations. While that part seems to have been corrected, the complexity and many options makes it difficult to approach.

Underlying ASN.1 are [Algebraic Data Types](https://en.wikipedia.org/wiki/Algebraic_data_type); the Wikipedia page states, "Algebraic data types are highly suited to implementing abstract syntax". The page on [Abstract Syntax](https://en.wikipedia.org/wiki/Abstract_syntax) directly links to [ASN.1](https://en.wikipedia.org/wiki/ASN.1). The ASN.1 wikipedia page includes:

> ASN.1 is visually similar to [Augmented Backus-Naur Form](https://en.wikipedia.org/wiki/Augmented_Backus%E2%80%93Naur_form) (ABNF), which is used to define many Internet 
> protocols like HTTP and SMTP. However, in practice they are quite different: ASN.1 defines a data 
> structure, which can be encoded in various ways (e.g. JSON, XML, binary). ABNF, on the other hand, defines 
> the encoding ("syntax") at the same time it defines the data structure ("semantics"). ABNF tends to be 
> used more frequently for defining textual, human-readable protocols, and generally is not used to define 
> type-length-value encodings."


If you squint you can see why Backus-Naur Form, Algebraic Data Types, and Abstract Syntax are all similar. It goes back to the concept of being arranged in series. They are all focused on ways of specifying the order of things; tokens in a language, grouping structures or atoms being serialized. Of the different concepts, Algebraic Data Types are a good basis for underlying science of serialization. 

Algebraic data types are structural [composite types](https://en.wikipedia.org/wiki/Composite_data_type) that include [Product Types](https://en.wikipedia.org/wiki/Product_type) (tuple, record, etc) and [Sum Types](https://en.wikipedia.org/wiki/Tagged_union) (Choice, OneOf, Union, etc). When you view the various implementations through the eyes of product types and sum types you can see the common threads. More importantly, you can see that by understanding and implementing around these concepts no other structures are required. This greatly simplifies the domain that needs to be investigated; both product and sum types are explored further later.

One of the questions I first had was why are product types and sum types all that is required for a serialization specification. While there is most likely a proof (anyone got a paper to link?), the likely answer is again related to "being arranged in series". For example, given tokens A and B that are arranged in series:

    A B 
    
What follows as the third element? I can state that it will be "A B A", "A B B", or "A B (A or B)". Given only the tokens A and B exist there is no other option for a serial form. Effectively "A B A" and "A B B" are examples of the product type while "A or B" is the sum type. The number of tokens (atomic types) can be expanded but as long as there's a serial form the options are limited.

One final option to the question of what follows "A B" is splitting the stream into two directions; this is the annotation use case. The first stream continued with the normal specification, and another stream begins at the same point. This can be thought of as the option, "A B (both A and B)".  While technically, this could be specified as a specialised product type, it is interesting enough that it warrants its own discussion.

The other concept that is prevalent in Backus-Nuar Form and Abstract Syntax is substitution that has its basis in [Context Free Grammars](https://en.wikipedia.org/wiki/Context-free_grammar). The idea that a specification is made up of smaller named rules that use the named rules in different parts of the specification. This gives rise to the concept of a type system and problems of naming and namespaces. It is important to note that the names are metadata for the specification and don't necessarily change the data format. As such, it's possible to use substitution to define rules which may look different but result in the same structure.

A common property of these production rules (often referred to as type definitions) is that they form constructor patterns for the implementation languages. For instance a rule for a circle includes x,y and radius values and it maps to a class Circle with constructor with the same parameters. As such, it is common to see a one to one mapping of a production rule (named rule or type) to a specific type in a language.

While ASN.1 defines an Abstract Syntax that defines the structure of data that can then be written using a variety of data formats, using the above concepts we can build an Abstract Model that can be used to understand all schemas and data formats. The Abstract Data Model for data serialization can be boiled down to the following underlying concepts:

 * Schema : A collection of defined types (production rules) that can have multiple start positions.
 * Type definition : A named production rule in a data model schema for use in substitution of other rules.
 * Product type : A compound data structure that combines atoms and other types.
 * Sum type : Specify allowed choices between valid types. 
 * Atomic values : the underlying data concepts to be transferred (boolean, numbers, string, dates, etc)
 * Annotations : Adding another dimension to data schemas. The sticky note of data structures allowing additional information to be associated with specific data.

That's it, there isn't a lot of complexity when you boil down schema design to these fundamental concepts. By exploring each of these concepts in detail, and understanding the extents of each of the concepts, it will be possible to build a better abstract model for serialization.

In addition to the above, there's one more advanced concepts that will also be explored. Complex validationa adds case statements to product type validation to allow interactions between fields. For example, if a boolean value is true then choose type X from a set of sum types.

### Schema reference material

There's quite a few schema solutions that are tied to specific data formats. This is all good reference material and referenced in the exploration of different elements of schemas.

  * [Amazon Ion Schema](https://amzn.github.io/ion-schema/docs/spec.html)
  * [Apache AVRO](https://avro.apache.org/docs/current/spec.html)
  * [Apache Thrift](https://thrift.apache.org/docs/idl.html)
  * [ASN.1](https://en.wikipedia.org/wiki/ASN.1)
  * [CapnProto](https://capnproto.org/language.html)
  * [CDDL](https://tools.ietf.org/html/rfc8610)
  * [JSON Schema](https://json-schema.org/draft/2019-09/json-schema-core.html)
  * [OpenAPI](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.3.md)
  * [Protocol Buffers](https://developers.google.com/protocol-buffers/docs/proto3)
  * [SDXF](https://en.wikipedia.org/wiki/SDXF)
  * [XDR](https://en.wikipedia.org/wiki/External_Data_Representation)
  * [XML Schema](https://www.w3.org/TR/xmlschema11-1/) 

Underlying concepts related to schemas and referenced in the document:

  * [Algebraic data type](https://en.wikipedia.org/wiki/Algebraic_data_type)
  * [Associative array](https://en.wikipedia.org/wiki/Associative_array)
  * [Comparison of data serialization formats](https://en.wikipedia.org/wiki/Comparison_of_data-serialization_formats)
  * [Composite data type](https://en.wikipedia.org/wiki/Composite_data_type)
  * [Data modeling languages](https://en.wikipedia.org/wiki/Category:Data_modeling_languages)
  * [Data structure](https://en.wikipedia.org/wiki/Data_structure)
  * [Data type](https://en.wikipedia.org/wiki/Data_type)
  * [Product type](https://en.wikipedia.org/wiki/Product_type)
  * [Serialization](https://en.wikipedia.org/wiki/Serialization)
  * [Tagged union](https://en.wikipedia.org/wiki/Tagged_union)
  * [Union type](https://en.wikipedia.org/wiki/Union_type)


## Exploration of themes

The rest of the document will be used to explore the different concepts and see if there's anything missing from this model. I would be interested to hear from others that can see the blind spots I'm missing from this model. There will be a few tangents as I try to tie together some previously written threads into a half decent narrative.

### Product Types (Sequence, Record, Struct, etc)

A [Product Type](https://en.wikipedia.org/wiki/Product_type) with regards to serialization is any structure that combines multiple elements. This has been called a Sequence (ASN.1, XML), a Record (Avro) or Struct (Apache thrift). A product type can have a number of attributes (named fields, assigned types). Given that there's a wide variety of names given to product types, I've created the following definitions:


  * [tuple](https://en.wikipedia.org/wiki/Tuple): A finite ordered list (sequence) of elements. Order is important and each index is not specifically named and does not have an assigned type. Number of elements is dynamic.
  * n-tuple: An ordered list (sequence) of n elements (n is non-negative integer).
  * named-n-tuple: An ordered list of n elements where each element is labeled with a name. No types specified.
  * typed-n-tuple: An ordered list of n elements where each element is assigned a type. No names assigned.
  * sequence: An ordered list of n elements having both a name and type assigned to each element.
  * [record](https://en.wikipedia.org/wiki/Record_(computer_science): A finite list of fields having both a name and type assigned to each element.


For the purpose of a serialization schema, I'm using  __record__  as the preferred name. I view a  __sequence__  that requires order is an implementation detail rather than a requirement of an abstract model. In addition, as mentioned previously, I'm interested in the idea of "data/schema complete" where both names and types are specified. The rest of the discussion on records will also apply to the other forms of product types. The elements of a record have been called fields, components and elements. To avoid confusion, I've defined the usage as:

  * field: A component of a record where the entry is identified using a name and order is not enforced.
  * element: A component of a sequence or tuple where the entry is identified using an index.
  
I'm going to use the following notation to define a record. this is not intended to be a syntax that will be used in a schema and is intended to used to explore concepts. A record includes a list of types and names.
 
    circle: record( integer x, integer y, integer radius );

The properties of a record in an abstract data type sense is quite reduced. Access to a field is by name and each field name is unique within the context of a record. There can also be a [cardinality](https://en.wikipedia.org/wiki/Cardinality) assigned to each field. That is each field could be one of the following:

  * required: Value for the field must be supplied.  
  * optional: A value for the field can be optionally supplied. "?" in Backus-Naur Form.
  * [array](https://en.wikipedia.org/wiki/Array_data_type): A dynamically sized finite ordered list of elements of a specific type. It is common to use symbols, "+" as one or more, or "*" as zero or more.
  
I've used the term "array" instead of specifying a size range or saying it is a "repeated" field. It should be noted that "array" is in some cases used where tuple might be more appropriate. For instance the JSON array allows elements of any type; more like a tuple as per the above definition. For the purposes of this discussion, I'm specifically saying that an array is a list of a specified type.

Where a field is optional it is also possible to have default values. Default values are similar to ordering of fields; they are an implementation detail which is helpful to the reader of a serialization format. There's also a question to be explored of whether there's a difference between null and optional for a field. The role of default values and nullability will be explored later. 


### Sum Type (Choice, Union, OneOf)

A Sum Type or [Tagged Union](https://en.wikipedia.org/wiki/Tagged_union) is a data structure used to hold a value that could take on several different, but fixed, types. XML Schema and ASN.1 uses Choice, Protobufs and JSON schema use OneOf, and Avro uses Union. In Backus-Naur Form it is denoted using the "|" symbol.  These all effectively allow the same thing, an ability to identify and constrain to a list of defined types. CORBA combines a [switch and union concepts](https://en.wikipedia.org/wiki/Discriminator) to create a discriminated union that requires an input value.

Depending on how you treat "null" in the type system, it is possible to define a value to be nullable by creating a union between a type and null. This is how Avro implements nullability (e.g. union ["null","string]).

A union type is more difficult to map to programming languages like Java that don't have a direct union type.  Consider the following example of mapping to multiple fields. It's also a good example that there doesn't need to be a direct mapping between data model and language implementation.

    my_record : record( union( string, integer ) x )

In java this could be mapped to:

```
   class MyRecord {
      private final String sX;
      private final int xI;
   
      public MyRecord( String sX, int iX ) {
         this.sX = sX;
         this.iX = iX;
      }
   }
```

The other option is to use an interface as the implementation. This requires that the union is not anonymous like in the above definition. For example:

    shape: union( circle, square )
   
This can be mapped to an interface and is a good use for the new sealed feature:

```
   public sealed interface Shape permits Circle, Square { ... }
```
   
The  __Any__  type can be considered an open ended version of a Union. This is found in numerous serialization systems and can be mapped to an Object type in a language like Java (ignoring issues with primitive types). In some cases an Any object is created as part of the implementation. This allows the type to be identified at runtime where reflection is not available or suitable.

Ignoring how a union is defined at the language level, a choice at the data specification level is that only one of a list of types is provided. Language related implementations could be assisted by annotations or information external to the specification. 

### Enumerated types

Enumerated types are strictly defined as being a Sum Type. For instance, you could define an enumerated type as a choice from a set of tokens:

    status: enum( GOOD, BAD )
    
Many data formats also use the term enum as a restriction on an atomic type. For instance:

    status: integer( enum( 1 = GOOD, 0 = BAD ))
    
Both designs are valid, however, they are different. It's also useful to point out that when it comes to implementing the set based version of enumerated types it requires the values to be mapped into an atomic type. For instance Java makes it easier to map the tokens to strings rather than integers.


### Annotations and containers

When you review a wide variety of both serialization formats and schemas you can see that a combination or Product Type and Sum Types are central to the structural parts of the design. However, there's plenty of instances where another dimension is added. A clear example of this is XML attributes. Attributes offer an opportunity to add additional information that belongs outside the core structure of the data. Early HTML styles are a good example of how this worked in practice; additional information that was optionally processed could be provided to add font styles to the page. Annotations in Java are also a good example of additional metadata applied to the class that can be optionally used for a wide variety of purposes. 

Returning to the conceptual framework of serialization, "being arranged in series", how do annotations fit? I think of annotations as being the sticky note of serialization. It acts as a note outside the serialized form. Think of the tokens A B in a series. Product types and Sum types allow specifying the valid order. Annotations are outside the series and provide additional information.

            comment "this is an anomaly" 
    A B A B A A B A B A B

The concept of annotations doesn't appear often in schemas (it exists in [CapnProto schema spec](https://capnproto.org/language.html)). In the context of a schema, annotations can be used to add language mapping information, comments or other formal information to a definition. For example:

```
    @java( type = "interface")
    shape: union( circle, square )
```

Given the usefulness of annotations in this situation, there's no reason this would not extend to applications for any structured data. It should also be possible for an annotation itself to also have annotations. The syntax for this for this would require further exploration. 

An annotation can be thought of as an application of a container structure. Container structures often occur in data format designs. A container provides a header, length and possibly a type identifier. They allow a reader to skip over the data in the case it might not understand or not want to read the value. Containers as a concept are required because the end serialization format must result in a serial form. Therefore the previous example would need to be:

            
    A B A B (comment "this is an anomaly") A A B A B A B

Using product types, sum types and annotations/containers as compositional forms, I believe all data structures could be specified. This will need to be tested against reality, but seems correct.

### Namespaces (packages and groups)

As a specification grows its very likely that the names given to types (production rules) that there will be name conflicts. XML and Avro uses namespaces, Profobufs uses Package and ASN.1 use modules. No need to discuss this further at this stage, other than the problem exists and there's plenty of ways to resolve. I'll use the term namespace in preference to other terms.

### Atomic Types

An atomic type or atom is any data which can not be further broken down into logical component parts. An atom is a conceptual idea rather than an implementation. For instance the integer 42 can be represented in a wide variety of ways; as binary using signed, unsigned or variable length values or as text. A Gregorian date or timestamp can also have a wide variety of both binary and text based formats. An atom can also have numerous language implementations and in-memory representations. This results in an atom being conceptual rather than having a physical representation. 

This idea that an atom is conceptual rather than material is useful, but it also complicates the concept of how to specify an atom. Most (if not all) serialization formats restrict the number of atoms to a know set. 

Defining an atom as a conceptual element needs to be specified by using some other external specification or system. For instance a date is from the Gregorian calendar system, a boolean is from boolean logic and an integer is from number theory. While these concepts are already deeply embedded in languages already, it is useful to step back and separate the conceptual from the implementation. Once that is done, we can get a step closer to proper separation of conceptual and data formats.

An outcome of the above is that the concepts that need to be defined by metadata are likely different for each conceptual type with little overlap between them. It is also difficult to be completely abstract in the definition of an abstract type. A text formatted date may require a pattern, however when represented in binary the pattern is not required. 

A detailed investigation into the various atomic types are discussed later.

### Macros (lambdas?) for data type specifications

Using production rules to define types with records, unions and atomic types provides a way of defining concrete data structures. One area that doesn't get a lot of focus in serialization libraries is using macros to define meta types. Consider the container use case; it would be possible to define a generic container with annotations as:

    container: record( array(any) annotations, any value )
    
While this works, it doesn't provide any way of being specific about the type of values held by the container. A macro might be defined as:

    container( annotationType, valueType ): record(array(annotationType) annotations, valueType value)
    
    shape: union( circle, square )
    shapeAnnotation: union()
    shapeContainer: container( shapeAnnotation, shape ) 

The shapeContainer would be expanded to:

    shapeContainer: record( array( shapeAnnotation ) annotations, shape value )

There's a question here about using both type substitution, value substitution or something else. ASN.1 is one example of a solution that allows macros. The whole type system is already a sort of macro expansion system, so this concept needs more exploration. Here's a potential draft of the type system defined using record, union with simple expansion rules that doesn't need macros. The syntax uses [] for array instances and  __name( ... )__  as type instances:

    record: record( [ field( array( ref(field) ), "fields" ) ] )
    field: record( [ field( ref( type ), "type"), field( ref(name), "name" ) ] )
    array: record( [ field( ref( type ), "type" ) ] )
    union: record( [ field( array( ref(type) ), "types" ) ] )
    type: union( [ ref(ref), ref(union), ref(record) ] )
    ref: record([ field( ref(typeName), "type") ])
    
    typeName: string           // strings to be defined later. 
    identifier: string 

The definitions define the constructors for the core set of [abstract data types](https://en.wikipedia.org/wiki/Abstract_data_type).  The concept of allowing the type system to define its own structure is not just a fun trick. It can become useful when doing type system comparisons between servers over time, and was used in [Argot](https://github.com/argotsdk/argot-java) originally. It allows the type system itself to change overtime rather than being static from the day it was defined. The whole list of meta types would be likely identified using a version number rather than transferring the complete meta dictionary types list.


### Composability and dealing with change

A core purpose of a metadata schema is to allow a receiver to validate if the data conforms to the structure of data it is able to accept. What that means is that the sender may have written the data with a different version of the schema than what the receiver understands. However, if the data also included the schema, it isn't very useful to just compare the internal and external schemas as a whole. While portions of the schema might be different, the data might validate against the portions of the schema that are the same. The data might then be rejected even though the data could be processed. Composability of the model is the idea that the data might only contain the metadata for the parts of the schema that is encoded in the data. When the data is received, the receiver can validate that only the portions of schema are matching. 

A good example of where composability would be useful is enumerated types. Take for example:

    status: enum( GOOD, BAD );
   
A sender has a definition of "status" that can either be GOOD or BAD. However, a receiver has defined status as:

    status: enum( GOOD, BAD, UGLY );
   
It would be easy to suggest that as the definitions do not match the data should be rejected, even though the senders data fits easily inside the definition of the receiver. If we modify the definition to be composable:

    status: enum( GOOD );
    status: enum( BAD );
    
and the receiver as:

    status: enum( GOOD );
    status: enum( BAD );
    status: enum( UGLY );
   
The sender might only embed the definition of the type being used. This allows the receiver to validate the data and accept it. It also allows data to flow in the opposite direction as long as enum( UGLY ) is not used in the data stream. This also removes the need for explicit version control. As long as a data stream contains a partially embedded schema and conforms to the receivers internal schema the data can be processed.

There's implications in the above for the rest of the schema design and how data can be validated. This is that if each element of the schema is composable then any data with embedded data can be validated using the equivalent of a tree walk algorithm that compares the sender and receivers partial schemas. It would be data format specific on whether enough embedded information is included to perform the required comparisons.

It's worth noting that the definitions of the type system do not need to be defined using individual definitions as above. The first definition, "status: enum( GOOD, BAD )" could easily be decomposed into individual definitions internally. 

Expanding on the example above, we can formally define validation between two schemas as finding the subset of rules that match between two grammars. Let's use another simple example:

    shapeList: array( shape );
    shape: union( circle, square );
    circle: record( integer x, integer y, integer radius );
    square: record( integer x, integer y, integer width, integer height );
   
If a sender was to transmit:

    circle( x=1, y=2, radius=10 )
   
the full schema is not required as only a circle was used, as such the sent grammar can be pruned to become:

    shape: union( circle );
    circle: record( integer x, integer y, integer radius );
   
The receiver can now perform a simple comparison (tree walk) to verify that the sent grammar can be overlaid on the receivers grammar. This could also be applied to records, however, would require default values to be provided for missing components. For example the receiver might define circle as:

    circle: record( integer x, integer y, integer radius, string name=default("unknown") );
   
This signifies that while "name" could be provided, if it is not provided a default value is set. In this case the sender grammar can still be matched. If we break the tuple up as individual production rules, it is shown that each link is either required or optional.

    circle: record( integer x);
    circle: record( integer y);
    circle: record( integer radius);
    circle: record( string name=default("unknown") );

This also shows what has been known about many other schema systems; adding new optional elements to a schema allows for backward compatibility. However, it is not always possible to make non-breaking changes, so some type of version control is required. Take for example schemas that have defined square as:

    square: record( integer x,  integer y, integer width, integer height );
    square: record( integer x1, integer y1, integer x2, integer y2 );
   
It isn't enough to alias particular values, as each would require different ways to construct an object. While it might be useful to add a note that the first production is preferred (or is version 2), it doesn't effect the grammar matching requirement. The need for versioning should be explored further, however, from this simple example, adding versioning does not assist the solution. 

However, when sending data, there might be a preference to use one of over the other. As there's no versioning, in this instance an annotation would be useful.

    square: record( integer x,  integer y, integer width, integer height );
    
    @deprecated
    square: record( integer x1, integer y1, integer x2, integer y2 );

An annotation would not form part of the production rule in the sense that it does not need to be matched, but might provide additional information useful for the sender or receiver.

Another set of breaking rule might be something like:

    shape: union( circle, quadrilateral );
    qaudrilateral: union(square, rectangle);
    circle: record( integer x, integer y, integer radius );
    square: record( integer x,  integer y, integer width );
    square: @deprecated @alias rectangle;
    rectangle: record( integer x,  integer y, integer width, integer height );
    
The sender with an older schema sends:

    shape: union( square );
    square: record( integer x,  integer y, integer width, integer height );  
    square( x=1, y=2, width=10, heigth=10 );
    
The receiver is still able to collapse the new schema to match the older data. The shape and quadrilateral are collapsed and the alias to rectangle is matched. There will of course be many examples of breaking changes, however, through the use of multiple rule matching and substitution it should be easier to define backward compatibility.

Another property that is implied by the above example is that there can be multiple definitions for a type name. Any reference for type substitution does not specify the rule to be matched. It would be up to the implementation and data format to verify if the correct rule can be matched.


### Optional field matching

Using the concept of composability, a record could be specified using a list of fields:

    point: record( integer x);
    point: record( integer y);
    
The concept of making a field required or optional has been discussed numerous times by other schema/protocol solutions [here](https://capnproto.org/faq.html#how-do-i-make-a-field-required-like-in-protocol-buffers), [here](https://stackoverflow.com/questions/31801257/why-required-and-optional-is-removed-in-protocol-buffers-3) and [here](https://github.com/protocolbuffers/protobuf/issues/2497). This is a strong argument for not having required fields:

>  Many required fields were "obviously" required until... they weren't. Let's say you have an
> id field for a Get method. That is obviously required. Except, later you might need to 
> change the id from int to string, or int32 to int64. That requires adding a new muchBetterId 
> field, and now you are left with the old id field that must be specified, but eventually is 
> completely ignored.

The problem with this is that older solutions will not be able to accept newer data if a field has changed id from int to string. Managing change is particular difficult for distributed systems and there isn't a silver bullet to solve this. However, the solution of making everyting optional seems to offer no guarantee of anything regarding the specification.

Let's assume that if a field is specified then a value is required unless a default value is supplied. Exploring this idea with the circle record example:

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

In summary a field could have @optional and @default( value ) as annotations. A record can have an @IgnoreAdditionalFields (note: needs a more concise name). Annotations are not transferred as part of any data metadata and only included for end points to assist in processing and matching data to schemas. 

    
    @IgnoreAdditionalFields
    circle:  record( [ integer x, integer y, 
                       integer radius, @default("unknown") string name ] );

    @IgnoreAdditionalFields
    contact:  record( [ @optional string firstName, 
                        @optional string lastName ] );
    


### Array discussion

An array specifies a collection of elements. An array can contain either a single type of repeated elements or can contain a mixed set of elements. The differences between existing schemas is quite surprising on how much they diverge. XML Schema embeds the concept of repeated elements by adding minOccurs/maxOccurs attributes. Protobufs simply allows a "repeated" keyword. Avro allows specifying the type of array only, and JSON is in its own world.

JSON Schema allows a complex set of constraints for [arrays](https://ajv.js.org/docs/json-schema.html#keywords-for-arrays). Like many other schemas, this includes min/max items to constrain the number of items. However, it also includes the ability to constrain to uniqueItems which should invalidate based on content. And includes min/max contains which allows enforcing the number of items of a particular type are present while ignoring other items. There's also a few other constraints which creates a very flexible way of validating the contents of an array. It's a really good example of building a schema based on a data format. 

It's worth exploring a few of these ideas as they don't fit neatly into the definition of array defined earlier. It is also a good example to show that building a schema solution that covers multiple data formats can not re-create the complexities and nuanced decisions of all other schema languages.

  * minItems/maxItems: This restricts the number of items in an array. This is equivalent to XML Schema minOccurs and maxOccurs. 
  
  * items: This allows specifying specific types onto an array. This is interesting as it blurs the lines between a tuple and an array. By specifying the order and types of an array, an unnamed tuple is defined. additionalItems is another option used with the items concept, it allows additional items in the array beyond what is specified by the items list. As the JSON format allows putting any types into an array, this just opens the door for added items beyond what is required. The items attribute is also used to constrain values to a specific type.
  
  * contains/minContains/maxContains: The contains attribute makes an array valid if it contains at least one item that is valid. This is quite a strange restraint that has a different purpose to the items constraint.
  
  * other: JSON schema allows very complex evaluation rules that contain sub schemas, if/then/else based on data values and a variety of other constrains.
  
The idea of constraints based on uniqueItems, if/then/else conditions and a wide variety of other concepts raises the question of how far should a schema specification go in performing the data validation. There has to become a line where procedural language concepts enter that go beyond a data template.

Another interesting element to JSON Schema arrays is that it allows for anonymous elements. By saying that the first n-elements are typed and named and then adding the attribute additionalItems is true, then any random other items can be added to the list. This seems to be like providing half a specification and saying "stuff goes here".  This is an area worth finding use cases. However, I'd say most use cases can be resolved using the "any" concept.

The way JSON Schema allows complex validation of array values makes it worth taking a side step to investigate a bit further the underlying structure of tuples and arrays. JSON Schema was likely developed in the way it was in part because of the way JavaScript treats an array as an ordered set of any type of object. This is quite different to Java where an array is an ordered list of a specific type.

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
    pointType: union( 2dPoint, 3dPoint );
    2dPoint: record( integer x, integer y);
    3dPoint: record( integer x, integer y, integer z);
    
Returning to JSON Schema, it would allow schema choice based on another value:

    pointTypeEnum: union( 2DPOINTS, 3DPOINTS );
    pointsRecord: record( pointTypeEnum type, if( type == 2DPOINTS, array(2dPoint), array(3dPoint)));
    
Adding this type of "if" construction to a schema is doable, however, the question is if the added complexity is worth adding. JSON Schema also allows creating pseudo record structures on top of a JSON array. It is obviously doable, but is it sensible. Something to add to the future discussion list.


### Atomic types

As discussed earlier, each atomic type requires its own set of definitions. The following is a very cursory glance at various common atomic types. Further investigation is required to develop a set of metadata abstract data type specifications for each type. Ideally, the metadata model is built using the same record and union types already explored. The individual mappings of each type would then need to be developed.

Ideally, it would be possible to create a model that allows defining new atomic types using a common set of metadata. A lot more work is required here once the core types of record and union are settled.

#### Boolean

This is the most simple to define. The XML Schema specification provides a good definition.

    boolean represents the values of two-valued logic. Boolean has the value space of {true, false}.
  
From a metadata point of view there's nothing else we can say about a boolean data value other than it is a boolean. The values "true" and "false" will also need to be defined.

Maybe something like. This is recursive definition, but using @atom annotation allows breaking endless loops: 

    boolean: @atom enum( true, false );
    true: @atom boolean(true);
    false: @atom boolean(false);
    

#### Integer (Decimal)

It's very easy to connect the concept of integer with the implementation of integer in hardware as a value 32-bit int, or signed 16-bit short. However, as a data language it is useful to attempt to describe the an integer more universally. For a given application, an integer might represent a persons age; in this case a valid range might be 0 to 150 (leaving some room for improvements in longevity).  In an implementation that might be mapped to a uint8, or a signed int depending on the language. Having said that, we're always implementing on a base 2 system, so as developers we're more likely to specify uint8 as a constraint than 0 to 150.

XML Schema has options of totalDigits, fractionDigits, pattern, whiteSpace, enumeration, maxInclusive, maxExclusive, minInclude and minExclusive as constraints on the integer type. The concept of starting with an "integer" type and then applying constraints to reduce the range is a good basis. Ideally, it would be useful to be able to specify the range, min/max values without recording the actual min/max values like XML Schema. e.g.

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
    
All integer types are going to be @atom types, as such, the metadata is more to assist developers implementing atoms rather than being useful at runtime.

ASN.1 uses a simple range or enumeration and does not specify the implementation. The following examples are taken from [here](https://www.oss.com/asn1/resources/asn1-made-simple/advanced-constraints.html):

    CarSpeed ::= INTEGER (0..200)
    CitySpeedLimit ::= INTEGER (25 | 30 | 40)
    HighwaySpeedLimit ::= INTEGER (40 | 50 | 60 | 70)
    SpeedLimitSigns ::= INTEGER (CitySpeedLimit | HighwaySpeedLimit | 10 | 65)
    RuralSpeedLimit ::= INTEGER (CitySpeedLimit INTERSECTION HighwaySpeedLimit)
    
For small integers specifying a value or range in this way is no problem. This gets more difficult when the physical implementation needs to be taken into consideration. Adding a max value of 2^128 is more difficult. 
    
#### Real (floating point)

Real numbers are most commonly implemented as an IEEE Floating point number as float or double, but could be implemented using fixed-point representation or [other representation](https://en.wikipedia.org/wiki/Floating-point_arithmetic#Other_notable_floating-point_formats).

Possible real restrictions include:

    minimum: Minimum value (inclusive).
    maximum: Maximum value (inclusive).
    standard: For example, this could include ieee756-single for a float.
    enumeration: A list of allowed values.

In a similar way to integers, all real types are specified using metadata. However, any metadata is to assist developers implementing rather than being particularly useful at runtime.

    float: @atom real( restriction = [ standard( ieee756-single ) ] );
    
Even though common representation is ieee756, there are a wider variety of implementations that could be useful depending on the situation.

An interesting property of the implementation of real numbers as text or ieee756 format is that they both have different ways of implementing precision.  As such, you won't get exact conversions going between back and forth between text and ieee756. 

Something to explore is that both integer and real are both numbers. It may be better to use a set of restrictions on a number type rather than keeping them separate. However, the implementation is different enough that keeping the separate probably makes more sense.

It needs to be possible to further restrict a already defined real atom type. For instance, a user might want to say that a latitude is a float with minimum -90 and maximum +90. 


#### Strings and encodings

It is becoming near on universal that a "string" in many data formats means a unicode encoded array of characters (JSON, XML, CapnProto, Avro, ProtoBuf, etc). For this reason it is worthwhile following the trend. As Unicode can include multiple characters the length of a string may be different to the number of bytes that a string uses.

Common restrictions for strings include:

    length: Fixed number of characters.
    minLength: Minimum number of characters.
    maxLength: Maximum number of characters.
    pattern: A regular expression that needs to be matched for characters.
    enumeration: A list of allowed values.
    
One thing that needs further research is a standard for regular expressions that has a common implementation across systems. This is also mentioned in the [CDDL specification](https://tools.ietf.org/html/rfc8610#section-3.8.3.2).

There also needs to be ways of describing other encodings such as ASCII.

#### Date and Timestamp

Most systems that say date are referring to a [Gregorian Date](https://en.wikipedia.org/wiki/Gregorian_calendar), however there are other date systems still [in use today around the world](https://en.wikipedia.org/wiki/History_of_calendars#Modern_calendars). 

Specifying a date in generic way without reference to either a text or binary format makes it more difficult to than a definition using just a text format. 

Many text based schemas and data formats will rely on providing a date pattern or default to a specific date format. [XML dates](https://www.w3.org/TR/xmlschema-2/#date) includes built in data types for duration, dateTime, time, date, gYearMonth, gYear, gMonthDay gDay, and gMonth. XML leans heavily on ISO8601 standard for dates. XML Schemas allows the following restrictions:
 
    enumeration: restrict to a set list of values.
    maxExclusive: Maximum exclusive of given value.
    maxInclusive: Maximum inclusive of given value.
    minExclusive: Minimum exclusive of given value.
    minInclusive: Minimum inclusive of given value.
    pattern: Allows specifying a specific pattern for the value.
    whiteSpace: How to treat whitespace which allows automatic trimming.
 
An issue with specifying date-time formats is which pattern definition to use. The [Unicode CLDR](http://cldr.unicode.org/translation/date-time-1/date-time-patterns) is emerging as the most likely lead for text based date format specifications. ISO8601 formats are likely the better choice for text based interchange patterns.

As dates and timestamps are often encoded using an underlying type (string, int, long) they often don't need their own data type. Avro uses [Logical Types](https://avro.apache.org/docs/1.8.0/spec.html#Logical+Types) to define the dates and timestamps. That's likely a good example of how



    


  

   
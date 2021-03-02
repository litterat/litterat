
Litterat Model - Abstract Data Model Library for Serialization
------------------------------------------------


The Litterat Model library is for creating cross platform abstract data model for serialization. The Litterat model is based on the theoretical serialization model explained in the [Litterat theory document](https://github.com/litterat/litterat/blob/main/litterat-theory.md). The model can be used as a format independent abstract model in a similar way to ASN.1 (Abstract Syntax Notation). The model can also be used as an abstract model to convert between different data schema languages. The model is used in conjunction with the Litterat language bind module. 

The Litterat Model is defined by the following [abstract data types](https://en.wikipedia.org/wiki/Abstract_data_type):

 * [record](https://en.wikipedia.org/wiki/Record_(computer_science): A finite list of fields having both a name and type assigned to each element. Each field being required or optional.
 * [union](https://en.wikipedia.org/wiki/Tagged_union): An invariant type that can hold one of a list of types.
 * array: A repeated list of a selected type.
 * attribute: Data that is associated with an instance of another data type.
 * namespace: 







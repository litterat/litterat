/*
 * Copyright (c) 2020-2021, Live Media Pty. Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.litterat.xpl.resolve;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClassArray;
import io.litterat.bind.DataClassField;
import io.litterat.bind.DataClassRecord;
import io.litterat.model.Array;
import io.litterat.model.Atom;
import io.litterat.model.Definition;
import io.litterat.model.Field;
import io.litterat.model.Record;
import io.litterat.model.TypeName;
import io.litterat.model.Union;
import io.litterat.model.atom.StringAtom;
import io.litterat.model.function.FunctionSignature;
import io.litterat.model.library.TypeException;
import io.litterat.model.library.TypeLibrary;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.TypeMapEntry;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.TypeReader;
import io.litterat.xpl.TypeResolver;
import io.litterat.xpl.TypeStream;
import io.litterat.xpl.TypeWriter;
import io.litterat.xpl.lang.Block;
import io.litterat.xpl.lang.ConstructInstance;
import io.litterat.xpl.lang.Expression;
import io.litterat.xpl.lang.FieldRead;
import io.litterat.xpl.lang.FieldSet;
import io.litterat.xpl.lang.Lambda;
import io.litterat.xpl.lang.LambdaFunction;
import io.litterat.xpl.lang.ReadArray;
import io.litterat.xpl.lang.ReadValue;
import io.litterat.xpl.lang.ReturnNode;
import io.litterat.xpl.lang.SlotAssigner;
import io.litterat.xpl.lang.SlotReference;
import io.litterat.xpl.lang.SlotSet;
import io.litterat.xpl.lang.Statement;
import io.litterat.xpl.lang.WriteArray;
import io.litterat.xpl.lang.WriteValue;
import io.litterat.xpl.lang.interpret.LitteratInterpreter;

public class SchemaResolver implements TypeResolver {

	private TypeMap typeMap;
	private TypeLibrary library;

	public SchemaResolver(TypeMap map) {
		this.typeMap = map;
		this.library = map.library();
	}

	@Override
	public TypeMapEntry map(TypeName name) throws TypeException {

		TypeMapEntry result = null;

		Definition definition = library.getDefinition(name);
		if (definition instanceof Record) {
			Record sequence = (Record) definition;
			DataClassRecord dataClass = (DataClassRecord) library.getTypeClass(name);

			result = new TypeMapEntry(0, name, definition,
					generateSequenceReaderConstructor(typeMap, sequence, name, dataClass),
					generateSequenceWriter(typeMap, sequence, name, dataClass));
		} else if (definition instanceof Union) {
			UnionReaderWriter union = new UnionReaderWriter(name);
			result = new TypeMapEntry(0, name, definition, union, union);
		} else if (definition instanceof Atom) {
			if (name.equals(TypeLibrary.STRING)) {
				new TypeMapEntry(0, name, definition, new StringReaderWriter.StringReader(),
						new StringReaderWriter.StringWriter());
			} else {
				new TypeMapEntry(0, name, definition, TransportHandles.getReader(name),
						TransportHandles.getWriter(name));
			}
		} else if (definition instanceof TypeName) {
			TypeName type = (TypeName) definition;
			TypeReader refReader = typeMap.getEntry(type).reader();
			TypeWriter refWriter = typeMap.getEntry(type).writer();
			result = new TypeMapEntry(0, name, definition, refReader, refWriter);
		} else if (definition instanceof StringAtom) {

			result = new TypeMapEntry(0, name, definition, new StringReaderWriter.StringReader(),
					new StringReaderWriter.StringWriter());

		} else {
			throw new TypeException(
					"failed to map " + name.toString() + " with definition " + definition.getClass().getName());
		}

		return result;
	}

	@Override
	public TypeName mapReverse(int streamId) throws TypeException {
		throw new TypeException("unable to map reverse");
	}

	// TODO this needs more work. Should start as DataClass and look at each type.
	private static TypeWriter generateSequenceWriter(TypeMap typeMap, Record sequence, TypeName typeName,
			DataClassRecord dataClass) throws TypeException {
		try {

			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			int varObject = slots.getSlot(dataClass.typeClass());

			DataClassField[] dataClassFields = dataClass.fields();
			Field[] sequenceFields = sequence.fields();

			List<Statement> statements = new ArrayList<>();
			for (int x = 0; x < dataClassFields.length; x++) {

				Field field = sequenceFields[x];
				DataClassField dataClassField = dataClassFields[x];

				if (field.type() instanceof TypeName) {
					TypeName type = (TypeName) field.type();
					Statement writeField = new WriteValue(type,
							new FieldRead(new SlotReference(varObject), typeName, field.name()));
					statements.add(writeField);
				} else if (field.type() instanceof Array) {

					Array array = (Array) field.type();
					DataClassArray dataArray = (DataClassArray) dataClassField.dataClass();

					Class<?> arrayClss = typeMap.library().getTypeClass(array.type()).typeClass();
					int loopSlot = slots.getSlot(arrayClss);
					Expression readField = new FieldRead(new SlotReference(varObject), typeName, field.name());
					Statement writeElement = new WriteValue(array.type(), new SlotReference(loopSlot));

					Statement loop = new WriteArray(dataArray, loopSlot, readField, writeElement);
					statements.add(loop);
				} else {
					throw new TypeException("Not recognised field element");
				}
			}

			Statement[] statementArray = new Statement[statements.size()];
			Block blockNode = new Block(statements.toArray(statementArray));

			Lambda lambda = new Lambda(new FunctionSignature(TypeLibrary.VOID, new TypeName("vm", "output"), typeName),
					slots.getSlots(), blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();

			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeWriter(lambdaFunction, dataClass);

		} catch (NoSuchMethodException | IllegalAccessException | TypeException | DataBindException e) {
			throw new TypeException(e);
		}
	}

	@SuppressWarnings("unused")
	private static TypeReader generateSequenceReader(TypeMap typeMap, Record sequence, TypeName typeName,
			DataClassRecord dataClass) throws TypeException {

		try {
			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			int varName = slots.getSlot(dataClass.dataClass());

			List<Statement> statements = new ArrayList<>();
			statements.add(new SlotSet(varName, new ConstructInstance(typeName, new Expression[0])));

			DataClassField[] dataClassFields = dataClass.fields();
			Field[] sequenceFields = sequence.fields();

			for (int x = 0; x < sequenceFields.length; x++) {
				Field field = sequenceFields[x];
				if (field.type() instanceof TypeName) {
					TypeName type = (TypeName) field.type();
					statements
							.add(new FieldSet(new SlotReference(varName), new ReadValue(type), typeName, field.name()));
				}
			}
			statements.add(new ReturnNode(new SlotReference(varName)));

			Statement[] statementArray = new Statement[statements.size()];
			Block blockNode = new Block(statements.toArray(statementArray));

			Lambda lambda = new Lambda(new FunctionSignature(typeName, new TypeName("vm", "input")), slots.getSlots(),
					blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();
			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeReader(lambdaFunction, dataClass);

		} catch (NoSuchMethodException | IllegalAccessException | TypeException | DataBindException e) {
			throw new TypeException(e);
		}

	}

	private static TypeReader generateSequenceReaderConstructor(TypeMap typeMap, Record sequence, TypeName typeName,
			DataClassRecord dataClass) throws TypeException {

		try {
			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			// int varName = slots.getSlot(clss);

			List<Statement> statements = new ArrayList<>();

			Expression[] constructorBlock = new Expression[sequence.fields().length];

			DataClassField[] dataClassFields = dataClass.fields();
			Field[] sequenceFields = sequence.fields();

			for (int x = 0; x < sequenceFields.length; x++) {
				Field field = sequenceFields[x];
				DataClassField dataClassField = dataClassFields[x];

				if (field.type() instanceof TypeName) {
					TypeName type = (TypeName) field.type();
					// int varReadSlot = slots.getSlot(clss);
					// statements.add(new SlotSet(varReadSlot, new
					// ReadType(field.type().toString())));
					constructorBlock[x] = new ReadValue(type);
				} else if (field.type() instanceof Array) {
					Array array = (Array) field.type();
					DataClassArray dataArray = (DataClassArray) dataClassField.dataClass();

					constructorBlock[x] = new ReadArray(dataArray, array, new ReadValue(array.type()));
				} else {
					throw new TypeException("not recognised type: " + field.type().getClass().getName());
				}
			}

			// BlockArray blockArray = new BlockArray(constructorBlock);

			// statements.add(new SlotSet(varName, new CreateInstance(typeName,
			// constructorBlock)));
			// statements.add(new ReturnNode(new SlotReference(varName)));
			statements.add(new ReturnNode(new ConstructInstance(typeName, constructorBlock)));

			Statement[] statementArray = new Statement[statements.size()];
			Block blockNode = new Block(statements.toArray(statementArray));

			Lambda lambda = new Lambda(new FunctionSignature(typeName, new TypeName("vm", "input")), slots.getSlots(),
					blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();

			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeReader(lambdaFunction, dataClass);

		} catch (NoSuchMethodException | IllegalAccessException | DataBindException e) {
			throw new TypeException(e);
		}

	}

	private static class LambdaTypeReader implements TypeReader {

		private final LambdaFunction readerLambda;
		private final MethodHandle toObject;

		public LambdaTypeReader(LambdaFunction reader, DataClassRecord dataClass) {
			this.readerLambda = reader;
			this.toObject = dataClass.toObject();

		}

		@Override
		public Object read(TypeInputStream reader) throws IOException {
			try {
				return toObject.invoke(readerLambda.execute(reader));
			} catch (Throwable e) {
				throw new IOException("Failed to read", e);
			}
		}
	}

	private static class LambdaTypeWriter implements TypeWriter {

		private final LambdaFunction writerLambda;
		private final MethodHandle toData;

		public LambdaTypeWriter(LambdaFunction writer, DataClassRecord dataClass) {
			this.writerLambda = writer;
			this.toData = dataClass.toData();
		}

		@Override
		public void write(TypeOutputStream writer, Object o) throws IOException {
			try {
				writerLambda.execute(writer, toData.invoke(o));
			} catch (Throwable e) {
				throw new IOException("Failed to write", e);
			}
		}

	}

}

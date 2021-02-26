/*
 * Copyright (c) 2003-2020, Live Media Pty. Ltd. All Rights Reserved.
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

import io.litterat.bind.PepDataClass;
import io.litterat.bind.PepException;
import io.litterat.model.TypeException;
import io.litterat.model.TypeLibrary;
import io.litterat.model.types.Array;
import io.litterat.model.types.Atom;
import io.litterat.model.types.Definition;
import io.litterat.model.types.Encoding;
import io.litterat.model.types.Field;
import io.litterat.model.types.Record;
import io.litterat.model.types.Reference;
import io.litterat.model.types.Signature;
import io.litterat.model.types.TypeName;
import io.litterat.model.types.Union;
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
			PepDataClass dataClass = library.getTypeClass(name);

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
		} else if (definition instanceof Reference) {
			Reference reference = (Reference) definition;
			TypeReader refReader = typeMap.getEntry(reference.type()).reader();
			TypeWriter refWriter = typeMap.getEntry(reference.type()).writer();
			result = new TypeMapEntry(0, name, definition, refReader, refWriter);
		} else if (definition instanceof Encoding) {
			// Strings need more work.
			if (name.equals(TypeLibrary.STRING)) {
				result = new TypeMapEntry(0, name, definition, new StringReaderWriter.StringReader(),
						new StringReaderWriter.StringWriter());
			} else {
				throw new TypeException(
						"failed to map " + name.toString() + " with definition " + definition.getClass().getName());

			}
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

	private static TypeWriter generateSequenceWriter(TypeMap typeMap, Record sequence, TypeName typeName,
			PepDataClass dataClass) throws TypeException {
		try {

			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			int varObject = slots.getSlot(dataClass.dataClass());

			List<Statement> statements = new ArrayList<>();
			for (Field field : sequence.fields()) {
				if (field.type() instanceof Reference) {
					Reference reference = (Reference) field.type();
					Statement writeField = new WriteValue(reference.type(),
							new FieldRead(new SlotReference(varObject), typeName, field.name()));
					statements.add(writeField);
				} else if (field.type() instanceof Array) {

					Array array = (Array) field.type();

					Class<?> arrayClss = typeMap.library().getTypeClass(array.type()).dataClass();
					int loopSlot = slots.getSlot(arrayClss);
					Expression readField = new FieldRead(new SlotReference(varObject), typeName, field.name());
					Statement writeElement = new WriteValue(array.type(), new SlotReference(loopSlot));

					Statement loop = new WriteArray(loopSlot, readField, writeElement);
					statements.add(loop);
				} else {
					throw new TypeException("Not recognised field element");
				}

			}

			Statement[] statementArray = new Statement[statements.size()];
			Block blockNode = new Block(statements.toArray(statementArray));

			Lambda lambda = new Lambda(new Signature(new Reference(TypeLibrary.VOID), new Reference("vm", "output"),
					new Reference(typeName)), slots.getSlots(), blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();

			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeWriter(lambdaFunction, dataClass);

		} catch (NoSuchMethodException | IllegalAccessException | TypeException | PepException e) {
			throw new TypeException(e);
		}
	}

	@SuppressWarnings("unused")
	private static TypeReader generateSequenceReader(TypeMap typeMap, Record sequence, TypeName typeName,
			PepDataClass dataClass) throws TypeException {

		try {
			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			int varName = slots.getSlot(dataClass.dataClass());

			List<Statement> statements = new ArrayList<>();
			statements.add(new SlotSet(varName, new ConstructInstance(typeName, new Expression[0])));

			for (Field field : sequence.fields()) {
				if (field.type() instanceof Reference) {
					Reference reference = (Reference) field.type();
					statements.add(new FieldSet(new SlotReference(varName), new ReadValue(reference.type()), typeName,
							field.name()));
				}
			}
			statements.add(new ReturnNode(new SlotReference(varName)));

			Statement[] statementArray = new Statement[statements.size()];
			Block blockNode = new Block(statements.toArray(statementArray));

			Lambda lambda = new Lambda(new Signature(new Reference(typeName), new Reference("vm", "input")),
					slots.getSlots(), blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();
			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeReader(lambdaFunction, dataClass);

		} catch (NoSuchMethodException | IllegalAccessException | TypeException | PepException e) {
			throw new TypeException(e);
		}

	}

	private static TypeReader generateSequenceReaderConstructor(TypeMap typeMap, Record sequence, TypeName typeName,
			PepDataClass dataClass) throws TypeException {

		try {
			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			// int varName = slots.getSlot(clss);

			List<Statement> statements = new ArrayList<>();

			Expression[] constructorBlock = new Expression[sequence.fields().length];
			for (int x = 0; x < sequence.fields().length; x++) {

				Field field = sequence.fields()[x];

				if (field.type() instanceof Reference) {
					Reference reference = (Reference) field.type();
					// int varReadSlot = slots.getSlot(clss);
					// statements.add(new SlotSet(varReadSlot, new
					// ReadType(field.type().toString())));
					constructorBlock[x] = new ReadValue(reference.type());
				} else if (field.type() instanceof Array) {
					Array array = (Array) field.type();

					constructorBlock[x] = new ReadArray(array, new ReadValue(array.type()));
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

			Lambda lambda = new Lambda(new Signature(new Reference(typeName), new Reference("vm", "input")),
					slots.getSlots(), blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();

			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeReader(lambdaFunction, dataClass);

		} catch (NoSuchMethodException | IllegalAccessException | PepException e) {
			throw new TypeException(e);
		}

	}

	private static class LambdaTypeReader implements TypeReader {

		private final LambdaFunction readerLambda;
		private final MethodHandle toObject;

		public LambdaTypeReader(LambdaFunction reader, PepDataClass dataClass) {
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

		public LambdaTypeWriter(LambdaFunction writer, PepDataClass dataClass) {
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

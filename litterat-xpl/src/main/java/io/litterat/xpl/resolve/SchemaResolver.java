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
import java.util.ArrayList;
import java.util.List;

import io.litterat.bind.*;
import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.*;
import io.litterat.schema.meta.Record;
import io.litterat.xpl.TypeInputStream;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.TypeMapEntry;
import io.litterat.xpl.TypeOutputStream;
import io.litterat.xpl.TypeReader;
import io.litterat.xpl.TypeStream;
import io.litterat.xpl.TypeWriter;
import io.litterat.xpl.lang.*;
import io.litterat.xpl.lang.interpret.LitteratInterpreter;

public class SchemaResolver {

	private TypeMap typeMap;
	private TypeContext context;

	public SchemaResolver(TypeMap map) {
		this.typeMap = map;
		this.context = map.context();
	}

	public TypeMapEntry register(Typename typename, Class<?> targetClass) throws TypeException {

		DataClass dataClass = context.register(typename, targetClass, targetClass);
		Definition definition = context.library().getDefinition(typename);

		return map(typename, definition, dataClass);
	}

	public TypeMapEntry map(Typename name) throws TypeException {

		DataClass descriptor = context.getDescriptor(name);
		Definition definition = context.library().getDefinition(name);

		return map(name, definition, descriptor);
	}

	public TypeMapEntry map(Typename name, Definition definition, DataClass descriptor) throws TypeException {

		TypeMapEntry result = null;

        switch (descriptor) {
            case DataClassRecord dataClass ->
                    result = new TypeMapEntry(0, name, definition, descriptor, generateSequenceReaderConstructor(typeMap, name, (Record) definition, dataClass),
                            generateSequenceWriter(typeMap, name, (Record) definition, dataClass));
            case DataClassUnion dataClassUnion -> {
                UnionReaderWriter union = new UnionReaderWriter(name);
                result = new TypeMapEntry(0, name, definition, descriptor, union, union);
            }
            case DataClassAtom dataClassAtom -> {
                if (name.equals(Meta.STRING)) {
                    result = new TypeMapEntry(0, name, definition, descriptor, new StringReaderWriter.StringReader(),
                            new StringReaderWriter.StringWriter());
                } else {
                    result = new TypeMapEntry(0, name, definition, descriptor, TransportHandles.getReader(name),
                            TransportHandles.getWriter(name));
                }
            }
            case null, default -> throw new TypeException("failed to map " + name.toString());
        }

		return result;
	}

	// TODO this needs more work. Should start as DataClass and look at each type.
	private static TypeWriter generateSequenceWriter(TypeMap typeMap, Typename typeName, Record sequence, DataClassRecord dataClass)
			throws TypeException {
		try {
			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			int varObject = slots.getSlot(dataClass.typeClass());

			DataClassField[] dataClassFields = dataClass.fields();
			Field[] sequenceFields = sequence.fields();

			List<Statement> statements = new ArrayList<>();
			for (int x = 0; x < dataClassFields.length; x++) {

				Field field = sequenceFields[x];
				DataClassField dataClassField = dataClassFields[x];

				if (field.type() instanceof Typename) {
					Typename type = (Typename) field.type();
					Statement writeField = new WriteValue(type,
							new FieldRead(new SlotReference(varObject), typeName, field.name()));
					statements.add(writeField);
				} else if (field.type() instanceof Array) {

					Array array = (Array) field.type();
					DataClassArray dataArray = (DataClassArray) dataClassField.dataClass();

					Class<?> arrayClss = dataArray.arrayDataClass().typeClass();
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

			Lambda lambda = new Lambda(new FunctionSignature(Meta.VOID, new Typename("vm", "output"), typeName),
					slots.getSlots(), blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();

			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeWriter(lambdaFunction);

		} catch (NoSuchMethodException | IllegalAccessException | TypeException | DataBindException e) {
			throw new TypeException(e);
		}
	}

	@SuppressWarnings("unused")
	private static TypeReader generateSequenceReader(TypeMap typeMap, Typename typeName, Record sequence, DataClassRecord dataClass)
			throws TypeException {

		try {
			SlotAssigner slots = new SlotAssigner(TypeStream.class);

			int varName = slots.getSlot(dataClass.typeClass());

			List<Statement> statements = new ArrayList<>();
			statements.add(new SlotSet(varName, new ConstructInstance(typeName, new Expression[0])));

			DataClassField[] dataClassFields = dataClass.fields();
			Field[] sequenceFields = sequence.fields();

			for (int x = 0; x < sequenceFields.length; x++) {
				Field field = sequenceFields[x];
				if (field.type() instanceof Typename) {
					Typename type = (Typename) field.type();
					statements
							.add(new FieldSet(new SlotReference(varName), new ReadValue(type), typeName, field.name()));
				}
			}
			statements.add(new ReturnNode(new SlotReference(varName)));

			Statement[] statementArray = new Statement[statements.size()];
			Block blockNode = new Block(statements.toArray(statementArray));

			Lambda lambda = new Lambda(new FunctionSignature(typeName, new Typename("vm", "input")), slots.getSlots(),
					blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();
			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeReader(lambdaFunction);

		} catch (NoSuchMethodException | IllegalAccessException | TypeException | DataBindException e) {
			throw new TypeException(e);
		}

	}

	private static TypeReader generateSequenceReaderConstructor(TypeMap typeMap, Typename typeName, Record sequence, DataClassRecord dataClass
			) throws TypeException {

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

				if (field.type() instanceof Typename) {
					Typename type = (Typename) field.type();
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

			Lambda lambda = new Lambda(new FunctionSignature(typeName, new Typename("vm", "input")), slots.getSlots(),
					blockNode);

			LitteratInterpreter compiler = new LitteratInterpreter();
			// LitteratGenerator compiler = new LitteratGenerator();

			LambdaFunction lambdaFunction = compiler.compile(typeMap, lambda);

			return new LambdaTypeReader(lambdaFunction);

		} catch (NoSuchMethodException | IllegalAccessException | DataBindException e) {
			throw new TypeException(e);
		}

	}

	private static class LambdaTypeReader implements TypeReader {

		private final LambdaFunction readerLambda;

		public LambdaTypeReader(LambdaFunction reader) {
			this.readerLambda = reader;
		}

		@Override
		public Object read(TypeInputStream reader) throws IOException {
			try {
				return readerLambda.execute(reader);
			} catch (Throwable e) {
				throw new IOException("Failed to read", e);
			}
		}
	}

	private static class LambdaTypeWriter implements TypeWriter {

		private final LambdaFunction writerLambda;

		public LambdaTypeWriter(LambdaFunction writer) {
			this.writerLambda = writer;
		}

		@Override
		public void write(TypeOutputStream writer, Object o) throws IOException {
			try {
				writerLambda.execute(writer, o);
			} catch (Throwable e) {
				throw new IOException("Failed to write", e);
			}
		}

	}

}

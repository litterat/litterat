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
package io.litterat.xpl.lang.interpret;

import java.util.ArrayList;
import java.util.List;

import io.litterat.bind.DataBindException;
import io.litterat.model.Atom;
import io.litterat.model.Definition;
import io.litterat.model.Record;
import io.litterat.model.TypeName;
import io.litterat.model.Union;
import io.litterat.model.atom.StringAtom;
import io.litterat.model.library.TypeException;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.Block;
import io.litterat.xpl.lang.BlockArray;
import io.litterat.xpl.lang.ConstructInstance;
import io.litterat.xpl.lang.CreateInstance;
import io.litterat.xpl.lang.Expression;
import io.litterat.xpl.lang.FieldRead;
import io.litterat.xpl.lang.FieldSet;
import io.litterat.xpl.lang.Lambda;
import io.litterat.xpl.lang.LambdaFunction;
import io.litterat.xpl.lang.Loop;
import io.litterat.xpl.lang.ReadArray;
import io.litterat.xpl.lang.ReadValue;
import io.litterat.xpl.lang.ReturnNode;
import io.litterat.xpl.lang.SlotReference;
import io.litterat.xpl.lang.SlotSet;
import io.litterat.xpl.lang.Statement;
import io.litterat.xpl.lang.Value;
import io.litterat.xpl.lang.WriteArray;
import io.litterat.xpl.lang.WriteValue;

public class LitteratInterpreter {

	public LambdaFunction compile(TypeMap typeMap, Lambda lambda)
			throws NoSuchMethodException, IllegalAccessException, DataBindException, TypeException {

		return new LambdaInterpreter(lambda, compileBlock(typeMap, lambda.block()));
	}

	private BlockInterpreter compileBlock(TypeMap typeMap, Block block)
			throws NoSuchMethodException, IllegalAccessException, DataBindException, TypeException {

		List<StatementInterpreter> statements = new ArrayList<>();
		for (Statement statement : block.statements()) {
			statements.add(compileStatement(typeMap, statement));
		}

		StatementInterpreter[] compiledStatements = new StatementInterpreter[statements.size()];
		return new BlockInterpreter(statements.toArray(compiledStatements));
	}

	private StatementInterpreter compileStatement(TypeMap typeMap, Statement statement)
			throws NoSuchMethodException, IllegalAccessException, DataBindException, TypeException {

		StatementInterpreter compiledStatement = null;
		if (statement instanceof FieldSet) {
			FieldSet fieldSet = (FieldSet) statement;
			compiledStatement = new FieldSetInterpreter(typeMap, fieldSet,
					compileExpression(typeMap, fieldSet.objectExpression()),
					compileExpression(typeMap, fieldSet.valueExpression()));
		} else if (statement instanceof ReturnNode) {
			ReturnNode retrn = (ReturnNode) statement;
			compiledStatement = new ReturnInterpreter(retrn, compileExpression(typeMap, retrn.expression()));
		} else if (statement instanceof SlotSet) {
			SlotSet slotSet = (SlotSet) statement;
			compiledStatement = new SlotSetInterpreter(slotSet, compileExpression(typeMap, slotSet.expression()));
		} else if (statement instanceof WriteValue) {
			WriteValue writeType = (WriteValue) statement;

			Definition def = typeMap.library().getDefinition(writeType.type());
			if (def instanceof Atom) {

				compiledStatement = new WriteAtomInterpreter(writeType,
						compileExpression(typeMap, writeType.expression()));
			} else if (def instanceof Record) {
				compiledStatement = new WriteObjectInterpreter(writeType,
						compileExpression(typeMap, writeType.expression()));

			} else if (def instanceof TypeName) {
				compiledStatement = new WriteObjectInterpreter(writeType,
						compileExpression(typeMap, writeType.expression()));
			} else if (def instanceof Union) {
				compiledStatement = new WriteObjectInterpreter(writeType,
						compileExpression(typeMap, writeType.expression()));
			} else if (def instanceof StringAtom) {
				compiledStatement = new WriteObjectInterpreter(writeType,
						compileExpression(typeMap, writeType.expression()));
			} else {
				throw new IllegalArgumentException("Write type not recognised: " + def.getClass().getName());
			}
		} else if (statement instanceof Loop) {
			Loop loop = (Loop) statement;
			compiledStatement = new LoopInterpreter(loop, compileExpression(typeMap, loop.arrayExpression()),
					compileStatement(typeMap, loop.loopStatement()));
		} else if (statement instanceof WriteArray) {
			WriteArray loop = (WriteArray) statement;
			compiledStatement = new WriteArrayInterpreter(loop, compileExpression(typeMap, loop.arrayExpression()),
					compileStatement(typeMap, loop.writeStatement()));
		} else {
			throw new IllegalArgumentException("Statement type not recognised: " + statement.getClass().getName());
		}
		return compiledStatement;
	}

	private ExpressionInterpreter compileExpression(TypeMap typeMap, Expression expression)
			throws NoSuchMethodException, IllegalAccessException, DataBindException, TypeException {
		ExpressionInterpreter compiledExpression = null;

		if (expression instanceof ConstructInstance) {
			ConstructInstance createInstance = (ConstructInstance) expression;
			ExpressionInterpreter[] args = new ExpressionInterpreter[createInstance.parameters().length];
			for (int x = 0; x < args.length; x++) {
				args[x] = compileExpression(typeMap, createInstance.parameters()[x]);
			}

			compiledExpression = new ConstructInstanceInterpreter(typeMap, createInstance, args);
		} else if (expression instanceof CreateInstance) {
			CreateInstance createInstance = (CreateInstance) expression;
			compiledExpression = new CreateInstanceInterpreter(typeMap, createInstance);
		} else if (expression instanceof BlockArray) {
			BlockArray blockArray = (BlockArray) expression;
			ExpressionInterpreter[] args = new ExpressionInterpreter[blockArray.statements().length];
			for (int x = 0; x < args.length; x++) {
				args[x] = compileExpression(typeMap, blockArray.statements()[x]);
			}
			compiledExpression = new BlockArrayInterpreter(args);
		} else if (expression instanceof FieldRead) {
			FieldRead fieldRead = (FieldRead) expression;
			compiledExpression = new FieldReadInterpreter(typeMap, fieldRead,
					compileExpression(typeMap, fieldRead.expression()));
		} else if (expression instanceof ReadValue) {
			ReadValue readType = (ReadValue) expression;

			Definition def = typeMap.library().getDefinition(readType.type());
			if (def instanceof Atom) {

				compiledExpression = new ReadAtomInterpreter(readType);
			} else if (def instanceof Record) {
				compiledExpression = new ReadObjectInterpreter(readType);
			} else if (def instanceof TypeName) {
				compiledExpression = new ReadObjectInterpreter(readType);
			} else if (def instanceof Union) {
				compiledExpression = new ReadObjectInterpreter(readType);
			} else if (def instanceof StringAtom) {
				compiledExpression = new ReadObjectInterpreter(readType);
			} else {
				throw new IllegalArgumentException("Write type not recognised: " + def.getClass().getName());
			}
		} else if (expression instanceof ReadArray) {
			ReadArray readArray = (ReadArray) expression;

			compiledExpression = new ReadArrayInterpreter(readArray,
					compileExpression(typeMap, readArray.readExpression()));
		} else if (expression instanceof SlotReference) {
			SlotReference slotReference = (SlotReference) expression;
			compiledExpression = new SlotReferenceInterpreter(slotReference);
		} else if (expression instanceof Value) {
			Value value = (Value) expression;
			compiledExpression = new ValueInterpreter(value);
		} else {
			throw new IllegalArgumentException("Expression type not recognised: " + expression.getClass().getName());
		}

		return compiledExpression;
	}
}

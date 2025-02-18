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
package io.litterat.xpl.lang.mh;

import java.util.ArrayList;
import java.util.List;

import io.litterat.schema.TypeException;
import io.litterat.xpl.TypeMap;
import io.litterat.xpl.lang.Block;
import io.litterat.xpl.lang.ConstructInstance;
import io.litterat.xpl.lang.CreateInstance;
import io.litterat.xpl.lang.Expression;
import io.litterat.xpl.lang.FieldRead;
import io.litterat.xpl.lang.FieldSet;
import io.litterat.xpl.lang.Lambda;
import io.litterat.xpl.lang.LambdaFunction;
import io.litterat.xpl.lang.ReadValue;
import io.litterat.xpl.lang.ReturnNode;
import io.litterat.xpl.lang.SlotReference;
import io.litterat.xpl.lang.SlotSet;
import io.litterat.xpl.lang.Statement;
import io.litterat.xpl.lang.Value;
import io.litterat.xpl.lang.WriteValue;

public class LitteratGenerator {
	public LambdaFunction compile(TypeMap typeMap, Lambda lambda)
			throws NoSuchMethodException, IllegalAccessException, TypeException {

		return new LambdaGenerator(lambda, compileBlock(typeMap, lambda.block()));
	}

	private BlockGenerator compileBlock(TypeMap typeMap, Block block)
			throws NoSuchMethodException, IllegalAccessException, TypeException {

		List<StatementGenerator> statements = new ArrayList<>();
		for (Statement statement : block.statements()) {
			statements.add(compileStatement(typeMap, statement));
		}

		StatementGenerator[] compiledStatements = new StatementGenerator[statements.size()];
		return new BlockGenerator(statements.toArray(compiledStatements));
	}

	private StatementGenerator compileStatement(TypeMap typeMap, Statement statement)
			throws NoSuchMethodException, IllegalAccessException, TypeException {

		StatementGenerator compiledStatement = null;
		if (statement instanceof FieldSet) {
			FieldSet fieldSet = (FieldSet) statement;
			compiledStatement = new FieldSetGenerator(fieldSet, compileExpression(typeMap, fieldSet.objectExpression()),
					compileExpression(typeMap, fieldSet.valueExpression()));
		} else if (statement instanceof ReturnNode) {
			ReturnNode retrn = (ReturnNode) statement;
			compiledStatement = new ReturnGenerator(retrn, compileExpression(typeMap, retrn.expression()));
		} else if (statement instanceof SlotSet) {
			SlotSet slotSet = (SlotSet) statement;
			compiledStatement = new SlotSetGenerator(slotSet, compileExpression(typeMap, slotSet.expression()));
		} else if (statement instanceof WriteValue) {
			WriteValue writeType = (WriteValue) statement;
			compiledStatement = new WriteAtomGenerator(writeType, compileExpression(typeMap, writeType.expression()));
		} else {
			throw new IllegalArgumentException("Statement type not recognised: " + statement.getClass().getName());
		}
		return compiledStatement;
	}

	private ExpressionGenerator compileExpression(TypeMap typeMap, Expression expression)
			throws NoSuchMethodException, IllegalAccessException, TypeException {
		ExpressionGenerator compiledExpression = null;

		if (expression instanceof ConstructInstance) {
			ConstructInstance createInstance = (ConstructInstance) expression;
			ExpressionGenerator[] args = new ExpressionGenerator[createInstance.parameters().length];
			for (int x = 0; x < args.length; x++) {
				args[x] = compileExpression(typeMap, createInstance.parameters()[x]);
			}
			compiledExpression = new ConstructInstanceGenerator(typeMap, createInstance, args);
		} else if (expression instanceof CreateInstance) {
			CreateInstance createInstance = (CreateInstance) expression;
			compiledExpression = new CreateInstanceGenerator(typeMap, createInstance);
		} else if (expression instanceof FieldRead) {
			FieldRead fieldRead = (FieldRead) expression;
			compiledExpression = new FieldReadGenerator(fieldRead, compileExpression(typeMap, fieldRead.expression()));
		} else if (expression instanceof ReadValue) {
			ReadValue readType = (ReadValue) expression;
			compiledExpression = new ReadAtomGenerator(readType);
		} else if (expression instanceof SlotReference) {
			SlotReference slotReference = (SlotReference) expression;
			compiledExpression = new SlotReferenceGenerator(slotReference);
		} else if (expression instanceof Value) {
			Value value = (Value) expression;
			compiledExpression = new ValueGenerator(value);
		} else {
			throw new IllegalArgumentException("Expression type not recognised: " + expression.getClass().getName());
		}

		return compiledExpression;
	}
}

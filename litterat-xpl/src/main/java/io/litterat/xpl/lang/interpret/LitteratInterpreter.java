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

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Array;
import io.litterat.schema.meta.Atom;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Record;
import io.litterat.schema.meta.Typename;
import io.litterat.schema.meta.Union;
import io.litterat.schema.meta.atom.StringAtom;
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

import java.util.ArrayList;
import java.util.List;

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

		return switch (statement) {
            case FieldSet fieldSet ->  new FieldSetInterpreter(typeMap, fieldSet,
                    compileExpression(typeMap, fieldSet.objectExpression()),
                    compileExpression(typeMap, fieldSet.valueExpression()));
            case ReturnNode returnNode ->
                    new ReturnInterpreter(returnNode, compileExpression(typeMap, returnNode.expression()));
            case SlotSet slotSet ->
                     new SlotSetInterpreter(slotSet, compileExpression(typeMap, slotSet.expression()));
            case WriteValue writeType -> {

                Definition def = typeMap.context().library().getDefinition(writeType.type());
				yield switch (def) {
                    case Record _, Typename _, Union _, StringAtom _ -> new WriteObjectInterpreter(writeType,
                            compileExpression(typeMap, writeType.expression()));
                    case Atom _ -> new WriteAtomInterpreter(writeType,
                            compileExpression(typeMap, writeType.expression()));
                    case Array _ -> new WriteObjectInterpreter(writeType,
                            compileExpression(typeMap, writeType.expression()));
                    case null, default ->
                            throw new IllegalArgumentException("Write type not recognised: " + def.getClass().getName());
                };
            }
            case Loop loop ->
                    new LoopInterpreter(loop, compileExpression(typeMap, loop.arrayExpression()),
                            compileStatement(typeMap, loop.loopStatement()));
            case WriteArray loop ->
                    new WriteArrayInterpreter(loop, compileExpression(typeMap, loop.arrayExpression()),
                            compileStatement(typeMap, loop.writeStatement()));
            case null, default ->
                    throw new IllegalArgumentException("Statement type not recognised: " + statement.getClass().getName());
        };
	}

	private ExpressionInterpreter compileExpression(TypeMap typeMap, Expression expression)
			throws NoSuchMethodException, IllegalAccessException, DataBindException, TypeException {

        return switch (expression) {
            case ConstructInstance createInstance -> {
                ExpressionInterpreter[] args = new ExpressionInterpreter[createInstance.parameters().length];
                for (int x = 0; x < args.length; x++) {
                    args[x] = compileExpression(typeMap, createInstance.parameters()[x]);
                }

                yield new ConstructInstanceInterpreter(typeMap, createInstance, args);
            }
            case CreateInstance createInstance ->
                   new CreateInstanceInterpreter(typeMap, createInstance);
            case BlockArray blockArray -> {
                ExpressionInterpreter[] args = new ExpressionInterpreter[blockArray.statements().length];
                for (int x = 0; x < args.length; x++) {
                    args[x] = compileExpression(typeMap, blockArray.statements()[x]);
                }
                yield new BlockArrayInterpreter(args);
            }
            case FieldRead fieldRead -> new FieldReadInterpreter(typeMap, fieldRead,
                    compileExpression(typeMap, fieldRead.expression()));
            case ReadValue readType -> {
                // We've just read a definition which includes a type that hasn't been used yet.
                // By looking up the descriptor with the Typename, we can resolve the definition.
                // This isn't the best way, as the type should have been defined prior to this type
                // in the stream.
                DataClass fieldDataClass = typeMap.context().getDescriptor(readType.type());
                typeMap.context();

                Definition def = typeMap.context().library().getDefinition(readType.type());
                yield switch (def) {
                    case Record record -> new ReadObjectInterpreter(readType);
                    case Typename typename -> new ReadObjectInterpreter(readType);
                    case Union union -> new ReadObjectInterpreter(readType);
                    case StringAtom stringAtom -> new ReadObjectInterpreter(readType);
                    case Atom atom -> new ReadAtomInterpreter(readType);
                    case Array array -> new ReadObjectInterpreter(readType);
                    case null, default ->
                            throw new IllegalArgumentException("Write type not recognised: " + def.getClass().getName());
                };
            }
            case ReadArray readArray -> new ReadArrayInterpreter(readArray,
                    compileExpression(typeMap, readArray.readExpression()));
            case SlotReference slotReference -> new SlotReferenceInterpreter(slotReference);
            case Value value -> new ValueInterpreter(value);
            case null, default ->
                    throw new IllegalArgumentException("Expression type not recognised: " + expression.getClass().getName());
        };
	}
}

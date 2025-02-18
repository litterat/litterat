package io.litterat.core.resolve;

import io.litterat.bind.DataBindException;
import io.litterat.bind.DataClass;
import io.litterat.core.TypeContext;
import io.litterat.schema.TypeException;
import io.litterat.schema.meta.Definition;

import java.lang.reflect.Type;

public interface TypeResolver<D extends Definition, T extends DataClass> {

    D resolveDefinition(TypeContext context, T dataClass, Type parameterizedType)
            throws TypeException, DataBindException;

}

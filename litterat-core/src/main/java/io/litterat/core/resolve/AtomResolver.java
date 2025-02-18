package io.litterat.core.resolve;

import io.litterat.bind.DataClassAtom;
import io.litterat.core.TypeContext;
import io.litterat.schema.meta.Atom;
import io.litterat.schema.meta.atom.*;

import java.lang.reflect.Type;

public class AtomResolver implements TypeResolver<Atom, DataClassAtom> {

    public Atom resolveDefinition(TypeContext context, DataClassAtom dataClassAtom, Type parameterizedType) throws CodeAnalysisException {

        Class<?> clss = dataClassAtom.dataClass();
        if (clss.equals(Boolean.class) || clss.equals(boolean.class)) {
            return new BooleanAtom(new AtomAttribute[]{});
        } else if (clss.isAssignableFrom(Number.class)) {
            return new IntegerAtom(new AtomAttribute[]{});
        } else if (clss.isAssignableFrom(String.class)) {
            return new StringAtom(new AtomAttribute[]{});
        }

        return new UnknownAtom(null);
    }
}

package io.litterat.core.resolve;


import io.litterat.bind.DataClassUnion;
import io.litterat.core.TypeContext;
import io.litterat.core.TypeException;
import io.litterat.core.meta.Typename;
import io.litterat.core.meta.Union;

import java.lang.reflect.Type;

public class UnionResolver implements TypeResolver<Union, DataClassUnion> {


    public Union resolveDefinition(TypeContext context, DataClassUnion dataClass, Type parameterizedType)
            throws TypeException {


        Class<?>[] members = dataClass.memberTypes();

        Typename[] unionMembers = new Typename[members.length];
        for (int x = 0; x < members.length; x++) {
            Class<?> memberClass = members[x];
            unionMembers[x] = context.getTypename(memberClass, memberClass);
        }

        return new Union(unionMembers, dataClass.isSealed());
    }
/*
        io.litterat.bind.annotation.Union union = targetClass.getAnnotation(io.litterat.bind.annotation.Union.class);
        if (union != null) {
            if (union.value() != null && union.value().length > 0) {

                Typename[] unionMembers = new Typename[union.value().length];
                for (int x = 0; x < union.value().length; x++) {
                    Class<?> memberClass = union.value()[x];

                    // The members of the union are not resolved at this point as we
                    // can end up in an infinite loop. By using the actual member classes
                    // the resolution loop is broken.
                    unionMembers[x] = context.getTypename(memberClass, memberClass);
                }

                return new Union(unionMembers, union.sealed());

            } else {
                // Empty union.
                Typename[] unionMembers = new Typename[0];
                return new Union(unionMembers, false);
            }
        }

        // Might need to look for sealed classes/interfaces here.
        throw new CodeAnalysisException("Invalid union");

    }

 */
}

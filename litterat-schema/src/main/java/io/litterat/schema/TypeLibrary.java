package io.litterat.schema;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Meta;
import io.litterat.schema.meta.Typename;
import io.litterat.schema.meta.atom.*;

public class TypeLibrary {

	private final Map<Typename, TypeLibraryEntry> definitionMap;

	public TypeLibrary() {
		this.definitionMap = new HashMap<>();

		try {
			// Atoms
			register(Meta.FLOAT, new RealAtom(new AtomAttribute[]{}));
			register(Meta.BOOLEAN, new BooleanAtom(new AtomAttribute[]{}));
			register(Meta.STRING,  new StringAtom(new AtomAttribute[]{}));
			register(Meta.INT32, new IntegerAtom(new AtomAttribute[]{}));
		} catch (TypeException e) {
			throw new RuntimeException(e);
		}
	}

	private TypeLibraryEntry requireEntry(Typename typename) throws TypeException {
		Objects.requireNonNull(typename, "typename must not be null");
		var entry = definitionMap.get(typename);
		if (entry == null) {
			throw new TypeNotDefinedException(typename);
		}

		return entry;
	}

	public Definition getDefinition(Typename typename) throws TypeException {
		var entry = requireEntry(typename);
		if (entry.state == TypeLibraryState.RESERVED) {
			throw new TypeNotDefinedException(typename);
		}
		return entry.definition();
	}

	public TypeLibraryState getDefinitionState(Typename typename) throws TypeException {
		return requireEntry(typename).state();
	}

	public boolean isRegistered(Typename typename) {
		Objects.requireNonNull(typename, "typename must not be null");
		return definitionMap.containsKey(typename);
	}

	public void reserve(Typename typename) throws TypeException {
		Objects.requireNonNull(typename, "typename must not be null");
		var entry = definitionMap.get(typename);
		if (entry != null) {
			throw new TypeException("typename already registered");
		}

		entry = new TypeLibraryEntry(TypeLibraryState.RESERVED, typename, null);
		definitionMap.put(typename, entry);
	}

	public void register(Typename typename, Definition definition) throws TypeException {
		Objects.requireNonNull(typename, "typename must not be null");
		var entry = definitionMap.get(typename);
		if (entry != null && entry.state == TypeLibraryState.REGISTERED) {
			if (!definition.equals(entry.definition)) {
				throw new TypeException(String.format("typename already registered: %s", typename));
			}

			// Already registered with the same definition.
			return;
		}

		entry = new TypeLibraryEntry(TypeLibraryState.REGISTERED, typename, definition);
		definitionMap.put(typename, entry);
	}
}

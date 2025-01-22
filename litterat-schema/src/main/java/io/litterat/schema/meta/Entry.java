package io.litterat.schema.meta;

import io.litterat.annotation.Record;

@Record
@io.litterat.annotation.Typename(namespace = "meta", name = "entry")
public class Entry {

	private final Typename typename;

	private final Definition definition;

	public Entry(Typename typename, Definition definition) {
		this.typename = typename;
		this.definition = definition;
	}

	public Typename typename() {
		return typename;
	}

	public Definition definition() {
		return definition;
	}
}

package io.litterat.xpl;

import io.litterat.annotation.Record;
import io.litterat.schema.meta.Definition;
import io.litterat.schema.meta.Entry;
import io.litterat.schema.meta.Typename;

@io.litterat.annotation.Typename(namespace = "meta", name = "stream_entry")
public class TypeStreamEntry {

	public final static Typename STREAM_ENTRY = new Typename("meta", "stream_entry");

	private final int streamId;
	private final Entry entry;

	@Record
	public TypeStreamEntry(int streamId, Entry entry) {
		this.streamId = streamId;
		this.entry = entry;
	}

	public int streamId() {
		return streamId;
	}

	public Entry entry() {
		return entry;
	}

	public Definition definition() {
		return entry.definition();
	}

	public Typename typename() {
		return entry.typename();
	}

}

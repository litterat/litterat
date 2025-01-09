package io.litterat.xpl;

import io.litterat.bind.annotation.Record;
import io.litterat.core.meta.Definition;
import io.litterat.core.meta.Entry;
import io.litterat.core.meta.Typename;

@io.litterat.bind.annotation.Typename(namespace = "meta", name = "stream_entry")
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

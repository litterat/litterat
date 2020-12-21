package io.litterat.pep.test.data;

import java.util.List;

import io.litterat.pep.Data;
import io.litterat.pep.Field;

public class UnmodifiableLinkedList {

	@Field(name = "list", bridge = UnmodifiableLinkedListBridge.class)
	private final List<String> list;

	@Data
	public UnmodifiableLinkedList(List<String> list) {
		this.list = list;
	}

	public List<String> list() {
		return this.list;
	}
}

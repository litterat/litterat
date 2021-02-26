package io.litterat.test.bind.data;

import java.util.List;

import io.litterat.bind.Data;
import io.litterat.bind.Field;

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

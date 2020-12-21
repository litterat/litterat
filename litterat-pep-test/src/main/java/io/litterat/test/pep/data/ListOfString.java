package io.litterat.test.pep.data;

import java.util.List;

import io.litterat.pep.Data;

public class ListOfString {

	private final List<List<String>> list;

	@Data
	public ListOfString(List<List<String>> list) {
		this.list = list;
	}

	public List<List<String>> list() {
		return this.list;
	}
}

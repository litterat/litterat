package io.litterat.test.pep.data;

import io.litterat.pep.Data;
import io.litterat.pep.DataOrder;

@DataOrder({ "location", "x", "y" })
public class MixedImmutableDataOrder {

	private final int x;
	private final int y;

	private String location;

	@Data
	public MixedImmutableDataOrder(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "{" + x + "," + y + "," + (location == null ? "null" : location) + "}";
	}

}

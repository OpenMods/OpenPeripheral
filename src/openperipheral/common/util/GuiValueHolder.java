package openperipheral.common.util;

import java.util.ArrayList;

import openperipheral.api.SyncableInt;

public class GuiValueHolder extends ArrayList<SyncableInt> {

	public GuiValueHolder(SyncableInt... values) {
		super(values.length);
		for (int i = 0; i < values.length; i++) {
			add(values[i]);
		}
	}

	public int[] asIntArray() {
		int[] values = new int[size()];
		for (int i = 0; i < size(); i++) {
			values[i] = get(i).getValue();
		}
		return values;
	}

}

package openperipheral.interfaces.cc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import com.google.common.base.Charsets;

import dan200.computercraft.api.filesystem.IMount;

public class StringMount implements IMount {

	private final String contents;

	public StringMount(String text) {
		this.contents = text;
	}

	@Override
	public boolean exists(String path) {
		return true;
	}

	@Override
	public boolean isDirectory(String path) {
		return false;
	}

	@Override
	public void list(String path, List<String> contents) {}

	@Override
	public long getSize(String path) {
		return contents.length();
	}

	@Override
	public InputStream openForRead(String path) {
		return new ByteArrayInputStream(contents.getBytes(Charsets.UTF_8));
	}

}

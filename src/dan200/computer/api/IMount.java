/**
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2013. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */

package dan200.computer.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * TODO: Document me
 */
public interface IMount
{
	public boolean exists( String path ) throws IOException;
	public boolean isDirectory( String path ) throws IOException;
	public void list( String path, List<String> contents ) throws IOException;
	public long getSize( String path ) throws IOException;
	public InputStream openForRead( String path ) throws IOException;	
}

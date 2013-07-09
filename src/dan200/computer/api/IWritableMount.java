/**
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2013. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */

package dan200.computer.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * TODO: Document me
 */
public interface IWritableMount extends IMount
{
	public void makeDirectory( String path ) throws IOException;
	public void delete( String path ) throws IOException;
	public OutputStream openForWrite( String path ) throws IOException;
	public OutputStream openForAppend( String path ) throws IOException;
	public long getRemainingSpace() throws IOException;
}

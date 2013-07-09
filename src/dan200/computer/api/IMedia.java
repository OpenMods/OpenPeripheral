/**
 * This file is part of the public ComputerCraft API - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2013. This API may be redistributed unmodified and in full only.
 * For help using the API, and posting your mods, visit the forums at computercraft.info.
 */

package dan200.computer.api;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;

/**
 * TODO: Document me
 */
public interface IMedia
{
	public String getLabel( ItemStack stack );
	public boolean setLabel( ItemStack stack, String label );
	
	public String getAudioTitle( ItemStack stack );
	public String getAudioRecordName( ItemStack stack );	
    
    public IMount createDataMount( ItemStack stack, World world );
}

package com.bettercraft.betachest;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;

public class BetaWorkbench extends ContainerWorkbench
{
	public BetaWorkbench(EntityPlayer player, int windowId)
	{
		super(player.inventory, player.world, 0, 0, 0);
		this.windowId = windowId;
		super.a((EntityHuman)player);
	}

	public boolean b(EntityHuman entityhuman)
	{
		return true;
	}
}

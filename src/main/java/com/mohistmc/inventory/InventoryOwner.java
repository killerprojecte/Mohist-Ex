package com.mohistmc.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlockEntityState;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author Mgazul
 * @date 2020/4/10 13:39
 */
public class InventoryOwner {

    public static InventoryHolder get(TileEntity te) {
        return get(te.level, te.getBlockPos(), true);
    }

    public static InventoryHolder get(IInventory inventory) {
        try {
            return inventory.getOwner();
        } catch (AbstractMethodError e) {
            return (inventory instanceof TileEntity) ? get((TileEntity) inventory) : null;
        }
    }

    public static InventoryHolder get(World world, BlockPos pos, boolean useSnapshot) {
        if (world == null) return null;
        // Spigot start
        org.bukkit.block.Block block = world.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        if (block == null) {
            return null;
        }
        // Spigot end
        org.bukkit.block.BlockState state = block.getState(useSnapshot);
        if (state instanceof InventoryHolder) {
            return (InventoryHolder) state;
        } else if (state instanceof CraftBlockEntityState) {
            TileEntity te = ((CraftBlockEntityState) state).getTileEntity();
            if (te instanceof IInventory) {
                return new CraftCustomInventory((IInventory) te);
            }
        }
        return null;
    }
}

package com.github.alexthe666.iceandfire.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PathNavigateFlyingCreature extends PathNavigateFlying {

    public PathNavigateFlyingCreature(EntityLiving entity, World world) {
        super(entity, world);
    }

    public boolean canEntityStandOnPos(BlockPos pos) {
        return this.world.isAirBlock(pos.down());
    }
}

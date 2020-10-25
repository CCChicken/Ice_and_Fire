package com.github.alexthe666.iceandfire.entity;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.alexthe666.citadel.server.entity.EntityPropertiesHandler;
import com.github.alexthe666.iceandfire.IafConfig;
import com.github.alexthe666.iceandfire.api.event.DragonFireDamageWorldEvent;
import com.github.alexthe666.iceandfire.block.BlockCharedPath;
import com.github.alexthe666.iceandfire.block.BlockFallingReturningState;
import com.github.alexthe666.iceandfire.block.BlockReturningState;
import com.github.alexthe666.iceandfire.block.IDragonProof;
import com.github.alexthe666.iceandfire.block.IafBlockRegistry;
import com.github.alexthe666.iceandfire.entity.props.FrozenEntityProperties;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDragonforgeInput;
import com.github.alexthe666.iceandfire.entity.util.BlockLaunchExplosion;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.misc.IafDamageRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpreadableSnowyDirtBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class IafDragonDestructionManager {

    public static void destroyAreaFire(World world, BlockPos center, EntityDragonBase destroyer) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;

        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageFire;

        if (stage <= 3) {
        	BlockPos.getAllInBox(center.add(-1, -1, -1), center.add(1, 1, 1)).map(BlockPos::toImmutable).parallel().forEach(pos -> {
                if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getTileEntity(pos)).onHitWithFlame();
                }
                if (IafConfig.dragonGriefing != 2 && world.rand.nextBoolean()) {
                    if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockFire(world.getBlockState(pos));
                        if(transformState.getBlock() != world.getBlockState(pos).getBlock()){
                            world.setBlockState(pos, transformState);
                        }
                        if (world.rand.nextBoolean() && transformState.getMaterial().isSolid() && world.isAirBlock(pos.up())) {
                            world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                        }
                    }
                }
        	});
        } else {
            final int radius = stage == 4 ? 2 : 3;
            final int j = radius + world.rand.nextInt(1);
            final int k = (radius + world.rand.nextInt(1));
            final int l = radius + world.rand.nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = (double) ff;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).parallel().forEach(blockpos -> {
                if (world.getTileEntity(blockpos) != null && world.getTileEntity(blockpos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getTileEntity(blockpos)).onHitWithFlame();
                }
                if (blockpos.distanceSq(center) <= ffDouble) {
                    if (IafConfig.dragonGriefing != 2 && world.rand.nextFloat() > (float) blockpos.distanceSq(center) / ff) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockFire(world.getBlockState(blockpos));
                            world.setBlockState(blockpos, transformState);
                            if (world.rand.nextBoolean() && transformState.getMaterial().isSolid() && world.isAirBlock(blockpos.up())) {
                                world.setBlockState(blockpos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        final int statusDuration = 5 + stage * 5;
        for (LivingEntity LivingEntity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) center.getX() - damageRadius, (double) center.getY() - damageRadius, (double) center.getZ() - damageRadius, (double) center.getX() + damageRadius, (double) center.getY() + damageRadius, (double) center.getZ() + damageRadius))) {
            if (!DragonUtils.onSameTeam(destroyer, LivingEntity) && !destroyer.isEntityEqual(LivingEntity) && destroyer.canEntityBeSeen(LivingEntity)) {
                LivingEntity.attackEntityFrom(IafDamageRegistry.DRAGON_FIRE, stageDmg);
                LivingEntity.setFire(statusDuration);
            }
        }
    }

    public static void destroyAreaIce(World world, BlockPos center, EntityDragonBase destroyer) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;

        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageIce;

        if (stage <= 3) {
        	BlockPos.getAllInBox(center.add(-1, -1, -1), center.add(1, 1, 1)).map(BlockPos::toImmutable).parallel().forEach(pos -> {
                if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getTileEntity(pos)).onHitWithFlame();
                }
                if (IafConfig.dragonGriefing != 2 && world.rand.nextBoolean()) {
                    if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockIce(world.getBlockState(pos));
                        if(transformState.getBlock() != world.getBlockState(pos).getBlock()){
                            world.setBlockState(pos, transformState);
                        }
                        if (world.rand.nextInt(9) == 0 && transformState.getMaterial().isSolid() && world.isAirBlock(pos.up()) && world.getBlockState(pos.down()).isSolid()) {
                            world.setBlockState(pos.up(), IafBlockRegistry.DRAGON_ICE_SPIKES.getDefaultState());
                        }
                    }
                }
        	});
        } else {
        	final int radius = stage == 4 ? 2 : 3;
            final int j = radius + world.rand.nextInt(1);
            final int k = (radius + world.rand.nextInt(1));
            final int l = radius + world.rand.nextInt(1);
            final float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = (double) ff;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).parallel().forEach(blockpos -> {
                if (world.getTileEntity(blockpos) != null && world.getTileEntity(blockpos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getTileEntity(blockpos)).onHitWithFlame();
                }
                if (blockpos.distanceSq(center) <= ffDouble) {
                    if (IafConfig.dragonGriefing != 2 && world.rand.nextFloat() > (float) blockpos.distanceSq(center) / ff) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockIce(world.getBlockState(blockpos));
                            world.setBlockState(blockpos, transformState);
                            if (world.rand.nextInt(9) == 0 && transformState.getMaterial().isSolid() && world.isAirBlock(blockpos.up()) && world.getBlockState(blockpos.down()).isSolid()) {
                                world.setBlockState(blockpos.up(), IafBlockRegistry.DRAGON_ICE_SPIKES.getDefaultState());
                            }
                        }
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        final int statusDuration = 50 * stage;
        for (LivingEntity LivingEntity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) center.getX() - damageRadius, (double) center.getY() - damageRadius, (double) center.getZ() - damageRadius, (double) center.getX() + damageRadius, (double) center.getY() + damageRadius, (double) center.getZ() + damageRadius))) {
            if (!DragonUtils.onSameTeam(destroyer, LivingEntity) && !destroyer.isEntityEqual(LivingEntity) && destroyer.canEntityBeSeen(LivingEntity)) {
                LivingEntity.attackEntityFrom(IafDamageRegistry.DRAGON_ICE, stageDmg);
                FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, FrozenEntityProperties.class);
                if (frozenProps != null) {
                    frozenProps.setFrozenFor(statusDuration);
                }
            }
        }
    }

    public static void destroyAreaFireCharge(World world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;

            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
            	Set<BlockPos> allInBlock = BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet());
                for (BlockPos pos : allInBlock) {
                    if (world.rand.nextFloat() > pos.distanceSq(center) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
                for (BlockPos pos : allInBlock) {
                    if (world.rand.nextBoolean()) {
                        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                            BlockState transformState = transformBlockFire(world.getBlockState(pos));
                            world.setBlockState(pos, transformState);
                            if (world.rand.nextBoolean() && transformState.getMaterial().isSolid() && world.isAirBlock(pos.up())) {
                                world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                }
            } else {
                final int radius = stage == 4 ? 2 : 3;
                j = radius + world.rand.nextInt(2);
                k = (radius + world.rand.nextInt(2));
                l = radius + world.rand.nextInt(2);
                final float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;
                final double ffDouble = (double) ff;

                BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                    if (blockpos.distanceSq(center) <= ffDouble) {
                        if (world.rand.nextFloat() > (float) blockpos.distanceSq(center) / ff && !(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
                        }
                    }
                });

                j++;
                k++;
                l++;
                BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                    if (blockpos.distanceSq(center) <= ffDouble) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockFire(world.getBlockState(blockpos));
                            world.setBlockState(blockpos, transformState);
                            if (world.rand.nextBoolean() && transformState.getMaterial().isSolid() && world.isAirBlock(blockpos.up())) {
                                world.setBlockState(blockpos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            final int statusDuration = 15;
            for (LivingEntity LivingEntity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) center.getX() - j, (double) center.getY() - k, (double) center.getZ() - l, (double) center.getX() + j, (double) center.getY() + k, (double) center.getZ() + l))) {
                if (!destroyer.isOnSameTeam(LivingEntity) && !destroyer.isEntityEqual(LivingEntity) && destroyer.canEntityBeSeen(LivingEntity)) {
                    LivingEntity.attackEntityFrom(IafDamageRegistry.DRAGON_ICE, stageDmg);
                    LivingEntity.setFire(statusDuration);
                }
            }

            if (IafConfig.explosiveDragonBreath) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2));
                explosion.doExplosionA();
                explosion.doExplosionB(true);
            }
        }
    }

    public static void destroyAreaIceCharge(World world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;

            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
            	Set<BlockPos> allInBox = BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet());
                for (BlockPos pos : allInBox) {
                    if (world.rand.nextFloat() > pos.distanceSq(center) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
                for (BlockPos pos : allInBox) {
                    if (world.rand.nextBoolean()) {
                        if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                            BlockState transformState = transformBlockIce(world.getBlockState(pos));
                            world.setBlockState(pos, transformState);
                            if (world.rand.nextBoolean() && transformState.isSolid() && world.isAirBlock(pos.up()) && world.getBlockState(pos.down()).isSolid()) {
                                world.setBlockState(pos.up(), IafBlockRegistry.DRAGON_ICE_SPIKES.getDefaultState());
                            }
                        }
                    }
                }
            } else {
                int radius = stage == 4 ? 2 : 3;
                j = radius + world.rand.nextInt(2);
                k = (radius + world.rand.nextInt(2));
                l = radius + world.rand.nextInt(2);
                final float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;
                final double ffDouble = (double) ff;

                BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                    if (blockpos.distanceSq(center) <= ffDouble) {
                        if (world.rand.nextFloat() > (float) blockpos.distanceSq(center) / ff && !(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
                        }
                    }
                });

                j++;
                k++;
                l++;
                BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                    if (blockpos.distanceSq(center) <= ffDouble) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockIce(world.getBlockState(blockpos));
                            world.setBlockState(blockpos, transformState);
                            if (world.rand.nextBoolean() && transformState.isSolid() && world.isAirBlock(blockpos.up()) && world.getBlockState(blockpos.down()).isSolid()) {
                                world.setBlockState(blockpos.up(), IafBlockRegistry.DRAGON_ICE_SPIKES.getDefaultState());
                            }
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            final int statusDuration = 400;
            for (LivingEntity LivingEntity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) center.getX() - j, (double) center.getY() - k, (double) center.getZ() - l, (double) center.getX() + j, (double) center.getY() + k, (double) center.getZ() + l))) {
                if (!destroyer.isOnSameTeam(LivingEntity) && !destroyer.isEntityEqual(LivingEntity) && destroyer.canEntityBeSeen(LivingEntity)) {
                    LivingEntity.attackEntityFrom(IafDamageRegistry.DRAGON_ICE, stageDmg);
                    FrozenEntityProperties frozenProps = EntityPropertiesHandler.INSTANCE.getProperties(LivingEntity, FrozenEntityProperties.class);
                    if (frozenProps != null) {
                        frozenProps.setFrozenFor(statusDuration);
                    }
                }
            }

            if (IafConfig.explosiveDragonBreath) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2));
                explosion.doExplosionA();
                explosion.doExplosionB(true);
            }
        }
    }

    public static void destroyAreaLightning(World world, BlockPos center, EntityDragonBase destroyer) {
        if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
            return;

        int stage = destroyer.getDragonStage();
        double damageRadius = 3.5D;
        float dmgScale = (float) IafConfig.dragonAttackDamageLightning;

        if (stage <= 3) {
        	BlockPos.getAllInBox(center.add(-1, -1, -1), center.add(1, 1, 1)).map(BlockPos::toImmutable).forEach(pos -> {
                if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getTileEntity(pos)).onHitWithFlame();
                }
                if (IafConfig.dragonGriefing != 2 && world.rand.nextBoolean()) {
                    if (!(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockLightning(world.getBlockState(pos));
                        if(transformState.getBlock() != world.getBlockState(pos).getBlock()){
                            world.setBlockState(pos, transformState);
                        }
                    }
                }
        	});
        } else {
            int radius = stage == 4 ? 2 : 3;
            int j = radius + world.rand.nextInt(1);
            int k = (radius + world.rand.nextInt(1));
            int l = radius + world.rand.nextInt(1);
            float f = (float) (j + k + l) * 0.333F + 0.5F;
            final float ff = f * f;
            final double ffDouble = (double) ff;

            damageRadius = 2.5F + f * 1.2F;
            BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                if (world.getTileEntity(blockpos) != null && world.getTileEntity(blockpos) instanceof TileEntityDragonforgeInput) {
                    ((TileEntityDragonforgeInput) world.getTileEntity(blockpos)).onHitWithFlame();
                }
                if (blockpos.distanceSq(center) <= ffDouble) {
                    if (IafConfig.dragonGriefing != 2 && world.rand.nextFloat() > (float) blockpos.distanceSq(center) / ff) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockLightning(world.getBlockState(blockpos));
                            world.setBlockState(blockpos, transformState);
                        }
                    }
                }
            });
        }

        final float stageDmg = stage * dmgScale;
        for (LivingEntity LivingEntity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) center.getX() - damageRadius, (double) center.getY() - damageRadius, (double) center.getZ() - damageRadius, (double) center.getX() + damageRadius, (double) center.getY() + damageRadius, (double) center.getZ() + damageRadius))) {
            if (!DragonUtils.onSameTeam(destroyer, LivingEntity) && !destroyer.isEntityEqual(LivingEntity) && destroyer.canEntityBeSeen(LivingEntity)) {
                LivingEntity.attackEntityFrom(IafDamageRegistry.DRAGON_LIGHTNING, stageDmg);
                double d1 = destroyer.getPosX() - LivingEntity.getPosX();
                double d0 = destroyer.getPosZ() - LivingEntity.getPosZ();
                LivingEntity.func_233627_a_(0.3F, d1, d0);
            }
        }
    }

    public static void destroyAreaLightningCharge(World world, BlockPos center, EntityDragonBase destroyer) {
        if (destroyer != null) {
            if (MinecraftForge.EVENT_BUS.post(new DragonFireDamageWorldEvent(destroyer, center.getX(), center.getY(), center.getZ())))
                return;

            int stage = destroyer.getDragonStage();
            int j = 2;
            int k = 2;
            int l = 2;

            if (stage <= 3) {
            	Set<BlockPos> allInBox = BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).collect(Collectors.toSet());
                for (BlockPos pos : allInBox) {
                    if (world.rand.nextFloat() * 7F > Math.sqrt(center.distanceSq(pos)) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
                for (BlockPos pos : allInBox) {
                    if (world.rand.nextFloat() * 7F > Math.sqrt(center.distanceSq(pos)) && !(world.getBlockState(pos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(pos).getBlock())) {
                        BlockState transformState = transformBlockLightning(world.getBlockState(pos));
                        world.setBlockState(pos, transformState);
                    }
                }
            } else {
                int radius = stage == 4 ? 2 : 3;
                j = radius + world.rand.nextInt(2);
                k = (radius + world.rand.nextInt(2));
                l = radius + world.rand.nextInt(2);
                float f = (float) (j + k + l) * 0.333F + 0.5F;
                final float ff = f * f;
                final double ffDouble = (double) ff;

                BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                    if (blockpos.distanceSq(center) <= ffDouble) {
                        if (world.rand.nextFloat() > (float) blockpos.distanceSq(center) / ff && !(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
                        }
                    }
                });

                j++;
                k++;
                l++;
                BlockPos.getAllInBox(center.add(-j, -k, -l), center.add(j, k, l)).map(BlockPos::toImmutable).forEach(blockpos -> {
                    if (blockpos.distanceSq(center) <= ffDouble) {
                        if (!(world.getBlockState(blockpos).getBlock() instanceof IDragonProof) && DragonUtils.canDragonBreak(world.getBlockState(blockpos).getBlock())) {
                            BlockState transformState = transformBlockLightning(world.getBlockState(blockpos));
                            world.setBlockState(blockpos, transformState);
                        }
                    }
                });
            }

            final float stageDmg = Math.max(1, stage - 1) * 2F;
            for (LivingEntity LivingEntity : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB((double) center.getX() - j, (double) center.getY() - k, (double) center.getZ() - l, (double) center.getX() + j, (double) center.getY() + k, (double) center.getZ() + l))) {
                if (!destroyer.isOnSameTeam(LivingEntity) && !destroyer.isEntityEqual(LivingEntity) && destroyer.canEntityBeSeen(LivingEntity)) {
                    LivingEntity.attackEntityFrom(IafDamageRegistry.DRAGON_LIGHTNING, stageDmg);
                    double d1 = destroyer.getPosX() - LivingEntity.getPosX();
                    double d0 = destroyer.getPosZ() - LivingEntity.getPosZ();
                    LivingEntity.func_233627_a_(0.9F, d1, d0);
                }
            }

            if (IafConfig.explosiveDragonBreath) {
                BlockLaunchExplosion explosion = new BlockLaunchExplosion(world, destroyer, center.getX(), center.getY(), center.getZ(), Math.min(2, stage - 2));
                explosion.doExplosionA();
                explosion.doExplosionB(true);
            }
        }
    }


    public static BlockState transformBlockFire(BlockState in) {
        if (in.getBlock() instanceof SpreadableSnowyDirtBlock) {
            return IafBlockRegistry.CHARRED_GRASS.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.EARTH && in.getBlock() == Blocks.DIRT) {
            return IafBlockRegistry.CHARRED_DIRT.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.CHARRED_GRAVEL.getDefaultState().with(BlockFallingReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.ROCK && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getTranslationKey().contains("cobblestone"))) {
            return IafBlockRegistry.CHARRED_COBBLESTONE.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.ROCK && in.getBlock() != IafBlockRegistry.CHARRED_COBBLESTONE) {
            return IafBlockRegistry.CHARRED_STONE.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.GRASS_PATH) {
            return IafBlockRegistry.CHARRED_GRASS_PATH.getDefaultState().with(BlockCharedPath.REVERTS, true);
        } else if (in.getMaterial() == Material.WOOD) {
            return IafBlockRegistry.ASH.getDefaultState();
        } else if (in.getMaterial() == Material.LEAVES || in.getMaterial() == Material.PLANTS || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.getDefaultState();
        }
        return in;
    }

    public static BlockState transformBlockIce(BlockState in) {
        if (in.getBlock() instanceof SpreadableSnowyDirtBlock) {
            return IafBlockRegistry.FROZEN_GRASS.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.EARTH && in.getBlock() == Blocks.DIRT || in.getMaterial() == Material.SNOW_BLOCK) {
            return IafBlockRegistry.FROZEN_DIRT.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.FROZEN_GRAVEL.getDefaultState().with(BlockFallingReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() != Blocks.GRAVEL) {
            return in;
        } else if (in.getMaterial() == Material.ROCK && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getTranslationKey().contains("cobblestone"))) {
            return IafBlockRegistry.FROZEN_COBBLESTONE.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.ROCK && in.getBlock() != IafBlockRegistry.FROZEN_COBBLESTONE) {
            return IafBlockRegistry.FROZEN_STONE.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.GRASS_PATH) {
            return IafBlockRegistry.FROZEN_GRASS_PATH.getDefaultState().with(BlockCharedPath.REVERTS, true);
        } else if (in.getMaterial() == Material.WOOD) {
            return IafBlockRegistry.FROZEN_SPLINTERS.getDefaultState();
        } else if (in.getMaterial() == Material.WATER) {
            return Blocks.ICE.getDefaultState();
        } else if (in.getMaterial() == Material.LEAVES || in.getMaterial() == Material.PLANTS || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.getDefaultState();
        }
        return in;
    }

    public static BlockState transformBlockLightning(BlockState in) {
        if (in.getBlock() instanceof SpreadableSnowyDirtBlock) {
            return IafBlockRegistry.CRACKLED_GRASS.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.EARTH && in.getBlock() == Blocks.DIRT) {
            return IafBlockRegistry.CRACKLED_DIRT.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.SAND && in.getBlock() == Blocks.GRAVEL) {
            return IafBlockRegistry.CRACKLED_GRAVEL.getDefaultState().with(BlockFallingReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.ROCK && (in.getBlock() == Blocks.COBBLESTONE || in.getBlock().getTranslationKey().contains("cobblestone"))) {
            return IafBlockRegistry.CRACKLED_COBBLESTONE.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getMaterial() == Material.ROCK && in.getBlock() != IafBlockRegistry.CRACKLED_COBBLESTONE) {
            return IafBlockRegistry.CRACKLED_STONE.getDefaultState().with(BlockReturningState.REVERTS, true);
        } else if (in.getBlock() == Blocks.GRASS_PATH) {
            return IafBlockRegistry.CRACKLED_GRASS_PATH.getDefaultState().with(BlockCharedPath.REVERTS, true);
        } else if (in.getMaterial() == Material.WOOD) {
            return IafBlockRegistry.ASH.getDefaultState();
        } else if (in.getMaterial() == Material.LEAVES || in.getMaterial() == Material.PLANTS || in.getBlock() == Blocks.SNOW) {
            return Blocks.AIR.getDefaultState();
        }
        return in;
    }

}

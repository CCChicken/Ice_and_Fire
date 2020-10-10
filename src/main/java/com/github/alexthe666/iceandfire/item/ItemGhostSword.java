package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.entity.EntityGhostSword;
import com.github.alexthe666.iceandfire.entity.IafEntityRegistry;
import com.github.alexthe666.iceandfire.event.ServerEvents;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemGhostSword extends SwordItem {

    public ItemGhostSword() {
        super(IafItemRegistry.GHOST_SWORD_TOOL_MATERIAL, 5, -1.0F, new Item.Properties().group(IceAndFire.TAB_ITEMS));
        this.setRegistryName(IceAndFire.MODID, "ghost_sword");
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity LivingEntity) {
        if(LivingEntity instanceof PlayerEntity){
            ServerEvents.onLeftClick((PlayerEntity)LivingEntity, stack);
        }
        return super.onItemUseFinish(stack, worldIn, LivingEntity);
    }


    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity targetEntity, LivingEntity attacker) {
        return super.hitEntity(stack, targetEntity, attacker);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.iceandfire.legendary_weapon.desc").func_240699_a_(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent("item.iceandfire.ghost_sword.desc_0").func_240699_a_(TextFormatting.GRAY));
    }
}
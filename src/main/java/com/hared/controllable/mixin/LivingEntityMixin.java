package com.hared.controllable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hared.controllable.FabricControllable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	
	@Shadow protected int itemUseTimeLeft;
	@Shadow protected ItemStack activeItemStack;;
	
	@Shadow public abstract boolean isUsingItem();
	@Shadow public abstract ItemStack getStackInHand(Hand hand);
	@Shadow public abstract Hand getActiveHand();
	
	@Inject(at = @At("HEAD"), method = ("tickActiveItemStack"))
	public void onUsageTick(CallbackInfo info) {
		if(this.isUsingItem())
			if(ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack))
				FabricControllable.getInstance().getControllerEvents().onPlayerUsingItem(this.activeItemStack, this.itemUseTimeLeft);
	}
	
}

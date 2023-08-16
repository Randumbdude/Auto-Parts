package autopartsclient.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autopartsclient.SharedVaribles;

@Mixin(InGameHud.class)
public abstract class InGameHUDMixin2 {

    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    @Shadow
    protected abstract void renderHotbarItem(MatrixStack matrixStack, int i, int j, float f, PlayerEntity playerEntity,
	    ItemStack itemStack, int k);

    // @Shadow
    // protected abstract void renderHotbarItem(int x, int y, float tickDelta,
    // PlayerEntity player, ItemStack stack, int seed);

    @Shadow
    protected abstract LivingEntity getRiddenEntity();

    @Inject(at = @At("HEAD"), method = "renderHotbar")
    public void renderArmorHud(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
	assert this.client.player != null;

	int i = this.scaledHeight - 55;
	int h = 63;

//		Moves armorhud up if player is under water
	if (client.player.getAir() < client.player.getMaxAir()) {
	    i = this.scaledHeight - 65;
	}
//		Moves armorhud down if player is in creative
	if (client.player.isCreative()) {
	    i = this.scaledHeight - 39;
	}
//		Moves armorhud up if player is on mount
	if (client.player.hasVehicle() && getRiddenEntity() != null) {
	    if (getRiddenEntity().isAlive()) {
		if (getRiddenEntity().getMaxHealth() > 20) {
		    i = this.scaledHeight - 65;
		} else {
		    i = this.scaledHeight - 55;
		}
	    }
	}

//		Render all armor items from player
	if (SharedVaribles.showArmorHUD) {
	    for (int j = 0; j < 4; j++) {
		renderHotbarItem(matrices, this.scaledWidth / 2 + h, i, tickDelta, client.player,
			this.client.player.getInventory().getArmorStack(j), 1);
		h = h - 15;
	    }
	}

    }

    @ModifyVariable(at = @At("STORE"), method = "renderHeldItemTooltip", ordinal = 2)
    public int renderHeldItemTooltip(int k) {
	return this.scaledHeight - 62;
    }

}
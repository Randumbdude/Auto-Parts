package autopartsclient.mixins;

import java.awt.Color;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import autopartsclient.SharedVaribles;
import autopartsclient.module.Render.Freecam;
import autopartsclient.ui.HUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

@Mixin(InGameHud.class)
public class InGameHUDMixin {

	Identifier fontID = new Identifier("autoparts", "font.ttf");

	MinecraftClient mc = MinecraftClient.getInstance();

	@Inject(method = "render", at = @At("RETURN"), cancellable = true)
	public void renderHUD(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		HUD.Render(matrices, tickDelta);
	}

	@Inject(at = @At("TAIL"), method = "render")
	public void renderFps(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer renderer = mc.textRenderer;

		String fps = ((MinecraftClientAccessor) mc).getCurrentFps() + " Fps";
		if (SharedVaribles.showFPS) {
			renderer.drawWithShadow(matrices, fps, 100, 5, Color.WHITE.getRGB());
		}
	}

	@Inject(at = @At("TAIL"), method = "render")
	public void renderCords(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer renderer = mc.textRenderer;

		int pX = (int) mc.player.getPos().x;
		int pY = (int) mc.player.getPos().y;
		int pZ = (int) mc.player.getPos().z;

		if (SharedVaribles.showCords) {
			renderer.drawWithShadow(matrices, "X: " + pX + " Y: " + pY + " Z: " + pZ, 150, 5, Color.WHITE.getRGB());
			// DrawableHelper.drawTextWithShadow(matrices, renderer, null, 10, 10, new
			// Color(0, 150, 0));
		}
		/*
		 * if (mc.player.world.getDimension() != null) {
		 * 
		 * }
		 */
		if (SharedVaribles.showTPS) {
			//renderer.drawWithShadow(matrices, "TPS: " + TPSutils.getTPS(10), 255, 5, Color.WHITE.getRGB());
		}
	}

	// Makes HUD correspond to the player rather than the FreeCamera.
	@Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
	private void onGetCameraPlayer(CallbackInfoReturnable<PlayerEntity> cir) {
		if (Freecam.isToggled == true) {
			cir.setReturnValue(mc.player);
		}
	}
}
//InGameOverlayRenderer.class
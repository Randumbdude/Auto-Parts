package autopartsclient.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autopartsclient.module.Player.NoHurtCam;
import autopartsclient.module.Render.UnfocusedCPU;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	MinecraftClient mc = MinecraftClient.getInstance();
	
    @Inject(at = @At("HEAD"), method = "bobViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V", cancellable = true)
    public void bobViewWhenHurt(MatrixStack matrixStack_1, float float_1, CallbackInfo ci) {
        if (NoHurtCam.isToggled) ci.cancel();
    }
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    void beforeRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (UnfocusedCPU.isToggled && !this.mc.isWindowFocused()) {
            ci.cancel(); // don't render
        }
    }
}
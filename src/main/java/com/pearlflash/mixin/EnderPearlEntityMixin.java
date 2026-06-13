package com.pearlflash.mixin;

import com.pearlflash.PearlFlashClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public class EnderPearlEntityMixin {

    /**
     * EnderPearlEntity.onCollision() 진입 시점에 inject.
     *
     * onCollision은 펄이 블록 또는 엔티티에 닿을 때 호출되며,
     * 내부에서 owner(플레이어)를 텔레포트시키고 무적 처리를 합니다.
     * 우리는 이 메서드가 시작되는 순간 → 즉, 펄 착지 직후를 잡아
     * 클라이언트 측 HUD 타이머를 시작합니다.
     */
    @Inject(
        method = "onCollision",
        at = @At("HEAD")
    )
    private void onPearlCollision(HitResult hitResult, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // 클라이언트 측에서, 로컬 플레이어의 펄인지 확인
        EnderPearlEntity self = (EnderPearlEntity) (Object) this;
        if (client.player != null && self.getOwner() == client.player) {
            PearlFlashClient.onPearlLand();
        }
    }
}

package com.pearlflash;

import com.pearlflash.hud.IFrameHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

import java.util.HashSet;
import java.util.Set;

public class PearlFlashClient implements ClientModInitializer {

    public static long iframeEndTime = -1;
    public static final long IFRAME_DURATION_NS = 50_000_000L;

    private static final Set<Integer> trackedPearls = new HashSet<>();

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(IFrameHud::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            Set<Integer> currentPearls = new HashSet<>();

            client.world.getEntities().forEach(entity -> {
                if (entity instanceof EnderPearlEntity pearl) {
                    if (pearl.getOwner() == client.player) {
                        currentPearls.add(pearl.getId());
                    }
                }
            });

            // 이전 틱에 있던 펄이 사라졌으면 = 착지한 것
            for (int id : trackedPearls) {
                if (!currentPearls.contains(id)) {
                    onPearlLand();
                }
            }

            trackedPearls.clear();
            trackedPearls.addAll(currentPearls);
        });
    }

    public static void onPearlLand() {
        iframeEndTime = System.nanoTime() + IFRAME_DURATION_NS;
    }

    public static long getRemainingMs() {
        if (iframeEndTime < 0) return -1L;
        long remaining = (iframeEndTime - System.nanoTime()) / 1_000_000L;
        return Math.max(0L, remaining);
    }
}

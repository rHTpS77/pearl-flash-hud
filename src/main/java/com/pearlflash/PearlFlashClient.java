package com.pearlflash;

import com.pearlflash.hud.IFrameHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class PearlFlashClient implements ClientModInitializer {

    // 모드 전역 상태 — 착지 감지 시 mixin이 여기에 기록
    public static long iframeEndTime = -1; // System.nanoTime() 기준 무적 종료 시각

    // 무적 시간: 1틱 = 50ms (= 50_000_000 ns)
    public static final long IFRAME_DURATION_NS = 50_000_000L;

    @Override
    public void onInitializeClient() {
        // 매 프레임 HUD 렌더링 콜백 등록
        HudRenderCallback.EVENT.register(IFrameHud::render);
    }

    /**
     * Mixin에서 호출 — 엔더펄 착지 감지 시 무적 타이머 시작
     */
    public static void onPearlLand() {
        iframeEndTime = System.nanoTime() + IFRAME_DURATION_NS;
    }

    /**
     * 현재 남은 무적 시간 (ms). 0이면 종료 또는 미발동.
     */
    public static long getRemainingMs() {
        if (iframeEndTime < 0) return -1L;
        long remaining = (iframeEndTime - System.nanoTime()) / 1_000_000L;
        return Math.max(0L, remaining);
    }
}

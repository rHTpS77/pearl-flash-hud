package com.pearlflash.hud;

import com.pearlflash.PearlFlashClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class IFrameHud {

    // HUD 위치 — 화면 우측 상단 기준
    private static final int OFFSET_RIGHT  = 6;   // 우측 여백
    private static final int OFFSET_TOP    = 6;   // 상단 여백
    private static final int BOX_W         = 62;
    private static final int BOX_H         = 28;

    // 색상 (ARGB)
    private static final int COLOR_BG      = 0xBB000000; // 반투명 검정
    private static final int COLOR_BORDER  = 0xFF9966FF; // 보라 테두리 (평상시)
    private static final int COLOR_ACTIVE  = 0xFFAA88FF; // 보라 (무적 중)
    private static final int COLOR_WARNING = 0xFFFFAA00; // 주황 (20ms 이하)
    private static final int COLOR_EXPIRED = 0xFFFF4444; // 빨강 (종료)
    private static final int COLOR_TEXT    = 0xFFFFFFFF;
    private static final int COLOR_LABEL   = 0xFFAAAAAA;

    // 바 색상
    private static final int BAR_BG        = 0xFF333333;

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        long remaining = PearlFlashClient.getRemainingMs();

        // 무적이 한 번도 발동 안 된 상태면 HUD 숨김
        if (remaining < 0) return;

        int screenW = client.getWindow().getScaledWidth();

        int x = screenW - BOX_W - OFFSET_RIGHT;
        int y = OFFSET_TOP;

        // 배경
        ctx.fill(x, y, x + BOX_W, y + BOX_H, COLOR_BG);

        // 테두리 색 결정
        int borderColor;
        if (remaining > 20) {
            borderColor = COLOR_ACTIVE;
        } else if (remaining > 0) {
            borderColor = COLOR_WARNING;
        } else {
            borderColor = COLOR_EXPIRED;
        }

        // 1px 테두리 (fill로 직접)
        ctx.fill(x,              y,              x + BOX_W,     y + 1,          borderColor); // top
        ctx.fill(x,              y + BOX_H - 1,  x + BOX_W,     y + BOX_H,      borderColor); // bottom
        ctx.fill(x,              y,              x + 1,          y + BOX_H,      borderColor); // left
        ctx.fill(x + BOX_W - 1, y,              x + BOX_W,     y + BOX_H,      borderColor); // right

        // 라벨: "I-FRAME"
        ctx.drawText(
            client.textRenderer,
            "I-FRAME",
            x + 4, y + 3,
            COLOR_LABEL,
            false
        );

        // 숫자: remaining ms
        String timeStr = remaining + "ms";
        int textColor = remaining > 20 ? COLOR_ACTIVE
                      : remaining > 0  ? COLOR_WARNING
                      :                  COLOR_EXPIRED;

        ctx.drawText(
            client.textRenderer,
            timeStr,
            x + 4, y + 13,
            textColor,
            true   // shadow
        );

        // 진행 바 (BOX 하단)
        int barX = x + 1;
        int barY = y + BOX_H - 4;
        int barMaxW = BOX_W - 2;
        float pct = (float) remaining / 50f; // 50ms = 100%
        int barW = (int) (barMaxW * Math.min(1f, pct));

        ctx.fill(barX, barY, barX + barMaxW, barY + 3, BAR_BG);
        if (barW > 0) {
            ctx.fill(barX, barY, barX + barW, barY + 3, borderColor);
        }
    }
}

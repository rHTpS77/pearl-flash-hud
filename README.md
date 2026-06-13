# Pearl Flash HUD

엔더펄 착지 직후 발생하는 **무적 i-frame(1틱 = 50ms)** 타이머를 화면 우측 상단에 표시하는 Fabric 모드입니다.

CPVP에서 Pearl Flashing 연습 시, 무적이 정확히 언제 시작·종료되는지 시각적으로 확인할 수 있습니다.

---

## 호환성

| 항목 | 버전 |
|------|------|
| Minecraft | 26.1.2 (Java Edition) |
| Fabric Loader | 0.18.4 |
| Fabric API | 0.151.0+26.1.2 |
| Java | 25 이상 |

---

## 빌드 방법

```bash
# 1. Java 25 설치 확인
java -version

# 2. 빌드
./gradlew build

# 3. 완성된 .jar 위치
# build/libs/pearl-flash-hud-1.0.0.jar
```

---

## 설치 방법

1. [Fabric Installer](https://fabricmc.net/use/installer/) 로 Minecraft 26.1.2 + Loader 0.18.4 설치
2. `%appdata%\.minecraft\mods\` 폴더에 아래 파일 복사:
   - `pearl-flash-hud-1.0.0.jar` (빌드 결과물)
   - `fabric-api-0.151.0+26.1.2.jar` ([Modrinth](https://modrinth.com/mod/fabric-api))

---

## 동작 원리

```
엔더펄 투척
    │
    ▼
EnderPearlEntity.onCollision() 호출  ← Mixin 주입 지점
    │
    ▼
PearlFlashClient.onPearlLand()
    │  iframeEndTime = now + 50ms
    ▼
IFrameHud.render() (매 프레임)
    │  remaining = iframeEndTime - now
    │
    ├─ remaining > 20ms → 보라색 바
    ├─ remaining > 0ms  → 주황색 바  (Flash 타이밍!)
    └─ remaining = 0    → 빨간색 → 0.5초 후 HUD 숨김
```

---

## HUD 커스터마이징

`IFrameHud.java` 상단 상수로 위치·색상 변경 가능:

```java
private static final int OFFSET_RIGHT = 6;   // 우측 여백 (px)
private static final int OFFSET_TOP   = 6;   // 상단 여백 (px)
private static final int BOX_W        = 62;  // HUD 너비
private static final int BOX_H        = 28;  // HUD 높이
```

---

## 파일 구조

```
pearl-flash-hud/
├── build.gradle
├── gradle.properties
└── src/main/
    ├── java/com/pearlflash/
    │   ├── PearlFlashClient.java       ← 엔트리포인트 + 전역 상태
    │   ├── hud/
    │   │   └── IFrameHud.java          ← HUD 렌더러
    │   └── mixin/
    │       └── EnderPearlEntityMixin.java  ← 착지 감지
    └── resources/
        ├── fabric.mod.json
        └── pearlflash.mixins.json
```

# 网络音乐机：唱片独奏

简体中文 | [English](README.md)

防止精妙背包播放网络音乐机（NetMusic）唱片时，与游戏背景音乐叠加的客户端补丁。

**0.1.0-alpha 测试版**

## 功能

- 播放可听范围内的背包网络唱片时，抑制原版背景音乐。
- 支持其他模组的 `MUSIC` 分类音乐，目前兼容灾变mod。
- 停止唱片或离开可听范围后解除抑制；仍在播放的背景音乐从当前进度恢复声音。

只有音频流成功开始播放后才会触发，不修改游戏音量设置。

## 目标环境：

| 组件 | 版本 |
| --- | --- |
| NeoForge | 21.1.229 |
| NetMusic | 1.5.2-neoforge+mc1.21.1 |
| Sophisticated Core | 1.5.2.2343 |
| Sophisticated Backpacks | 3.26.6.2174 |

## 配置与兼容范围

配置文件：`config/NetMusicSoloDisc-client.toml`。

- `enabled`：启用背景音乐抑制，默认 `true`。
- `suppressOtherModMusic`：尝试通过内置兼容规则抑制非 `MUSIC` 分类的模组音乐，
  默认 `true`。目前涵盖使用 `RECORDS` 分类的灾变 Boss 音乐，不会静音整个 `RECORDS` 分类。
- `extraBackgroundSounds`：补充精确的音乐事件 ID，例如 `["anothermod:boss_theme"]`。
  补丁启用时，这些 ID 独立于 `suppressOtherModMusic` 生效。

仅背包唱片升级中的 NetMusic 唱片触发本补丁。原版唱片和 NetMusic 自带播放机不会触发。
通过 Minecraft `MUSIC` 分类播放的音乐自动兼容；其他分类可能需要补充事件 ID，
独立音频系统需要专门适配。

反馈问题请附模组版本与复现步骤。

非官方兼容补丁，采用 [MIT 许可证](LICENSE)。

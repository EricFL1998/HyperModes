# OS4 发布渠道策略

## 背景

旧版本（2.x）内置的升级检测在 App 启动时请求：

    https://api.github.com/repos/EricFL1998/HyperModes/releases/latest

并把返回 tag 的语义版本与本地版本比较，判定更新则弹窗。判定逻辑：

    remote.split(".").mapNotNull { it.toIntOrNull() }

即 tag 按 "." 分段后**只保留纯数字段**，非数字段直接丢弃。

## 规则：正式发布，但 tag 第一段必须非数字（os4- 前缀）

旧版本解析 tag 时丢弃非数字段：

- "os4-3.0-beta1" -> 分段 ["os4-3", "0-beta1"] -> 全部非数字 -> 空数组
  -> 比较时每一位按 0 处理 -> 0 < 2 -> 永远判定"不是新版本"
- "3.0-os4" -> 分段 ["3", "0-os4"] -> 保留 [3] -> 3 > 2 -> **会被判定为更新**

因此：

1. OS4 版本必须是**正式 release**（不用 pre-release）。
2. tag 必须以 os4- 开头（第一段非纯数字），例如 os4-3.0-beta1、
   os4-3.0.1。
3. 严禁使用纯数字 tag（3.0、3.0.1 等）发布 OS4 版本，否则旧版 2.x
   用户会收到升级提示并可能装坏 OS3 设备。

发布命令示例（不带 --prerelease）：

    gh release create os4-3.0-beta2 HyperModes-*.apk --target os4 ...

## 双重保险说明

即使 GitHub 的 releases/latest 之后指向了 os4 正式版（当前仍指向
2.3.1），旧版本解析 "os4-3.0-*" 也会得到全 0 数组，判定不是新版本，
依旧不会弹更新框。tag 前缀是唯一且必须的防线，发布时务必遵守。

## 验证方法

发布后用任意 2.x 版本号模拟旧更新检测：

    python -c "print('os4-3.0-beta1'.split('.')[0].isdigit())"  # 必须输出 False

以及确认 releases/latest 当前指向：

    curl -s https://api.github.com/repos/EricFL1998/HyperModes/releases/latest | grep tag_name

当前应返回 2.3.1。

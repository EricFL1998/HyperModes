# OS4 发布渠道策略

## 背景

旧版本（2.x）内置的升级检测在 App 启动时请求：

    https://api.github.com/repos/EricFL1998/HyperModes/releases/latest

并用 tag 的语义版本与本地版本比较，更新则弹窗。

## 规则：OS4 线一律发 pre-release

GitHub 的 releases/latest 端点**永远不会返回 pre-release 和 draft**。
因此只要 OS4 适配版全部以「预发布」形式发布：

- 旧版本请求 releases/latest 时拿到的永远是 2.x 正式版，更新检测不触发；
- OS4 测试用户通过预发布页面的直链手动安装。

## 操作要求

发布 OS4 版本时必须带 --prerelease：

    gh release create os4-3.0-beta2 HyperModes-*.apk --target os4 --prerelease ...

严禁把 OS4 构建发布为正式 release（不带 --prerelease），否则会立即出现在
所有旧版本的升级检测里。

tag 命名使用 os4-3.0-* 前缀，与 main 线的纯数字 tag（2.3.1 等）区分。

## 验证方法

发布后确认 releases/latest 仍指向 2.3.1：

    curl -s https://api.github.com/repos/EricFL1998/HyperModes/releases/latest | grep tag_name

返回 2.3.1 即代表旧版本检测不到新版本。

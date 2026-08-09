# Qwen3.8-Max 眼睛

当主模型是 **deepseek**（无原生视觉）时，用 [Qwen3.8-Max](https://www.qianwenai.com/models/qwen3.8-max)
作为视觉模型来"看"图片。接入方式：DashScope 兼容模式（OpenAI 兼容接口）。

## 配置

- API Key：`config.json`（已随仓库 .gitignore 默认忽略，不会提交）
- 接口：`https://dashscope.aliyuncs.com/compatible-mode/v1`
- 模型：`qwen3.8-max`（支持图像/文本/视频输入）

也可用环境变量 `DASHSCOPE_API_KEY` 替代 config.json 中的 key。

## 用法

```powershell
# 分析一张本地图片
python tools/qwen-eyes/eyes.py screenshot.png --prompt "描述这个界面的布局"

# 对比多张图片
python tools/qwen-eyes/eyes.py a.png b.png --prompt "这两张图有什么不同？"

# 直接截当前屏幕再分析
python tools/qwen-eyes/eyes.py --screen --prompt "屏幕上显示的是什么？"

# 分析网络图片
python tools/qwen-eyes/eyes.py https://example.com/img.png
```

回答会先打印模型的思考过程（若有），再打印正式回答。`--save 文件.txt` 可把回答写入文件。

## 注意

- 仅当主模型为 deepseek 时调用（deepseek 不直接支持图像输入）。
- 脚本只用 Python 标准库，无需安装 openai 等依赖。

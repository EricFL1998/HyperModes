#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Qwen3.8-Max 视觉助手 —— deepseek 模型的"眼睛"

当主模型是 deepseek（本身不支持图像输入）时，用它来看图：
    python eyes.py <图片路径或URL> [更多图片...] [--prompt "问题"] [--max-tokens 1024]
    python eyes.py screenshot.png --prompt "这个界面的布局是什么样的？"
    python eyes.py --screen --prompt "当前屏幕显示的是什么？"

配置: 同目录 config.json（api_key / base_url / model），或环境变量 DASHSCOPE_API_KEY
"""

import argparse
import base64
import json
import mimetypes
import os
import struct
import subprocess
import sys
import tempfile
import urllib.error
import urllib.request

# Windows 下 Python 的 stdout/stderr 默认用本地代码页（gbk/cp936）编码，
# 而终端/管道通常是 UTF-8，导致中文输出乱码。这里统一强制 UTF-8。
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    sys.stderr.reconfigure(encoding="utf-8", errors="replace")

CONFIG_PATH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "config.json")
DEFAULT_PROMPT = "请详细描述这张图片的内容，包括文字、元素、布局和整体风格。"
MIN_IMAGE_SIZE = 32  # 模型要求宽高都 > 10，留余量设为 32

try:
    from PIL import Image as PILImage
    HAVE_PIL = True
except ImportError:
    HAVE_PIL = False


def load_config():
    cfg = {}
    if os.path.exists(CONFIG_PATH):
        with open(CONFIG_PATH, encoding="utf-8") as f:
            cfg = json.load(f)
    if not cfg.get("api_key"):
        cfg["api_key"] = os.environ.get("DASHSCOPE_API_KEY", "")
    cfg.setdefault("base_url", "https://dashscope.aliyuncs.com/compatible-mode/v1")
    cfg.setdefault("model", "qwen3.8-max")
    return cfg


def to_data_url(path_or_url):
    if path_or_url.startswith(("http://", "https://")):
        return {"type": "image_url", "image_url": {"url": path_or_url}}
    if not os.path.exists(path_or_url):
        raise FileNotFoundError(f"图片不存在: {path_or_url}")
    mime, _ = mimetypes.guess_type(path_or_url)
    if not mime or not mime.startswith("image/"):
        mime = "image/png"
    with open(path_or_url, "rb") as f:
        b64 = base64.b64encode(f.read()).decode("ascii")
    return {"type": "image_url", "image_url": {"url": f"data:{mime};base64,{b64}"}}


def image_size(path):
    """读取 PNG/JPEG 宽高（仅用标准库，失败返回 None）。"""
    try:
        with open(path, "rb") as f:
            head = f.read(32)
        if head[:8] == b"\x89PNG\r\n\x1a\n":
            w, h = struct.unpack(">II", head[16:24])
            return w, h
        if head[:2] == b"\xff\xd8":
            with open(path, "rb") as f:
                data = f.read(131072)
            i = 2
            while i + 9 < len(data):
                if data[i] != 0xFF:
                    i += 1
                    continue
                marker = data[i + 1]
                if marker in (0xC0, 0xC1, 0xC2, 0xC3, 0xC5, 0xC6, 0xC7,
                              0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF):
                    h = int.from_bytes(data[i + 5:i + 7], "big")
                    w = int.from_bytes(data[i + 7:i + 9], "big")
                    return w, h
                seg_len = int.from_bytes(data[i + 2:i + 4], "big")
                i += 2 + seg_len
    except OSError:
        pass
    return None


def resize_image(src, out_path, scale):
    """把图片放大 scale 倍后保存，优先 PIL，否则用 PowerShell System.Drawing。"""
    if HAVE_PIL:
        with PILImage.open(src) as im:
            im = im.convert("RGB")
            im = im.resize((im.width * scale, im.height * scale), PILImage.LANCZOS)
            im.save(out_path, "PNG")
        return
    ps = (
        "Add-Type -AssemblyName System.Drawing;"
        f"$src=[System.Drawing.Image]::FromFile('{os.path.abspath(src)}');"
        f"$scale={scale};"
        "$w=[int]($src.Width*$scale);$h=[int]($src.Height*$scale);"
        "$bmp=New-Object System.Drawing.Bitmap($w,$h);"
        "$g=[System.Drawing.Graphics]::FromImage($bmp);"
        "$g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic;"
        "$g.DrawImage($src,0,0,$w,$h);"
        f"$bmp.Save('{os.path.abspath(out_path)}',[System.Drawing.Imaging.ImageFormat]::Png);"
        "$g.Dispose();$bmp.Dispose();$src.Dispose()"
    )
    proc = subprocess.run(["powershell", "-NoProfile", "-Command", ps],
                          capture_output=True, text=True, timeout=60)
    if proc.returncode != 0 or not os.path.exists(out_path):
        raise RuntimeError(f"图片放大失败: {proc.stderr.strip()}")


def ensure_valid_size(path):
    """若图片宽或高太小，先放大到 MIN_IMAGE_SIZE 再返回可用路径。"""
    size = image_size(path)
    if size is None:
        return path
    w, h = size
    if min(w, h) >= MIN_IMAGE_SIZE:
        return path
    scale = max(2, -(-MIN_IMAGE_SIZE // min(w, h)))
    out = os.path.join(tempfile.gettempdir(),
                       f"qwen_eyes_resized_{os.path.basename(path)}.png")
    resize_image(path, out, scale)
    print(f"[eyes] 图片过小({w}x{h})，已放大 {scale} 倍后分析", file=sys.stderr)
    return out


def capture_screen(out_path):
    """用 PowerShell 截取主屏幕，返回保存路径。"""
    ps = (
        "Add-Type -AssemblyName System.Windows.Forms;"
        "Add-Type -AssemblyName System.Drawing;"
        "$b=[System.Windows.Forms.Screen]::PrimaryScreen.Bounds;"
        "$bmp=New-Object System.Drawing.Bitmap($b.Width,$b.Height);"
        "$g=[System.Drawing.Graphics]::FromImage($bmp);"
        "$g.CopyFromScreen($b.Location,[System.Drawing.Point]::Empty,$b.Size);"
        f"$bmp.Save('{out_path}',[System.Drawing.Imaging.ImageFormat]::Png);"
        "$g.Dispose();$bmp.Dispose()"
    )
    proc = subprocess.run(
        ["powershell", "-NoProfile", "-Command", ps],
        capture_output=True, text=True, timeout=30,
    )
    if proc.returncode != 0 or not os.path.exists(out_path):
        raise RuntimeError(f"截图失败: {proc.stderr.strip()}")
    return out_path


def call_vision(cfg, images, prompt, max_tokens):
    content = [{"type": "text", "text": prompt}]
    content += [to_data_url(p) for p in images]
    payload = {
        "model": cfg["model"],
        "messages": [{"role": "user", "content": content}],
        "max_tokens": max_tokens,
    }
    req = urllib.request.Request(
        cfg["base_url"].rstrip("/") + "/chat/completions",
        data=json.dumps(payload).encode("utf-8"),
        headers={
            "Content-Type": "application/json",
            "Authorization": "Bearer " + cfg["api_key"],
        },
    )
    try:
        with urllib.request.urlopen(req, timeout=180) as resp:
            return json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"API 错误 {e.code}: {body}") from e


def main():
    ap = argparse.ArgumentParser(
        description="Qwen3.8-Max 视觉助手：deepseek 主模型看图时调用",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="示例:\n  python eyes.py shot.png --prompt \"描述界面布局\"\n"
               "  python eyes.py a.png b.png --prompt \"对比两张图\"\n"
               "  python eyes.py --screen --prompt \"屏幕上是什么\"",
    )
    ap.add_argument("images", nargs="*", help="本地图片路径或 http(s) 图片 URL")
    ap.add_argument("--prompt", default=DEFAULT_PROMPT, help="要问的问题（默认：详细描述图片）")
    ap.add_argument("--max-tokens", type=int, default=2048, help="最大输出 token 数")
    ap.add_argument("--screen", action="store_true", help="先截取当前屏幕再分析")
    ap.add_argument("--save", metavar="FILE", help="把回答保存到文件")
    args = ap.parse_args()

    if not args.images and not args.screen:
        ap.error("需要提供至少一张图片，或使用 --screen 截屏")

    images = [ensure_valid_size(p) for p in args.images]
    if args.screen:
        tmp = os.path.join(tempfile.gettempdir(), "qwen_eyes_screen.png")
        capture_screen(tmp)
        images.append(tmp)

    cfg = load_config()
    if not cfg["api_key"]:
        sys.exit("错误: 未找到 API key。请在 tools/qwen-eyes/config.json 中配置，"
                 "或设置环境变量 DASHSCOPE_API_KEY。")

    print(f"[eyes] 模型: {cfg['model']}  图片数: {len(images)}", file=sys.stderr)
    data = call_vision(cfg, images, args.prompt, args.max_tokens)
    msg = data["choices"][0]["message"]

    parts = []
    reasoning = msg.get("reasoning_content")
    if reasoning:
        parts.append("【思考过程】\n" + reasoning.strip())
    parts.append("【回答】\n" + (msg.get("content") or "").strip())
    text = "\n\n".join(parts)
    print(text)

    if args.save:
        with open(args.save, "w", encoding="utf-8") as f:
            f.write(text)
        print(f"[eyes] 已保存到 {args.save}", file=sys.stderr)


if __name__ == "__main__":
    main()

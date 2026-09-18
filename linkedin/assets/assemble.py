import glob
import os
from PIL import Image, ImageDraw, ImageFont

BASE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.dirname(BASE)
FRAMES = os.path.join(OUT, "frames")
STATES = os.path.join(OUT, "states")

FRAME_MS = 50
GIF_W = 380


def load_frames():
    files = sorted(glob.glob(os.path.join(FRAMES, "f_*.png")))
    frames = []
    for f in files:
        im = Image.open(f).convert("RGB")
        if im.width != GIF_W:
            im = im.resize((GIF_W, round(im.height * GIF_W / im.width)), Image.LANCZOS)
        frames.append(im)
    return frames


def build_gif():
    frames = load_frames()
    if not frames:
        print("no frames")
        return
    pal_frames = [f.quantize(colors=128, method=Image.MEDIANCUT, dither=Image.FLOYDSTEINBERG) for f in frames]
    gif = os.path.join(OUT, "demo.gif")
    pal_frames[0].save(
        gif,
        save_all=True,
        append_images=pal_frames[1:],
        duration=FRAME_MS,
        loop=0,
        optimize=True,
        disposal=2,
    )
    print("gif", gif, round(os.path.getsize(gif) / 1024 / 1024, 2), "MB")


def font(size, bold=False):
    candidates = [
        r"C:\Windows\Fonts\segoeuib.ttf" if bold else r"C:\Windows\Fonts\segoeui.ttf",
        r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf",
    ]
    for c in candidates:
        if os.path.exists(c):
            return ImageFont.truetype(c, size)
    return ImageFont.load_default()


def build_states():
    labels = ["Home", "Search", "Create", "Inbox", "Saved"]
    colors = ["#E53946", "#3D9DF6", "#8B5CF6", "#F59E0B", "#10B981"]
    crops = []
    for i in range(5):
        im = Image.open(os.path.join(STATES, f"sel{i}.png")).convert("RGB")
        w, h = im.size
        crops.append(im.crop((0, h - 280, w, h)))

    pad = 40
    row_h = crops[0].height
    gap = 26
    label_w = 150
    W = pad * 2 + label_w + crops[0].width
    H = pad * 2 + row_h * 5 + gap * 4
    canvas = Image.new("RGB", (W, H), (13, 13, 17))
    draw = ImageDraw.Draw(canvas)
    f_label = font(30, bold=True)
    for i, crop in enumerate(crops):
        y = pad + i * (row_h + gap)
        canvas.paste(crop, (pad + label_w, y))
        draw.text((pad, y + row_h // 2 - 18), labels[i], font=f_label, fill=colors[i])

    out = os.path.join(OUT, "indicator_states.png")
    canvas.save(out)
    print("states", out, canvas.size)


if __name__ == "__main__":
    build_gif()
    build_states()

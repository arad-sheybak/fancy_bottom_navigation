import os
from PIL import Image, ImageDraw, ImageFont

BASE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.dirname(BASE)
STATES = os.path.join(OUT, "states")

LABELS = ["Home", "Search", "Create", "Inbox", "Saved"]
COLORS = ["#E53946", "#3D9DF6", "#8B5CF6", "#F59E0B", "#10B981"]


def font(size, bold=False):
    for c in ([r"C:\Windows\Fonts\segoeuib.ttf", r"C:\Windows\Fonts\arialbd.ttf"] if bold
              else [r"C:\Windows\Fonts\segoeui.ttf", r"C:\Windows\Fonts\arial.ttf"]):
        if os.path.exists(c):
            return ImageFont.truetype(c, size)
    return ImageFont.load_default()


def bar_crop(i, bar_h=300):
    im = Image.open(os.path.join(STATES, f"sel{i}.png")).convert("RGB")
    w, h = im.size
    return im.crop((0, h - bar_h, w, h))


def rounded(im, radius):
    mask = Image.new("L", im.size, 0)
    d = ImageDraw.Draw(mask)
    d.rounded_rectangle([0, 0, im.size[0] - 1, im.size[1] - 1], radius=radius, fill=255)
    out = im.convert("RGBA")
    out.putalpha(mask)
    return out


def showcase():
    gap, pad, radius = 56, 60, 44
    crops = []
    for i in (0, 2, 4):
        im = Image.open(os.path.join(STATES, f"sel{i}.png")).convert("RGB")
        w, h = im.size
        crops.append(rounded(im.crop((0, 470, w, h)), radius))
    w = crops[0].width
    hh = crops[0].height
    W = pad * 2 + w * 3 + gap * 2
    H = pad * 2 + hh
    canvas = Image.new("RGB", (W, H), (11, 11, 15))
    for i, c in enumerate(crops):
        x = pad + i * (w + gap)
        canvas.paste(c, (x, pad), c)
    out = os.path.join(OUT, "showcase.png")
    canvas.save(out)
    print("showcase", out, canvas.size)


def arch_zoom():
    im = Image.open(os.path.join(STATES, "sel2.png")).convert("RGB")
    w, h = im.size
    bar = im.crop((0, h - 300, w, h))
    bw, bh = bar.size
    zoom = bar.crop((int(bw * 0.30), int(bh * 0.02), int(bw * 0.70), int(bh * 1.0)))
    zoom = zoom.resize((zoom.width * 2, zoom.height * 2), Image.LANCZOS)
    out = os.path.join(OUT, "arch_zoom.png")
    zoom.save(out)
    print("arch_zoom", out, zoom.size)


def states_grid():
    gap, pad = 28, 40
    label_w = 130
    cell = bar_crop(0, 240)
    cw, ch = cell.size
    cols, rows = 3, 2
    W = pad * 2 + cols * (label_w + cw) + (cols - 1) * gap
    H = pad * 2 + rows * ch + (rows - 1) * gap
    canvas = Image.new("RGB", (W, H), (13, 13, 17))
    draw = ImageDraw.Draw(canvas)
    f = font(30, bold=True)
    for i in range(5):
        r, c = divmod(i, cols)
        x = pad + c * (label_w + cw + gap)
        y = pad + r * (ch + gap)
        canvas.paste(bar_crop(i, 240), (x + label_w, y))
        draw.text((x + 6, y + ch // 2 - 18), LABELS[i], font=f, fill=COLORS[i])
    out = os.path.join(OUT, "states_grid.png")
    canvas.save(out)
    print("states_grid", out, canvas.size)


def save_bars():
    for i in range(5):
        im = bar_crop(i, 300)
        out = os.path.join(OUT, f"bar_{i}.png")
        im.save(out)
        print("bar", out, im.size)


if __name__ == "__main__":
    showcase()
    arch_zoom()
    states_grid()
    save_bars()

# LinkedIn Pack — Fancy Bottom Navigation

Everything you need is in this folder:

- `fancy_bottom_navigation.pdf` — 8-slide **document post** (upload as a LinkedIn "Document").
- `demo.gif` — animated demo for a **regular image post** (upload as a photo/GIF).
- `hero.png`, `showcase.png`, `indicator_states.png`, `states_grid.png`, `arch_zoom.png` — still images.
- `post.md` — copy-paste post text (English + Persian), first comment and alt text.

> Note: LinkedIn does not let one post contain both a document and images. Choose **one** format:
> - **Document post** → upload the PDF with the caption below.
> - **Media post** → upload `demo.gif` (or `showcase.png`) with the same caption.

---

## Recommended caption (English)

Most Android bottom nav bars are flat: a pill, some icons, a fade.

I wanted the selection to feel hand-drawn.

So I rebuilt it from scratch with Jetpack Compose Canvas — one continuous Bézier stroke that wraps the active icon and trails off into the bar.

A few things I learned building it:

The crown is an ellipse made from two cubic Béziers using K = 4/3(√2−1) ≈ 0.5523, so there are no corners, no flat top and no straight vertical edges.

Short concave fillets blend the ellipse sides into the horizontal tails. The ellipse is centered on the measured icon center — not the item or the label.

The animation is a path-reveal, not a fade. Geometry stays fixed at the selected item and PathMeasure.getSegment() redraws along the real curve. A fresh Animatable per selection cancels any in-flight reveal.

Each destination owns one color that drives both the indicator stroke and the icon tint, so they can never drift apart.

The horizontal tail budget transfers from right to left as the selection moves, so the first item keeps only the right tail and the last only the left — the sum never changes.

The navigation layer is built on Navigation 3 (NavDisplay + NavBackStack) and persisted with a custom listSaver, so no kotlinx.serialization is needed.

Stack: Kotlin 2.2, Compose BOM 2026.02.01, Navigation 3 1.1.6, Material 3, minSdk 26 / targetSdk 37. Zero icon libraries — every icon is compiled from a raw SVG path string at runtime.

Full source is on GitHub (link in the first comment).

If you could rebuild any mobile component from scratch, which one would you pick: a curved app bar or a morphing FAB? Drop it in the comments — I will build the winner next.

#Android #JetpackCompose #Kotlin #AndroidDev #MobileDevelopment #OpenSource

---

## First comment (post this yourself, right after publishing)

Source code → https://github.com/arad-sheybak/fancy_bottom_navigation

Built with Jetpack Compose Canvas + Navigation 3. Stars and feedback are very welcome.

---

## Caption (Persian)

بیشتر باتم‌نویگیشن‌های اندروید صاف و ساده‌اند: یک پیل، چند آیکون و یک fade.

من می‌خواستم انتخاب شدن، حس «ترسیم شدن» داشته باشد.

برای همین از صفر با Jetpack Compose Canvas بازش کردم — یک خط پیوسته‌ی بزیه که دور آیکون انتخاب‌شده می‌پیچد و در نوار ادامه پیدا می‌کند.

چند نکته‌ی فنی که در مسیر ساخت یاد گرفتم:

تاج منحنی یک بیضی است که از دو بزیه‌ی مکعبی با ثابت K = 4/3(√2−1) ≈ 0.5523 ساخته می‌شود؛ بدون گوشه، بدون سطح صاف و بدون لبه‌ی عمودی.

فیلت‌های مقعر کناره‌های بیضی را به دنباله‌های افقی وصل می‌کنند. بیضی روی مرکز اندازه‌گیری‌شده‌ی آیکون وسط‌چین می‌شود، نه روی مرکز آیتم یا لیبل.

انیمیشن یک path-reveal است، نه fade. هندسه ثابت می‌ماند و PathMeasure.getSegment دوباره روی مسیر واقعی ترسیم می‌کند. برای هر انتخاب یک Animatable تازه ساخته می‌شود تا انیمیشن نیمه‌کاره‌ی قبلی لغو شود.

هر مقصد یک رنگ دارد که هم استروک ایندیکاتور و هم رنگ آیکون انتخاب‌شده را کنترل می‌کند؛ پس هرگز از هم جدا نمی‌شوند.

بودجه‌ی دنباله‌ی افقی با جابه‌جا شدن انتخاب از راست به چپ منتقل می‌شود؛ آیتم اول فقط دنباله‌ی راست و آیتم آخر فقط دنباله‌ی چپ را نگه می‌دارد و مجموع ثابت می‌ماند.

لایه‌ی ناوبری روی Navigation 3 ساخته شده (NavDisplay + NavBackStack) و با یک listSaver سفارشی ذخیره می‌شود؛ بدون نیاز به kotlinx.serialization.

استک: Kotlin 2.2، Compose BOM 2026.02.01، Navigation 3 1.1.6، Material 3، minSdk 26 / targetSdk 37. بدون هیچ کتابخانه‌ی آیکون — همه‌ی آیکون‌ها در زمان اجرا از رشته‌ی مسیر SVG ساخته می‌شوند.

سورس کامل روی گیت‌هاب است (لینک در اولین کامنت).

اگر می‌توانستید یک کامپوننت موبایل را از صفر بسازید، کدام را انتخاب می‌کردید: یک app bar منحنی یا یک FAB مرفینگ؟ در کامنت بنویسید — برنده را می‌سازم.

#Android #JetpackCompose #Kotlin #AndroidDev #MobileDevelopment #OpenSource

---

## Alt text (for accessibility)

- **showcase.png** — Three phone screens on a dark background showing the floating bottom navigation bar with the Home (red), Create (purple) and Saved (green) destinations selected, each with a glowing elliptical indicator.
- **demo.gif** — Screen recording of the bottom bar: tapping each destination makes the colored indicator redraw itself with a path-reveal animation.
- **states_grid.png** — Five close-ups of the bottom bar, one per destination, showing the indicator color, position and tail direction change.
- **arch_zoom.png** — Close-up of the indicator over the Create icon: an elliptical crown blended into horizontal tails.

---

## Posting checklist

1. Choose format: **Document** (PDF) *or* **Photo/GIF** (`demo.gif`).
2. Paste the English caption (recommended) or the Persian caption.
3. For the document post, the PDF thumbnail is page 1 — it already has the hook title.
4. Publish, then immediately add the **first comment** with the GitHub link.
5. Reply to comments within the first hour for reach.

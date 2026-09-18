import puppeteer from "puppeteer-core";
import { fileURLToPath } from "node:url";
import path from "node:path";
import fs from "node:fs";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const mockup = "file:///" + path.resolve(__dirname, "mockup.html").replace(/\\/g, "/");
const outDir = path.resolve(__dirname, "..");
const framesDir = path.join(outDir, "frames");
fs.mkdirSync(framesDir, { recursive: true });

const CHROME =
  process.env.CHROME_PATH ||
  "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";

const browser = await puppeteer.launch({
  executablePath: CHROME,
  headless: "new",
  args: ["--hide-scrollbars", "--force-color-profile=srgb", "--disable-gpu"],
});

const page = await browser.newPage();
await page.goto(mockup, { waitUntil: "networkidle0" });
await page.waitForFunction("window.__rendered === true");

async function shot(sel, p, file, scale) {
  await page.setViewport({ width: 380, height: 820, deviceScaleFactor: scale });
  await page.evaluate((s, pp) => window.render(s, pp), sel, p);
  await page.screenshot({ path: file });
}

// Hero stills at 2x
for (let sel = 0; sel < 5; sel++) {
  await shot(sel, 1, path.join(outDir, `states/sel${sel}.png`), 2);
}
await shot(0, 1, path.join(outDir, "hero.png"), 2);
await shot(0, 1, path.join(outDir, "app_preview.png"), 1);

// Animated GIF frames at 1x
const scenes = [];
for (let i = 0; i < 10; i++) scenes.push({ sel: 0, p: 1 });
const steps = 10;
for (let from = 0; from < 4; from++) {
  const to = from + 1;
  for (let i = 0; i < steps; i++) scenes.push({ sel: to, p: i / (steps - 1) });
  for (let i = 0; i < 6; i++) scenes.push({ sel: to, p: 1 });
}

await page.setViewport({ width: 380, height: 820, deviceScaleFactor: 1 });
let idx = 0;
for (const s of scenes) {
  await page.evaluate((ss, pp) => window.render(ss, pp), s.sel, s.p);
  await page.screenshot({ path: path.join(framesDir, `f_${String(idx).padStart(3, "0")}.png`) });
  idx++;
}

console.log(`Captured ${scenes.length} frames.`);
await browser.close();

import puppeteer from "puppeteer-core";
import { fileURLToPath } from "node:url";
import path from "node:path";
import fs from "node:fs";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const url = "file:///" + path.resolve(__dirname, "document.html").replace(/\\/g, "/");
const out = path.resolve(__dirname, "..", "doc_preview");
fs.mkdirSync(out, { recursive: true });

const browser = await puppeteer.launch({
  executablePath: "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
  headless: "new",
  args: ["--allow-file-access-from-files", "--hide-scrollbars", "--force-color-profile=srgb"],
});
const page = await browser.newPage();
await page.setViewport({ width: 1080, height: 1350, deviceScaleFactor: 0.5 });
await page.goto(url, { waitUntil: "networkidle0" });
const slides = await page.$$(".slide");
for (let i = 0; i < slides.length; i++) {
  await slides[i].screenshot({ path: path.join(out, `slide_${i + 1}.png`) });
}
console.log("captured", slides.length, "slides");
await browser.close();

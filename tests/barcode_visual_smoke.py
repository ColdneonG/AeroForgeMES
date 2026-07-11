from pathlib import Path

from playwright.sync_api import sync_playwright


ROOT = Path(__file__).resolve().parents[1]
BASE_URL = "http://127.0.0.1:5173"


def main() -> None:
    console_errors: list[str] = []
    page_errors: list[str] = []
    with sync_playwright() as playwright:
        browser = playwright.chromium.launch(headless=True)
        context = browser.new_context(viewport={"width": 1600, "height": 1000})
        page = context.new_page()
        page.on("console", lambda message: console_errors.append(message.text) if message.type == "error" else None)
        page.on("pageerror", lambda error: page_errors.append(str(error)))

        page.goto(BASE_URL + "/login?redirect=/barcode/rules", wait_until="networkidle")
        page.locator('input[autocomplete="username"]').fill("test_operator")
        page.locator('input[autocomplete="current-password"]').fill("123456")
        page.locator('button[type="submit"]').click()
        page.wait_for_url(lambda url: "/login" not in url)
        page.locator(".siemens-shell").wait_for()
        token_length = page.evaluate("() => (localStorage.getItem('fan-mes-token') || '').length")
        if token_length == 0:
            raise RuntimeError("login completed without a persisted token")

        pages = [
            ("/barcode/rules", ".codex-barcode-rules.png", ".barcode-page h1"),
            ("/barcode/generate", ".codex-barcode-generate.png", ".barcode-page h1"),
            ("/traceability", ".codex-barcode-trace.png", ".trace-page h1"),
        ]
        for path, screenshot, heading_selector in pages:
            page.goto(BASE_URL + path, wait_until="networkidle")
            page.wait_for_timeout(800)
            page.screenshot(path=str(ROOT / screenshot), full_page=True)
            page.locator(heading_selector).wait_for()

        page.goto(BASE_URL + "/barcode/generate", wait_until="networkidle")
        page.locator(".page-heading .primary-btn").click()
        page.locator(".dialog h2").wait_for()
        page.screenshot(path=str(ROOT / ".codex-barcode-generate-dialog.png"), full_page=True)
        browser.close()

    if console_errors or page_errors:
        raise RuntimeError(f"console_errors={console_errors}; page_errors={page_errors}")

    print("barcode visual smoke passed: 3 pages + generation dialog")


if __name__ == "__main__":
    main()

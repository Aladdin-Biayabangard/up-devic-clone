//package com.team.updevic001.utility.screenshot;
//
//import com.microsoft.playwright.*;
//import com.microsoft.playwright.options.LoadState;
//import com.microsoft.playwright.options.ScreenshotType;
//import com.microsoft.playwright.options.WaitForSelectorState;
//import com.team.updevic001.dao.entities.ScreenshotConfigEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.Objects;
//
//@Service
//public class ScreenshotService {
//
//    private final Browser browser;
//
//    public ScreenshotService(Browser browser) {
//        this.browser = browser;
//    }
//
//    public byte[] capture(ScreenshotConfigEntity req) {
//        Browser.NewContextOptions ctxOpts = new Browser.NewContextOptions()
//                .setViewportSize(
//                        Objects.requireNonNullElse(req.getWidth(), 1280),
//                        Objects.requireNonNullElse(req.getHeight(), 1024))
//                .setDeviceScaleFactor(Objects.requireNonNullElse(req.getScale(), 1.5));
//
//        try (BrowserContext ctx = browser.newContext(ctxOpts)) {
//            Page page = ctx.newPage();
//
//            page.navigate(req.getUrl(), new Page.NavigateOptions()
//                    .setTimeout(Objects.requireNonNullElse(req.getNavTimeoutMs(), 60000L)));
//
//            page.waitForLoadState(LoadState.NETWORKIDLE);
//
//            // Disable animations for pixel-stable captures
//            page.addStyleTag(new Page.AddStyleTagOptions().setContent(
//                    "*{animation:none!important;transition:none!important;}"));
//
//            // small settle delay
//            page.waitForTimeout(Objects.requireNonNullElse(req.getWaitForMs(), 300L));
//
//            String format = (req.getFormat() == null ? "png" : req.getFormat()).toLowerCase();
//            boolean png = format.equals("png");
//
//            if ("fullpage".equalsIgnoreCase(req.getType())) {
//                Page.ScreenshotOptions opt = new Page.ScreenshotOptions()
//                        .setFullPage(true)
//                        .setOmitBackground(Boolean.TRUE.equals(req.getOmitBackground()))
//                        .setType(png ? ScreenshotType.PNG : ScreenshotType.JPEG);
//
//                if (!png && req.getQuality() != null) opt.setQuality(req.getQuality());
//                return page.screenshot(opt);
//            }
//
//            // element
//            if (req.getSelector() == null || req.getSelector().isBlank()) {
//                throw new IllegalArgumentException("selector is required when type='element'");
//            }
//            Locator el = page.locator(req.getSelector()).first();
//            el.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
//            el.scrollIntoViewIfNeeded();
//
//            Locator.ScreenshotOptions opt = new Locator.ScreenshotOptions()
//                    .setOmitBackground(Boolean.TRUE.equals(req.getOmitBackground()))
//                    .setType(png ? ScreenshotType.PNG : ScreenshotType.JPEG);
//
//            if (!png && req.getQuality() != null) opt.setQuality(req.getQuality());
//            return el.screenshot(opt);
//        }
//    }
//}

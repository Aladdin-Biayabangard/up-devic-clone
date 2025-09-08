package com.team.updevic001.utility.screenshot;

import com.microsoft.playwright.*;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class PlaywrightConfig {

    private Playwright playwright;
    private Browser browser;

    @Bean
    public Browser chromiumBrowser() {
        playwright = Playwright.create();
        BrowserType.LaunchOptions launch = new BrowserType.LaunchOptions()
                .setHeadless(true)
                .setArgs(Arrays.asList(new String[]{"--no-sandbox", "--disable-setuid-sandbox"}));
        browser = playwright.chromium().launch(launch);
        return browser;
    }

    @PreDestroy
    public void shutdown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
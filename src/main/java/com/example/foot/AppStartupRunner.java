package com.example.foot;

import com.example.foot.service.SeleniumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppStartupRunner implements ApplicationRunner {

    private final SeleniumService seleniumService;

    @Autowired
    public AppStartupRunner(SeleniumService seleniumService) {
        this.seleniumService = seleniumService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seleniumService.scrapeData();
    }
}

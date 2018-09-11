package com.base.web.bootstrap;

import com.base.web.Run;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Project: base-framework
 * Author: Sendya <18x@loacg.com>
 * Date: 2017/6/7 16:18
 */
public class ServletInitializer extends SpringBootServletInitializer {

    static {
        System.setProperty("spring.output.ansi.enabled", "ALWAYS");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Run.class);
    }

}

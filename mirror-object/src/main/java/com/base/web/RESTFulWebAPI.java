package com.base.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * ${DESCRIPTION}
 *
 * @author xianghugui
 * 项目访问地址：http://localhost:8080/swagger-ui.html
 * @create 2018-03-31 05:45
 */
@EnableSwagger2
@Configuration
public class RESTFulWebAPI {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //生成api文档包路径
                .apis(RequestHandlerSelectors.basePackage("com.base.web.controller.api"))
                .paths(PathSelectors.any())
                .build();
    }
    //构建 api文档的详细信息函数
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("知晓物联技术服务有限公司java前后端接口对接文档")
                //创建人
                .contact(new Contact("arnoldlve", "arnoldlve@icloud.com", "arnoldlve@icloud.com"))
                //版本号
                .version("0.0.0.1")
                //描述
                .description("API 描述")
                .build();
    }
}

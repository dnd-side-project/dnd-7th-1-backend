package com.dnd.ground.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @description 클라이언트가 Swagger를 편하게 이용하기 위함.
 * @author  박찬호
 * @since   2022-08-10
 * @updated 1. 클래스 생성
 *          - 2022.08.10 박찬호
 */

@Controller
public class SwaggerController {

    @GetMapping("/doc")
    public String redirectSwagger() {
        return "redirect:/swagger-ui/index.html";
    }
}

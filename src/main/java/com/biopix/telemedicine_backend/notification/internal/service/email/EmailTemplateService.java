package com.biopix.telemedicine_backend.notification.internal.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public String render(String templateName) {

        Context context = new Context();

        return templateEngine.process(
                "email/" + templateName,
                context
        );
    }

    public String render(String templateName, Map<String, Object> model) {

        Context context = new Context();

        context.setVariables(model);

        return templateEngine.process(
                "email/" + templateName,
                context
        );
    }
}
package com.override.orchestrator_service.service.interceptor;

import com.override.orchestrator_service.annotations.OnlyServiceUse;
import com.override.orchestrator_service.exception.InternalKeyNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestHeaderInterceptor implements HandlerInterceptor {
    @Value("${filters.authorization-header.header-value}")
    private String secretKey;
    private final String HEADER_NAME = "X-INTERNAL-KEY";

    /**
     * Метод перехватывает все http запросы к сервису и в случае если метод(котроллера),
     * который обраьытывает запрос помечан аннотацией @OnlyServiceUse,
     * происходит проверка хедера на наличие  Internal Key,
     * если в запросе отсутсвеут ключ или ключ не верный выбрасывается исключение и отказ в доступе,
     * если ключ верный или метод(котроллера) не помечан аннотацией @OnlyServiceUse,
     * происходит отработка метода контроллера.
     *
     * @throws InternalKeyNotFoundException (отказ в доступе)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            OnlyServiceUse onlyServiceUse = ((HandlerMethod) handler).getMethodAnnotation(OnlyServiceUse.class);

            if (onlyServiceUse != null) {
                String internalKeyRequest = request.getHeader(HEADER_NAME);

                if (internalKeyRequest == null || !internalKeyRequest.equals(secretKey)) {
                    throw new InternalKeyNotFoundException("Доступ к странице запрещен");
                }
            }
        }
        return true;
    }
}

package com.override.orchestrator_service.annotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Интерфейс-аннотация
 *
 * @author dementyD
 * Данная аннотация используется для защиты ендпоинтов,
 * преднозначенных только! для взаимодействия между внутренними сервисами.
 * Устанавливается на методы в контроллерах.
 * При установки аннотации на метод, будет проверяться хедер запроса на наличие и соответствие Internal Key.
 * @see com.override.orchestrator_service.service.interceptor.RequestHeaderInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyServiceUse {
}

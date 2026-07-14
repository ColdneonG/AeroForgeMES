package com.fanmes.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionCheckAspect {

    private final HttpServletRequest request;

    public PermissionCheckAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(com.fanmes.common.security.RequirePermission) || @within(com.fanmes.common.security.RequirePermission)")
    public Object check(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        RequirePermission required = AnnotatedElementUtils.findMergedAnnotation(
                signature.getMethod(), RequirePermission.class);
        if (required == null) {
            required = AnnotatedElementUtils.findMergedAnnotation(
                    point.getTarget().getClass(), RequirePermission.class);
        }
        if (required == null) {
            return point.proceed();
        }

        Object value = request.getAttribute(JwtAuthenticationFilter.AUTHENTICATED_USER_ATTRIBUTE);
        if (!(value instanceof AuthenticatedUser user) || !user.permissions().contains(required.value())) {
            throw new PermissionDeniedException("缂哄皯鏉冮檺: " + required.value());
        }
        return point.proceed();
    }
}

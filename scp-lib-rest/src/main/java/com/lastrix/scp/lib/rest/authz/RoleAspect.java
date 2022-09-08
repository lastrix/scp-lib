package com.lastrix.scp.lib.rest.authz;

import com.lastrix.scp.lib.rest.error.ServiceErrorException;
import com.lastrix.scp.lib.rest.error.SystemError;
import com.lastrix.scp.lib.rest.jwt.Jwt;
import com.lastrix.scp.lib.rest.jwt.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scp.authz.enable", havingValue = "true", matchIfMissing = true)
public class RoleAspect {
    private final Jwt jwt;
    private final HttpServletRequest request;

    @Around(value = "@annotation(requireRoles)", argNames = "joinPoint,requireRoles")
    public Object intercept(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        return checkedAccessFor(joinPoint, requireRoles.userTypes(), requireRoles.value());
    }

    private Object checkedAccessFor(ProceedingJoinPoint joinPoint, UserType[] userTypes, String[] roles) throws Throwable {
        if (roles == null || roles.length == 0) throw new IllegalArgumentException("No roles supplied");
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null || token.isBlank())
            throw new ServiceErrorException(SystemError.AUTH_FAILED, Map.of("token", "<null>"));
        UserType userType = jwt.getUserType();
        if (userTypes.length != 0 && !hasAccountType(userTypes, userType))
            throw new IllegalStateException("Not access for account type '" + userType + "' to: " + joinPoint.getSignature());

        List<String> roleList = Arrays.asList(roles);
        if (!isAllowedByJwt(roleList)) throw new IllegalStateException("No access to:  " + joinPoint.getSignature());
        return joinPoint.proceed();
    }

    private boolean isAllowedByJwt(List<String> roleList) {
        var roles = jwt.getRoles();
        return roles.containsAll(roleList);
    }

    private boolean hasAccountType(UserType[] types, UserType type) {
        for (UserType t : types) {
            if (t == type) return true;
        }
        return false;
    }
}

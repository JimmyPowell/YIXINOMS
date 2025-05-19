package tech.cspioneer.backend.security;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义认证入口点
 * 处理未认证的请求，返回401状态码
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException {
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", "401");
        responseBody.put("message", "用户未认证");
        responseBody.put("data", null);
        
        response.getWriter().write(JSON.toJSONString(responseBody));
    }
} 
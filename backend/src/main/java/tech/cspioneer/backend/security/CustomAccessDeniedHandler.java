package tech.cspioneer.backend.security;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义访问拒绝处理器
 * 处理已认证但权限不足的请求，返回403状态码
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException {
        
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("code", "403");
        responseBody.put("message", "权限不足");
        responseBody.put("data", null);
        
        response.getWriter().write(JSON.toJSONString(responseBody));
    }
} 
package tech.cspioneer.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.cspioneer.backend.entity.request.GenerateVerifyCodeRequest;
import tech.cspioneer.backend.entity.request.UserLoginRequest;
import tech.cspioneer.backend.entity.request.UserRegisterRequest;
import tech.cspioneer.backend.entity.request.VerifyCodeRequest;
import tech.cspioneer.backend.entity.response.HttpResponseEntity;
import tech.cspioneer.backend.entity.response.SessionResponse;
import tech.cspioneer.backend.entity.response.ValidationResult;
import tech.cspioneer.backend.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 获取session
     *
     * @param request
     * @return
     */
    @PostMapping("/get-session-and-verify-code")
    public ResponseEntity<HttpResponseEntity> getSession(@RequestBody GenerateVerifyCodeRequest request) {
        // 创建响应体
        HttpResponseEntity responseEntity = new HttpResponseEntity();

        try {
            String session = authService.generateSessionAndVerifyCode(request.getEmail());
            SessionResponse sessionResponse = new SessionResponse(session);
            responseEntity.setCode("200");
            responseEntity.setMessage("获取session成功,验证码已发送");
            responseEntity.setData(sessionResponse);
            return new ResponseEntity<>(responseEntity, HttpStatus.OK);
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("获取session失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 验证码验证
     *
     * @param request
     * @return
     */
    @PostMapping("/verify-code")
    public ResponseEntity<HttpResponseEntity> verifyCode(@RequestBody VerifyCodeRequest request) {
        // 创建响应体
        HttpResponseEntity responseEntity = new HttpResponseEntity();

        try {
            boolean isVerified = authService.verifyCode(request.getSessionId(), request.getCode()); 
            if (isVerified) {
                responseEntity.setCode("200");
                responseEntity.setMessage("验证码验证成功");
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.OK);
            }
            responseEntity.setCode("400");
            responseEntity.setMessage("验证码验证失败");
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("验证码验证失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<HttpResponseEntity> register(@RequestBody UserRegisterRequest request) {
        // 创建响应体
        HttpResponseEntity responseEntity = new HttpResponseEntity();

        try {
            // 调用Service层验证参数
            ValidationResult validationResult = authService.validateRegisterRequest(request);
            
            // 如果验证不通过，直接返回错误信息
            if (!validationResult.isValid()) {
                responseEntity.setCode(validationResult.getCode());
                responseEntity.setMessage(validationResult.getMessage());
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
            }
            
            // 调用注册服务
            boolean isRegistered = authService.register(request);
            
            if (isRegistered) {
                responseEntity.setCode("200");
                responseEntity.setMessage("注册成功");
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.OK);
            } else {
                responseEntity.setCode("400");
                responseEntity.setMessage("注册失败，服务器内部错误");
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("用户注册失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<HttpResponseEntity> login(@RequestBody UserLoginRequest request) {
        // 创建响应体
        HttpResponseEntity responseEntity = new HttpResponseEntity();

        try {
            // 调用登录服务
            ValidationResult result = authService.login(request);
            
            if (result.isValid()) {
                responseEntity.setCode("200");
                responseEntity.setMessage("登录成功");
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.OK);
            } else {
                responseEntity.setCode(result.getCode());
                responseEntity.setMessage(result.getMessage());
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("登录失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
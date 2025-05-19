package tech.cspioneer.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.cspioneer.backend.entity.request.UserUpdateRequest;
import tech.cspioneer.backend.entity.response.HttpResponseEntity;
import tech.cspioneer.backend.entity.response.ValidationResult;
import tech.cspioneer.backend.service.UserService;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<HttpResponseEntity> getUserProfile() {
        HttpResponseEntity responseEntity = new HttpResponseEntity();
        
        try {
            // 调用服务层获取当前用户信息
            ValidationResult result = userService.getCurrentUserProfile();
            
            if (result.isValid()) {
                responseEntity.setCode("200");
                responseEntity.setMessage("获取用户信息成功");
                responseEntity.setData(result.getData());
                return new ResponseEntity<>(responseEntity, HttpStatus.OK);
            } else {
                // 根据错误代码决定HTTP状态码
                HttpStatus httpStatus;
                if ("404".equals(result.getCode())) {
                    httpStatus = HttpStatus.NOT_FOUND;
                } else if ("401".equals(result.getCode())) {
                    httpStatus = HttpStatus.UNAUTHORIZED;
                } else if ("403".equals(result.getCode())) {
                    httpStatus = HttpStatus.FORBIDDEN;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
                
                responseEntity.setCode(result.getCode());
                responseEntity.setMessage(result.getMessage());
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, httpStatus);
            }
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("获取用户信息失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 更新用户信息
     * @param request 更新请求
     * @return 更新结果
     */
    
    @PutMapping("/profile")
    public ResponseEntity<HttpResponseEntity> updateUserProfile(@RequestBody UserUpdateRequest request) {
        HttpResponseEntity responseEntity = new HttpResponseEntity();
        
        try {
            // 调用服务层更新当前用户信息
            ValidationResult result = userService.updateCurrentUserProfile(request);
            
            if (result.isValid()) {
                responseEntity.setCode("200");
                responseEntity.setMessage("更新用户信息成功");
                responseEntity.setData(result.getData());
                return new ResponseEntity<>(responseEntity, HttpStatus.OK);
            } else {
                // 根据错误代码决定HTTP状态码
                HttpStatus httpStatus;
                if ("401".equals(result.getCode())) {
                    httpStatus = HttpStatus.UNAUTHORIZED;
                } else if ("403".equals(result.getCode())) {
                    httpStatus = HttpStatus.FORBIDDEN;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
                
                responseEntity.setCode(result.getCode());
                responseEntity.setMessage(result.getMessage());
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, httpStatus);
            }
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("更新用户信息失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 注销用户
     * @return 注销结果
     */
    @DeleteMapping("/deactivate")
    public ResponseEntity<HttpResponseEntity> deactivateUser() {
        HttpResponseEntity responseEntity = new HttpResponseEntity();
        
        try {
            // 调用服务层注销当前用户
            ValidationResult result = userService.deactivateCurrentUser();
            
            if (result.isValid()) {
                responseEntity.setCode("200");
                responseEntity.setMessage("注销用户成功");
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, HttpStatus.OK);
            } else {
                // 根据错误代码决定HTTP状态码
                HttpStatus httpStatus;
                if ("401".equals(result.getCode())) {
                    httpStatus = HttpStatus.UNAUTHORIZED;
                } else if ("403".equals(result.getCode())) {
                    httpStatus = HttpStatus.FORBIDDEN;
                } else {
                    httpStatus = HttpStatus.BAD_REQUEST;
                }
                
                responseEntity.setCode(result.getCode());
                responseEntity.setMessage(result.getMessage());
                responseEntity.setData(null);
                return new ResponseEntity<>(responseEntity, httpStatus);
            }
        } catch (Exception e) {
            responseEntity.setCode("500");
            responseEntity.setMessage("注销用户失败: " + e.getMessage());
            responseEntity.setData(null);
            return new ResponseEntity<>(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 
package tech.cspioneer.backend.service;

import tech.cspioneer.backend.entity.User;
import tech.cspioneer.backend.entity.request.UserLoginRequest;
import tech.cspioneer.backend.entity.request.UserRegisterRequest;
import tech.cspioneer.backend.entity.response.ValidationResult;

public interface AuthService {
    /**
     * 生成session和验证码
     * @param email 用户邮箱
     * @return 生成的会话ID
     */
    public String generateSessionAndVerifyCode(String email);

    /**
     * 发送验证码
     * @param email 用户邮箱
     * @return 是否发送成功
     */
    public boolean sendVerifyCode(String email);

    /**
     * 验证码校验
     * @param sessionId 会话ID
     * @param code 验证码
     * @return 是否验证成功
     */
    public boolean verifyCode(String sessionId, String code);

    /**
     * 验证注册参数
     * @param request 注册请求
     * @return 验证结果
     */
    public ValidationResult validateRegisterRequest(UserRegisterRequest request);

    /**
     * 用户注册
     * @param request 注册请求
     * @return 是否注册成功
     */
    public boolean register(UserRegisterRequest request);

    /**
     * 根据邮箱查询用户
     * @param email 用户邮箱
     * @return 用户
     */
    public User getUserByEmail(String email);

    /**
     * 用户登录
     * @param request 登录请求
     * @return 验证结果
     */
    public ValidationResult login(UserLoginRequest request);
}

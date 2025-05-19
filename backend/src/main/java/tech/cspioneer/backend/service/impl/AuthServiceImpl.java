package tech.cspioneer.backend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.cspioneer.backend.entity.User;
import tech.cspioneer.backend.entity.request.UserLoginRequest;
import tech.cspioneer.backend.entity.request.UserRegisterRequest;
import tech.cspioneer.backend.entity.response.ValidationResult;
import tech.cspioneer.backend.mapper.UserMapper;
import tech.cspioneer.backend.service.AuthService;
import tech.cspioneer.backend.util.CaptchaUtils;
import tech.cspioneer.backend.util.RedisUtils;
import tech.cspioneer.backend.util.SMTPUtils;
import tech.cspioneer.backend.util.UuidUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SMTPUtils smtpUtils;

    @Autowired
    private CaptchaUtils captchaUtils;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String generateSessionAndVerifyCode(String email) {
        // 生成session
        String session = UuidUtils.generateUuidWithoutDash();
        System.out.println("session: " + session);
        System.out.println("email: " + email);
        //生成验证码
        String verifyCode = captchaUtils.generateCaptcha();
        System.out.println("verifyCode: " + verifyCode);

        // 将session、verifyCode存储到redis
        // 使用JSONObject构建JSON以确保格式正确
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("session", session);
        jsonObject.put("verifyCode", verifyCode);
        jsonObject.put("is_verified", false);
        
        RedisUtils.set(email, jsonObject.toJSONString(), 1000 * 60 * 60 * 24, 0);
        
        // 同时存储session到email的映射，方便查找
        RedisUtils.set("session:" + session, email, 1000 * 60 * 60 * 24, 0);
        
        // 发送验证码
        smtpUtils.sendEmail(email, "验证码", "您的验证码是: " + verifyCode);

        return session;
    }

    @Override
    public boolean sendVerifyCode(String email) {
        try {
            // 生成6位验证码
            String verifyCode = captchaUtils.generateCaptcha();
            System.out.println("验证码: " + verifyCode + ", 邮箱: " + email);
            
            // 构建邮件内容
            String title = "验证码服务 - 注册验证";
            String content = String.format(
                "<div style='font-family: Arial, sans-serif; max-width: 600px;'>" +
                "<h2 style='color: #333;'>邮箱验证码</h2>" +
                "<p>您好！</p>" +
                "<p>您的验证码是: <strong style='font-size: 18px; color: #e74c3c;'>%s</strong></p>" +
                "<p>验证码有效期为10分钟，请及时使用。</p>" +
                "<p>如果您没有请求此验证码，请忽略此邮件。</p>" +
                "<p>谢谢！</p>" +
                "<p style='color: #888; font-size: 12px; margin-top: 30px;'>此邮件由系统自动发送，请勿回复。</p>" +
                "</div>",
                verifyCode
            );
            
            // 验证码有效期10分钟
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("verify_code", verifyCode);
            jsonObject.put("created_at", System.currentTimeMillis());
                
            RedisUtils.set("verify:" + email, jsonObject.toJSONString(), 60 * 10, 1);
            
            // 发送邮件
            return smtpUtils.sendEmail(email, title, content);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("发送验证码失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyCode(String sessionId, String code) {
        try {
            System.out.println("验证码检查：sessionId=" + sessionId + ", code=" + code);
            
            // 先尝试通过session映射找到email
            String email = RedisUtils.get("session:" + sessionId, 0);
            
            if (email != null) {
                System.out.println("通过session映射找到email: " + email);
                // 验证该email下的验证码
                String jsonValue = RedisUtils.get(email, 0);
                if (jsonValue != null) {
                    try {
                        JSONObject jsonObject = JSON.parseObject(jsonValue);
                        if (jsonObject.containsKey("verifyCode")) {
                            String storedVerifyCode = jsonObject.getString("verifyCode");
                            System.out.println("存储的验证码: " + storedVerifyCode + ", 提交的验证码: " + code);
                            
                            if (code.equals(storedVerifyCode)) {
                                // 验证成功，更新状态
                                jsonObject.put("is_verified", true);
                                RedisUtils.set(email, jsonObject.toJSONString(), 1000 * 60 * 60 * 24, 0);
                                return true;
                            }
                            // 验证码不匹配
                            return false;
                        }
                    } catch (JSONException e) {
                        System.err.println("解析JSON失败: " + e.getMessage() + ", JSON值: " + jsonValue);
                    }
                }
                return false;
            }
            
            // 如果没找到映射，再尝试遍历所有键
            System.out.println("通过映射未找到email，尝试遍历所有键");
            
            // 获取所有可能的键（邮箱）
            String[] keys = RedisUtils.keys(0);
            System.out.println("查找到 " + keys.length + " 个Redis键");
            
            for (String key : keys) {
                // 排除非邮箱的键（如验证码相关的键或session映射）
                if (key.startsWith("verify:") || key.startsWith("session:")) {
                    continue;
                }
                
                // 获取值并解析
                String jsonValue = RedisUtils.get(key, 0);
                System.out.println("检查键 " + key + ", 值: " + jsonValue);
                
                if (jsonValue != null) {
                    try {
                        JSONObject jsonObject = JSON.parseObject(jsonValue);
                        // 检查session是否匹配
                        if (!jsonObject.containsKey("session")) {
                            System.out.println("键 " + key + " 的值没有session字段");
                            continue;
                        }
                        
                        String storedSession = jsonObject.getString("session");
                        System.out.println("存储的session: " + storedSession + ", 要验证的sessionId: " + sessionId);
                        
                        if (sessionId.equals(storedSession)) {
                            // 找到匹配的session，检查验证码
                            if (!jsonObject.containsKey("verifyCode")) {
                                System.out.println("键 " + key + " 的值没有verifyCode字段");
                                return false;
                            }
                            
                            String storedVerifyCode = jsonObject.getString("verifyCode");
                            System.out.println("存储的验证码: " + storedVerifyCode + ", 提交的验证码: " + code);
                            
                            if (code.equals(storedVerifyCode)) {
                                // 验证成功，更新状态
                                jsonObject.put("is_verified", true);
                                RedisUtils.set(key, jsonObject.toJSONString(), 1000 * 60 * 60 * 24, 0);
                                
                                // 同时添加映射，方便下次查找
                                RedisUtils.set("session:" + sessionId, key, 1000 * 60 * 60 * 24, 0);
                                
                                return true;
                            }
                            // 验证码不匹配
                            return false;
                        }
                    } catch (JSONException e) {
                        System.err.println("解析JSON失败: " + e.getMessage() + ", JSON值: " + jsonValue);
                    }
                }
            }
            
            // 找不到对应的session
            System.out.println("未找到匹配的session");
            return false;
        } catch (Exception e) {
            System.err.println("验证码校验异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ValidationResult validateRegisterRequest(UserRegisterRequest request) {
        // 检查是否为空
        if (request == null) {
            return ValidationResult.fail("注册请求不能为空");
        }
        
        // 检查邮箱
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ValidationResult.fail("邮箱不能为空");
        }
        
        // 检查邮箱格式
        if (!request.getEmail().matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            return ValidationResult.fail("邮箱格式不正确");
        }
        
        // 检查密码
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ValidationResult.fail("密码不能为空");
        }
        
        // 检查密码长度
        if (request.getPassword().length() < 6) {
            return ValidationResult.fail("密码长度不能小于6位");
        }
        
        // 检查用户名
        if (request.getUserName() == null || request.getUserName().trim().isEmpty()) {
            return ValidationResult.fail("用户名不能为空");
        }
        
        // 验证用户是否已经存在
        User existingUser = getUserByEmail(request.getEmail());
        if (existingUser != null) {
            return ValidationResult.fail("该邮箱已被注册");
        }
        
        // 验证邮箱是否已通过验证
        String jsonValue = RedisUtils.get(request.getEmail(), 0);
        if (jsonValue == null) {
            return ValidationResult.fail("该邮箱未进行验证");
        }
        
        try {
            JSONObject jsonObject = JSON.parseObject(jsonValue);
            
            if (!jsonObject.containsKey("is_verified")) {
                return ValidationResult.fail("验证状态不存在");
            }
            
            boolean isVerified = jsonObject.getBooleanValue("is_verified");
            if (!isVerified) {
                return ValidationResult.fail("该邮箱未通过验证");
            }
        } catch (Exception e) {
            return ValidationResult.fail("验证状态检查失败: " + e.getMessage(), "500");
        }
        
        // 所有验证通过
        return ValidationResult.success();
    }

    @Override
    public boolean register(UserRegisterRequest request) {
        // 首先进行参数验证
        ValidationResult validationResult = validateRegisterRequest(request);
        if (!validationResult.isValid()) {
            System.out.println("注册验证失败: " + validationResult.getMessage());
            return false;
        }
        
        try {
            // 验证通过，可以注册
            User newUser = new User();
            newUser.setEmail(request.getEmail());
            // 对密码进行加密
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setPhone(request.getPhone());
            newUser.setUserName(request.getUserName());
            String uuid = UuidUtils.generateUuid();
            newUser.setUuid(uuid);
            
            // 设置注册时间
            newUser.setCreatedAt(java.time.LocalDateTime.now());
            newUser.setUpdatedAt(java.time.LocalDateTime.now());
            newUser.setIsDeleted(0); // 未删除
            
            // 保存用户信息
            userMapper.register(newUser);
            
            return true;
        } catch (Exception e) {
            System.err.println("注册失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        // TODO Auto-generated method stub
        User user = userMapper.getUserByEmail(email);
        if(user == null){
            return null;    
        }
        return user;
    }

    @Override
    public ValidationResult login(UserLoginRequest request) {
        try {
            // 参数校验
            if (request == null) {
                return ValidationResult.fail("登录请求不能为空");
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ValidationResult.fail("邮箱不能为空");
            }
            
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ValidationResult.fail("密码不能为空");
            }
            
            // 查询用户
            User user = getUserByEmail(request.getEmail());
            if (user == null) {
                return ValidationResult.fail("用户不存在");
            }
            
            // 校验密码
            boolean isPasswordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if (!isPasswordMatch) {
                return ValidationResult.fail("密码错误");
            }
            
            // 登录成功
            return ValidationResult.success();
        } catch (Exception e) {
            return ValidationResult.fail("登录失败: " + e.getMessage(), "500");
        }
    }
}

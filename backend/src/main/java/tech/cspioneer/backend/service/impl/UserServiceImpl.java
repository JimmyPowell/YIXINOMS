package tech.cspioneer.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import tech.cspioneer.backend.entity.User;
import tech.cspioneer.backend.entity.request.UserUpdateRequest;
import tech.cspioneer.backend.entity.response.UserInfoResponse;
import tech.cspioneer.backend.entity.response.ValidationResult;
import tech.cspioneer.backend.mapper.UserMapper;
import tech.cspioneer.backend.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByUuid(String uuid) {
        return userMapper.getUserByUuid(uuid);
    }
    
    @Override
    public ValidationResult getCurrentUserProfile() {
        try {
            // 从认证信息中获取用户UUID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ValidationResult.fail("用户未认证", "401");
            }
            
            String uuid = (String) authentication.getPrincipal();
            
            // 获取用户信息
            User user = getUserByUuid(uuid);
            
            System.out.println("user: " + user);

            if (user != null) {
                UserInfoResponse userInfoResponse = new UserInfoResponse();
                userInfoResponse.setUuid(user.getUuid());
                userInfoResponse.setUserName(user.getUserName());
                userInfoResponse.setEmail(user.getEmail());
                userInfoResponse.setPhone(user.getPhone());
                userInfoResponse.setAvatarUrl(user.getAvatarUrl());
                userInfoResponse.setGender(user.getGender());
                userInfoResponse.setAge(user.getAge());
               
                return ValidationResult.success(userInfoResponse);
            } else {
                return ValidationResult.fail("用户不存在", "404");
            }
        } catch (Exception e) {
            return ValidationResult.fail("获取用户信息失败: " + e.getMessage(), "500");
        }
    }

    @Override
    public ValidationResult updateUser(String uuid, User updatedUser) {
        try {
            // 获取当前用户信息
            User existingUser = userMapper.getUserByUuid(uuid);
            if (existingUser == null) {
                return ValidationResult.fail("用户不存在", "404");
            }

            // 更新用户信息，保留不变的字段
            if (updatedUser.getUserName() != null) {
                existingUser.setUserName(updatedUser.getUserName());
            }
            if (updatedUser.getPhone() != null) {
                existingUser.setPhone(updatedUser.getPhone());
            }
            if (updatedUser.getAvatarUrl() != null) {
                existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
            }
            if (updatedUser.getGender() != null) {
                existingUser.setGender(updatedUser.getGender());
            }
            if (updatedUser.getAge() != null) {
                existingUser.setAge(updatedUser.getAge());
            }

            // 更新修改时间
            existingUser.setUpdatedAt(LocalDateTime.now());

            // 执行更新
            boolean success = userMapper.updateUser(existingUser);
            if (success) {
                // 清除敏感信息
                existingUser.setPassword(null);
                existingUser.setId(null);
                return ValidationResult.success(existingUser);
            } else {
                return ValidationResult.fail("更新用户信息失败", "400");
            }
        } catch (Exception e) {
            return ValidationResult.fail("更新用户信息异常: " + e.getMessage(), "500");
        }
    }
    
    @Override
    public ValidationResult updateCurrentUserProfile(UserUpdateRequest request) {
        try {
            // 从认证信息中获取用户UUID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ValidationResult.fail("用户未认证", "401");
            }
            
            String uuid = (String) authentication.getPrincipal();
            
            // 检查权限 - 如果用户要更改用户名，需要检查是否有权限
            // if (request.getUserName() != null) {
            //     // 检查用户是否有ADMIN角色
            //     Collection<?> authorities = authentication.getAuthorities();
            //     boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
                
            //     if (!isAdmin) {
            //         return ValidationResult.fail("您没有权限修改用户名", "403");
            //     }
            // }
            
            // 构建更新对象
            User userToUpdate = new User();
            userToUpdate.setUserName(request.getUserName());
            userToUpdate.setPhone(request.getPhone());
            userToUpdate.setAvatarUrl(request.getAvatarUrl());
            userToUpdate.setGender(request.getGender());
            userToUpdate.setAge(request.getAge());
            
            // 执行更新
            return updateUser(uuid, userToUpdate);
        } catch (Exception e) {
            return ValidationResult.fail("更新用户信息异常: " + e.getMessage(), "500");
        }
    }

    @Override
    public ValidationResult deactivateUser(String uuid) {
        try {
            // 获取当前用户信息
            User existingUser = userMapper.getUserByUuid(uuid);
            if (existingUser == null) {
                return ValidationResult.fail("用户不存在", "404");
            }

            // 检查权限 - 只有管理员或者用户本人可以注销账户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserUuid = (String) authentication.getPrincipal();
            Collection<?> authorities = authentication.getAuthorities();
            boolean isAdmin = authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            
            if (!currentUserUuid.equals(uuid) && !isAdmin) {
                return ValidationResult.fail("您没有权限注销此用户", "403");
            }

            // 更新修改时间
            existingUser.setUpdatedAt(LocalDateTime.now());

            // 执行软删除
            boolean success = userMapper.deactivateUser(existingUser);
            if (success) {
                return ValidationResult.success();
            } else {
                return ValidationResult.fail("注销用户失败", "400");
            }
        } catch (Exception e) {
            return ValidationResult.fail("注销用户异常: " + e.getMessage(), "500");
        }
    }
    
    @Override
    public ValidationResult deactivateCurrentUser() {
        try {
            // 从认证信息中获取用户UUID
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return ValidationResult.fail("用户未认证", "401");
            }
            
            String uuid = (String) authentication.getPrincipal();
            
            // 执行注销
            return deactivateUser(uuid);
        } catch (Exception e) {
            return ValidationResult.fail("注销用户异常: " + e.getMessage(), "500");
        }
    }
} 
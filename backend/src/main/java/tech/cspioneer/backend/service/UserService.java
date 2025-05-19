package tech.cspioneer.backend.service;

import tech.cspioneer.backend.entity.User;
import tech.cspioneer.backend.entity.request.UserUpdateRequest;
import tech.cspioneer.backend.entity.response.ValidationResult;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 根据UUID获取用户信息
     * @param uuid 用户UUID
     * @return 用户信息
     */
    User getUserByUuid(String uuid);
    
    /**
     * 获取当前登录用户的信息
     * @return 处理过的用户信息和结果
     */
    ValidationResult getCurrentUserProfile();
    
    /**
     * 更新用户信息
     * @param uuid 用户UUID
     * @param user 更新的用户信息
     * @return 更新结果
     */
    ValidationResult updateUser(String uuid, User user);
    
    /**
     * 更新当前用户信息
     * @param request 更新请求
     * @return 更新结果
     */
    ValidationResult updateCurrentUserProfile(UserUpdateRequest request);
    
    /**
     * 注销用户（软删除）
     * @param uuid 用户UUID
     * @return 注销结果
     */
    ValidationResult deactivateUser(String uuid);
    
    /**
     * 注销当前用户
     * @return 注销结果
     */
    ValidationResult deactivateCurrentUser();
} 
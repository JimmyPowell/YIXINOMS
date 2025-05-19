package tech.cspioneer.backend.entity.response;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String uuid;
    private String userName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Integer gender;
    private Integer age;
    
}

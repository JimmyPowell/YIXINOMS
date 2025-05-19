package tech.cspioneer.backend.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import tech.cspioneer.backend.entity.User;
@Mapper
public interface UserMapper {
    /**用户注册 */

    @Insert("INSERT INTO users (email, password, user_name, phone,uuid) VALUES (#{email}, #{password}, #{userName}, #{phone},#{uuid})")
    boolean register(User user);
    /**根据邮箱查询用户 */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User getUserByEmail(String email);
}
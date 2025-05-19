package tech.cspioneer.backend.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tech.cspioneer.backend.entity.User;
@Mapper
public interface UserMapper {
    /**用户注册 */
    @Insert("INSERT INTO users (email, password, user_name, phone, uuid, avatar_url, gender, age, created_at, updated_at, is_deleted) " +
            "VALUES (#{email}, #{password}, #{userName}, #{phone}, #{uuid}, #{avatarUrl}, #{gender}, #{age}, #{createdAt}, #{updatedAt}, #{isDeleted})")
    boolean register(User user);
    /**根据邮箱查询用户 */
    @Select("SELECT * FROM users WHERE email = #{email}")
    User getUserByEmail(String email);
    /**根据UUID查询用户 */
    @Select("SELECT * FROM users WHERE uuid = #{uuid} AND is_deleted = 0")
    User getUserByUuid(String uuid);
    /**更新用户信息 */
    @Update("UPDATE users SET user_name = #{userName}, phone = #{phone}, avatar_url = #{avatarUrl}, " +
            "gender = #{gender}, age = #{age}, updated_at = #{updatedAt} WHERE uuid = #{uuid}")
    boolean updateUser(User user);
    /**注销用户（软删除） */
    @Update("UPDATE users SET is_deleted = 1, updated_at = #{updatedAt} WHERE uuid = #{uuid}")
    boolean deactivateUser(User user);
}
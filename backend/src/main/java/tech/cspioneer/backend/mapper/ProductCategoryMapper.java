package tech.cspioneer.backend.mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tech.cspioneer.backend.entity.ProductCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    /**
     * 查询所有商品分类
     * @return 商品分类列表
     */
    @Select("SELECT * FROM product_category")
    List<ProductCategory> selectAllProductCategories();
    
    /**
     * 根据ID查询商品分类
     * @param id 商品分类ID
     * @return 商品分类
     */
    @Select("SELECT * FROM product_category WHERE id = #{id}")
    ProductCategory selectProductCategoryById(Long id);
    
    /**添加商品分类 */
    @Insert("INSERT INTO product_category (category_name, parent_id) VALUES (#{categoryName}, #{parentId})")
    void insertProductCategory(ProductCategory productCategory);
    
    /**更新商品分类 */
    @Update("UPDATE product_category SET category_name = #{categoryName}, parent_id = #{parentId} WHERE id = #{id}")
    void updateProductCategory(ProductCategory productCategory);
    
    
}
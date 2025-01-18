package com.scm.mapper;

import com.scm.entity.ProductPrice;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductPriceMapper {
    
    @Select("SELECT * FROM product_price WHERE id = #{id}")
    ProductPrice getById(Long id);
    
    @Select("<script>" +
            "SELECT pp.*, p.code as productCode, p.name as productName, " +
            "p.spec, p.unit, pc.name as categoryName " +
            "FROM product_price pp " +
            "LEFT JOIN product p ON pp.product_id = p.id " +
            "LEFT JOIN product_category pc ON p.category_id = pc.id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'>" +
            "AND p.name LIKE CONCAT('%', #{productName}, '%')" +
            "</if> " +
            "<if test='categoryId != null'>AND p.category_id = #{categoryId}</if> " +
            "ORDER BY pp.update_time DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<ProductPrice> search(
        @Param("productName") String productName,
        @Param("categoryId") Long categoryId,
        @Param("offset") int offset,
        @Param("pageSize") int pageSize
    );
    
    @Select("<script>" +
            "SELECT COUNT(*) " +
            "FROM product_price pp " +
            "LEFT JOIN product p ON pp.product_id = p.id " +
            "LEFT JOIN product_category pc ON p.category_id = pc.id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'>" +
            "AND p.name LIKE CONCAT('%', #{productName}, '%')" +
            "</if> " +
            "<if test='categoryId != null'>AND p.category_id = #{categoryId}</if> " +
            "</script>")
    long count(
        @Param("productName") String productName,
        @Param("categoryId") Long categoryId
    );
    
    @Insert("INSERT INTO product_price(product_id, retail_price, wholesale_price, member_price, " +
            "remark, create_time, update_time, create_by, update_by) " +
            "VALUES(#{productId}, #{retailPrice}, #{wholesalePrice}, #{memberPrice}, " +
            "#{remark}, NOW(), NOW(), #{createBy}, #{updateBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPrice price);
    
    @Update("UPDATE product_price SET retail_price = #{retailPrice}, " +
            "wholesale_price = #{wholesalePrice}, member_price = #{memberPrice}, " +
            "remark = #{remark}, update_time = NOW(), update_by = #{updateBy} " +
            "WHERE id = #{id}")
    int update(ProductPrice price);
    
    @Select("SELECT * FROM product_price WHERE product_id = #{productId} " +
            "ORDER BY create_time DESC")
    List<ProductPrice> getHistoryByProductId(Long productId);
    
    @Select("SELECT * FROM product_price WHERE product_id = #{productId} " +
            "ORDER BY create_time DESC LIMIT 1")
    ProductPrice getLatestByProductId(Long productId);
} 
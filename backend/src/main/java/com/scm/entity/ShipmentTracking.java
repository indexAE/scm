package com.scm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_shipment_tracking")
public class ShipmentTracking {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发货单ID
     */
    private Long shipmentId;
    
    /**
     * 物流位置
     */
    private String location;
    
    /**
     * 物流描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
} 
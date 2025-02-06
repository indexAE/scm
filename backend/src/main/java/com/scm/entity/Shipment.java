package com.scm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_shipment")
public class Shipment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 收货人
     */
    private String consignee;
    
    /**
     * 收货人电话
     */
    private String consigneePhone;
    
    /**
     * 收货地址
     */
    private String consigneeAddress;
    
    /**
     * 发货人
     */
    private String shipper;
    
    /**
     * 发货人电话
     */
    private String shipperPhone;
    
    /**
     * 发货地址
     */
    private String shipperAddress;
    
    /**
     * 状态：pending-待发货，shipped-已发货，delivered-已送达，cancelled-已取消
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
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
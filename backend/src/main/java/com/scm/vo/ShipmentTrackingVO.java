package com.scm.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShipmentTrackingVO {
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 状态
     */
    private String status;

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
     * 当前位置
     */
    private String location;

    /**
     * 物流描述
     */
    private String description;

    /**
     * 发货时间
     */
    private LocalDateTime createTime;

    /**
     * 跟踪时间
     */
    private LocalDateTime trackingTime;
} 
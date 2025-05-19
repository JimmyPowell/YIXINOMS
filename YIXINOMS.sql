-- 用户表
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
  `uuid` VARCHAR(36) NOT NULL COMMENT '用户唯一标识符',
  `user_name` VARCHAR(50) NULL UNIQUE COMMENT '用户名（可为空，用于账号密码登录）',
  `email` VARCHAR(100) NULL UNIQUE COMMENT '邮箱（可为空，用于账号密码登录）',
  `phone` VARCHAR(20) NULL UNIQUE COMMENT '手机号（绑定后必填）',
  `password` VARCHAR(255) NULL COMMENT '加密后的密码（账号密码登录时使用）',
  `avatar_url` VARCHAR(255) NULL COMMENT '用户头像URL',
  `gender` TINYINT DEFAULT 0 NULL COMMENT '性别（0-未知 1-男 2-女）',
  `age` INT NULL COMMENT '用户年龄',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '注册时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '最后更新时间',
  `is_deleted` TINYINT DEFAULT 0 NULL COMMENT '是否删除（0-未删除 1-已删除）'
);

-- 第三方登录关联表
CREATE TABLE `user_third_party` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT COMMENT '关联用户ID',
  `platform` VARCHAR(20) COMMENT '平台类型（wechat, github等）',
  `open_id` VARCHAR(100) COMMENT '第三方平台用户唯一标识',
  `access_token` VARCHAR(255) COMMENT '第三方平台访问令牌（可选）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_third_party_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

-- 角色表
CREATE TABLE `roles` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `role_name` VARCHAR(50) UNIQUE COMMENT '角色名称（如admin, customer, merchant）',
  `description` VARCHAR(255) COMMENT '角色描述'
);

-- 用户角色关联表
CREATE TABLE `user_roles` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT COMMENT '用户ID',
  `role_id` BIGINT COMMENT '角色ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` BIGINT COMMENT '创建人',
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
);

-- 权限表
CREATE TABLE `permissions` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `permission_name` VARCHAR(50) UNIQUE COMMENT '权限名称（如order_create, inventory_edit）',
  `description` VARCHAR(255)
);

-- 角色权限关联表
CREATE TABLE `role_permissions` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
);

-- 订单主表
CREATE TABLE `orders` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT COMMENT '下单用户ID',
  `order_number` VARCHAR(50) UNIQUE COMMENT '订单编号（自动生成）',
  `total_amount` DECIMAL(10,2) COMMENT '订单总金额',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '订单状态（pending, paid, shipped, completed, canceled）',
  `payment_method` VARCHAR(50) COMMENT '支付方式（wechat, alipay, bank_transfer）',
  `shipping_address` VARCHAR(255) COMMENT '收货地址',
  `shipping_method` VARCHAR(50) COMMENT '配送方式（express, pickup_in_store）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

-- 订单明细表
CREATE TABLE `order_items` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT COMMENT '关联订单ID',
  `product_id` BIGINT COMMENT '商品ID',
  `product_name` VARCHAR(100) COMMENT '商品名称（冗余字段）',
  `quantity` INT COMMENT '购买数量',
  `price` DECIMAL(10,2) COMMENT '单价',
  `subtotal` DECIMAL(10,2) COMMENT '小计金额',
  CONSTRAINT `fk_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_item_product` FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE SET NULL
);

-- 订单状态历史表
CREATE TABLE `order_status_history` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT,
  `old_status` VARCHAR(20),
  `new_status` VARCHAR(20),
  `changed_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `operator` VARCHAR(50) COMMENT '操作人（用户或系统）',
  CONSTRAINT `fk_order_history_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
);

-- 订单评价表
CREATE TABLE `order_reviews` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT,
  `user_id` BIGINT,
  `rating` INT CHECK (rating BETWEEN 1 AND 5) COMMENT '评分（1-5）',
  `comment` TEXT COMMENT '评价内容',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

-- 商品分类表
CREATE TABLE `category` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL COMMENT '分类名称（如：电子产品、手机、智能手机）',
    `parent_id` BIGINT COMMENT '父分类ID，0表示根分类',
    `is_leaf` BOOLEAN DEFAULT FALSE COMMENT '是否为叶子节点（用于分页控制）',
    `level` INT DEFAULT 0 COMMENT '层级深度（可选）',
    `path` VARCHAR(255) COMMENT '路径信息（可选）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME ON UPDATE CURRENT_TIMESTAMP,
    `status` TINYINT DEFAULT 1 COMMENT '分类状态（1:启用，0:禁用）',
    `description` TEXT COMMENT '分类描述（可选）',
    CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
);

-- 商品表
CREATE TABLE `products` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `uuid` VARCHAR(36) NULL COMMENT '商品唯一标识符',
  `cover_image_url` VARCHAR(255) NULL COMMENT '商品封面图片对象存储URL',
  `description_mongo_id` VARCHAR(24) NULL COMMENT '商品详细描述的MongoDB文档ID',
  `category_id` BIGINT NULL,
  `name` VARCHAR(200) NULL,
  `description` TEXT NULL,
  `price` DECIMAL(10,2) NULL,
  `status` VARCHAR(20) DEFAULT 'on_sale' NULL COMMENT '商品状态（on_sale, off_sale, out_of_stock）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
);

CREATE INDEX `idx_product_category` ON `products` (`category_id`);
CREATE INDEX `idx_product_uuid` ON `products` (`uuid`);

-- 商品库存表
CREATE TABLE `inventory` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_id` BIGINT UNIQUE COMMENT '商品ID',
  `product_name` VARCHAR(100) COMMENT '商品名称（冗余字段）',
  `available_stock` INT DEFAULT 0 COMMENT '可用库存',
  `locked_stock` INT DEFAULT 0 COMMENT '锁定库存（已下单但未发货）',
  `low_stock_threshold` INT DEFAULT 10 COMMENT '库存预警阈值',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_inventory_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
);

-- 库存变动记录表
CREATE TABLE `inventory_logs` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `product_id` BIGINT,
  `change_type` VARCHAR(20) COMMENT '变动类型（inbound, outbound, cancel）',
  `quantity` INT COMMENT '变动数量',
  `related_id` BIGINT COMMENT '关联ID（如订单ID或采购单ID）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_inventory_log_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
);

-- 物流商表
CREATE TABLE `logistics_providers` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) UNIQUE COMMENT '物流商名称（如顺丰、菜鸟）',
  `api_config` TEXT COMMENT 'API对接配置（JSON格式）'
);

-- 物流信息表
CREATE TABLE `logistics_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT UNIQUE COMMENT '关联订单ID',
  `logistics_number` VARCHAR(50) COMMENT '物流单号',
  `provider_id` BIGINT COMMENT '物流商ID',
  `status` VARCHAR(20) DEFAULT 'created' COMMENT '物流状态（created, shipped, in_transit, delivered）',
  `current_location` VARCHAR(255) COMMENT '当前位置',
  `estimated_delivery` DATETIME COMMENT '预计送达时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_logistics_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_logistics_provider` FOREIGN KEY (`provider_id`) REFERENCES `logistics_providers`(`id`) ON DELETE SET NULL
);

-- 物流轨迹表
CREATE TABLE `logistics_tracks` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `logistics_id` BIGINT,
  `status` VARCHAR(50) COMMENT '轨迹状态（如已揽件、运输中）',
  `location` VARCHAR(255) COMMENT '位置信息',
  `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_logistics_track_info` FOREIGN KEY (`logistics_id`) REFERENCES `logistics_info`(`id`) ON DELETE CASCADE
);

-- 支付记录表
CREATE TABLE `payments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT UNIQUE COMMENT '关联订单ID',
  `user_id` BIGINT COMMENT '支付用户ID',
  `amount` DECIMAL(10,2) COMMENT '支付金额',
  `payment_method` VARCHAR(50) COMMENT '支付方式（wechat, alipay, bank）',
  `transaction_id` VARCHAR(100) COMMENT '第三方交易ID',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '支付状态（pending, success, failed, refunded）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
);

-- 退款记录表
CREATE TABLE `refunds` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT,
  `payment_id` BIGINT,
  `refund_amount` DECIMAL(10,2) COMMENT '退款金额',
  `reason` TEXT COMMENT '退款原因',
  `status` VARCHAR(20) DEFAULT 'processing' COMMENT '退款状态（processing, success, failed）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_refund_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_refund_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments`(`id`) ON DELETE CASCADE
);

-- 售后申请表
CREATE TABLE `after_sales_applications` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_id` BIGINT UNIQUE,
  `user_id` BIGINT,
  `type` VARCHAR(20) COMMENT '类型（return, exchange）',
  `reason` TEXT COMMENT '申请原因',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '处理状态（pending, approved, rejected, completed）',
  `logistics_number` VARCHAR(50) COMMENT '退货物流单号',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_aftersale_order` FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_aftersale_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
);

-- 售后处理记录表
CREATE TABLE `after_sales_logs` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `application_id` BIGINT,
  `operator` VARCHAR(50) COMMENT '操作人（客服或系统）',
  `action` VARCHAR(50) COMMENT '操作类型（approve, reject, refund_processed）',
  `note` TEXT COMMENT '操作备注',
  `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_aftersale_log_app` FOREIGN KEY (`application_id`) REFERENCES `after_sales_applications`(`id`) ON DELETE CASCADE
);

-- 优惠券表
CREATE TABLE `coupons` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `code` VARCHAR(50) UNIQUE COMMENT '优惠券编码',
  `discount_type` VARCHAR(20) COMMENT '折扣类型（fixed, percentage）',
  `discount_value` DECIMAL(10,2) COMMENT '折扣值（如10元或8折）',
  `valid_from` DATETIME,
  `valid_to` DATETIME,
  `usage_limit` INT DEFAULT 1 COMMENT '使用限制（每人限用次数）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户优惠券领取记录表
CREATE TABLE `user_coupons` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT,
  `coupon_id` BIGINT,
  `used` TINYINT DEFAULT 0 COMMENT '是否已使用（0-未使用 1-已使用）',
  `used_at` DATETIME NULL DEFAULT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_user_coupon_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_coupon_coupon` FOREIGN KEY (`coupon_id`) REFERENCES `coupons`(`id`) ON DELETE CASCADE
);

-- 系统配置表（如订单编号生成规则、物流API密钥等）
CREATE TABLE `system_configs` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `config_key` VARCHAR(100) UNIQUE,
  `config_value` TEXT,
  `description` VARCHAR(255),
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 操作日志表
CREATE TABLE `operation_logs` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT,
  `operation_type` VARCHAR(50) COMMENT '操作类型（order_edit, payment_refund）',
  `description` TEXT,
  `ip_address` VARCHAR(45) COMMENT '操作IP',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_operation_log_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
);

-- 销售统计视图（示例）
CREATE VIEW `sales_summary` AS
SELECT 
  DATE_FORMAT(created_at, '%Y-%m') AS month,
  COUNT(*) AS total_orders,
  SUM(total_amount) AS total_sales
FROM `orders`
WHERE status = 'completed'
GROUP BY DATE_FORMAT(created_at, '%Y-%m');

-- 库存预警查询（示例）
-- SELECT * FROM `inventory`
-- WHERE available_stock <= low_stock_threshold;

-- 订单表索引
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);

-- 支付表索引
CREATE INDEX idx_payments_user_id ON payments(user_id);
CREATE INDEX idx_payments_status ON payments(status);

-- 库存表索引
CREATE INDEX idx_inventory_product_id ON inventory(product_id);

# YIXINOMS
开源的OMS系统，基于Spring Boot+Vue 使用了微服务

## 系统功能概述

YIXINOMS是一个完整的订单管理系统，主要功能包括：

1. **用户注册/登录**
   - 账号+密码/第三方登录
   - 权限分配/注册
   - 身份认证通道

2. **订单创建**
   - API接口创建
   - 手动创建

3. **订单管理**
   - 订单查询、部分修改、取消、订单状态、期间正向订单
   - 搜索、筛选、排序
   - 订单详情显示：物流状态、数量、价格、发送信息等
   - 订单状态跟踪
   - 订单编辑与修改
   - 订单导出
   - 订单批量操作
   - 订单评价

4. **报表分析**
   - 统计销售额
   - 订单状态
   - 库存
   - 物流
   - 其他

5. **库存管理**
   - 库存查询/库存水平提醒
   - 软发出货/批量/批次库存管理
   - 发货输出

6. **物流与配送管理**
   - 运单创建
   - 取件选择
   - 运单跟踪
   - 包装选择

7. **支付管理**

8. **退换货管理**
   - 审批
   - 退款处理
   - 运费
   - 报表

## 待办事项(TODO)

- [x] 搭建基础项目架构
- [ ] 用户认证与授权模块开发
  - [ ] 实现账号密码登录
  - [ ] 第三方登录集成
  - [ ] 权限管理系统
- [ ] 订单管理核心功能
  - [ ] 订单创建API
  - [ ] 订单CRUD基础操作
  - [ ] 订单状态流转
  - [ ] 订单查询与筛选
- [ ] 报表分析功能
  - [ ] 销售数据统计
  - [ ] 订单状态分析
- [ ] 库存管理系统
  - [ ] 库存查询
  - [ ] 库存水平监控
- [ ] 物流配送集成
  - [ ] 运单生成
  - [ ] 物流商对接
- [ ] 支付系统集成
- [ ] 退换货流程
- [ ] 前端界面开发
  - [ ] 管理后台
  - [ ] 移动端适配
- [ ] 系统测试
- [ ] 部署与上线

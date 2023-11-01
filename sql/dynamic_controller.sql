CREATE TABLE interface_group
(
    id          BIGINT AUTO_INCREMENT COMMENT '主键'
        PRIMARY KEY,
    name        VARCHAR(255)                       NOT NULL COMMENT '名称',
    code        VARCHAR(255)                       NOT NULL COMMENT 'url',
    icon        VARCHAR(255)                       NULL COMMENT '图标',
    description VARCHAR(255)                       NULL COMMENT '描述',
    parent_id   BIGINT                             NULL COMMENT '上级节点id',
    create_by   VARCHAR(255)                       NULL COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_by   VARCHAR(255)                       NULL COMMENT '更新人',
    update_time DATETIME                           NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '接口分组';

CREATE TABLE interface_item
(
    id          BIGINT AUTO_INCREMENT COMMENT '主键'
        PRIMARY KEY,
    name        VARCHAR(255)  NOT NULL COMMENT '名称',
    code        VARCHAR(255)  NOT NULL COMMENT '接口名',
    type        TINYINT       NOT NULL COMMENT '类型，1-sql，2-js',
    description TEXT          NULL COMMENT '描述',
    method      VARCHAR(255)  NOT NULL DEFAULT 'GET' COMMENT '请求方式,GET/POST',
    group_id    BIGINT        NULL COMMENT '分组',
    group_code  VARCHAR(255)  NULL COMMENT '分组',
    datasource  VARCHAR(255)  NULL COMMENT '数据源',
    is_enable   TINYINT       NOT NULL DEFAULT 1 COMMENT '是否启用',
    is_log      TINYINT       NOT NULL DEFAULT 0 COMMENT '是否记录调用日志',
    permission  VARCHAR(255)  NULL COMMENT '权限标识,*表示免登录',
    param       VARCHAR(1000) NULL COMMENT '输入参数',
    program     TEXT          NULL COMMENT '代码内容',
    create_by   VARCHAR(255)  NULL COMMENT '创建人',
    create_time DATETIME      NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by   VARCHAR(255)  NULL COMMENT '更新人',
    update_time DATETIME      NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '接口项目';

-- 菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES ('动态接口', 3, 4, 'dyninterface', 'system/interface', null, 1, 0, 'C', '0', '0', '', 'component', 'admin', sysdate(), 'admin', sysdate(), '');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口分组查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:interfacegroup:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口分组新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:interfacegroup:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口分组修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:interfacegroup:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口分组删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:interfacegroup:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口分组导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:interfacegroup:export',       '#', 'admin', sysdate(), '', null, '');

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口项目查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:interfaceitem:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口项目新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:interfaceitem:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口项目修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:interfaceitem:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口项目删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:interfaceitem:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('接口项目导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:interfaceitem:export',       '#', 'admin', sysdate(), '', null, '');
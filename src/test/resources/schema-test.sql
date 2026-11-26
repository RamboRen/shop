DROP TABLE IF EXISTS shop_order_item;
DROP TABLE IF EXISTS shop_order;
DROP TABLE IF EXISTS shop_user;
DROP TABLE IF EXISTS sys_setting;
DROP TABLE IF EXISTS shop_coupon;
DROP TABLE IF EXISTS shop_product_spec;
DROP TABLE IF EXISTS shop_gallery_image;
DROP TABLE IF EXISTS shop_gallery_category;
DROP TABLE IF EXISTS shop_product;
DROP TABLE IF EXISTS shop_product_category;
DROP TABLE IF EXISTS sys_role_menu;
DROP TABLE IF EXISTS sys_menu;
DROP TABLE IF EXISTS sys_admin;

CREATE TABLE sys_admin
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    username        VARCHAR(20)  NOT NULL,
    password        VARCHAR(100) NOT NULL,
    nickname        VARCHAR(30)  NOT NULL,
    avatar          VARCHAR(255) DEFAULT NULL,
    phone           VARCHAR(20)  DEFAULT NULL,
    email           VARCHAR(100) DEFAULT NULL,
    role_code       VARCHAR(20)  NOT NULL,
    status          TINYINT      NOT NULL DEFAULT 1,
    last_login_time DATETIME     DEFAULT NULL,
    remark          VARCHAR(200) DEFAULT NULL,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_admin_username UNIQUE (username)
);

CREATE TABLE sys_menu
(
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    parent_id       BIGINT       NOT NULL DEFAULT 0,
    menu_name       VARCHAR(50)  NOT NULL,
    menu_type       VARCHAR(20)  NOT NULL,
    route_path      VARCHAR(100) NOT NULL,
    component       VARCHAR(150) DEFAULT NULL,
    icon            VARCHAR(50)  DEFAULT NULL,
    permission_code VARCHAR(100) DEFAULT NULL,
    sort            INT          NOT NULL DEFAULT 0,
    visible         TINYINT      NOT NULL DEFAULT 1,
    status          TINYINT      NOT NULL DEFAULT 1,
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_menu_route_path UNIQUE (route_path),
    CONSTRAINT uk_sys_menu_permission_code UNIQUE (permission_code)
);

CREATE TABLE sys_role_menu
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    role_code   VARCHAR(20) NOT NULL,
    menu_id     BIGINT      NOT NULL,
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_role_menu_role_menu UNIQUE (role_code, menu_id)
);

CREATE TABLE shop_product_spec
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    spec_values TEXT         NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 1,
    sort        INT          NOT NULL DEFAULT 0,
    remark      VARCHAR(200) DEFAULT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_shop_product_spec_name UNIQUE (name)
);

CREATE TABLE shop_coupon
(
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    coupon_code      VARCHAR(32)    NOT NULL,
    name             VARCHAR(100)   NOT NULL,
    coupon_type      VARCHAR(20)    NOT NULL,
    discount_value   DECIMAL(10, 2) NOT NULL,
    threshold_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_count      INT            NOT NULL DEFAULT 0,
    received_count   INT            NOT NULL DEFAULT 0,
    used_count       INT            NOT NULL DEFAULT 0,
    valid_start_time DATETIME       NOT NULL,
    valid_end_time   DATETIME       NOT NULL,
    status           TINYINT        NOT NULL DEFAULT 1,
    description      VARCHAR(500)   DEFAULT NULL,
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_shop_coupon_code UNIQUE (coupon_code)
);

CREATE TABLE shop_product_category
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(30) NOT NULL,
    sort        INT         NOT NULL DEFAULT 0,
    status      TINYINT     NOT NULL DEFAULT 1,
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_product_category_name UNIQUE (name)
);

CREATE TABLE shop_product
(
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    product_code   VARCHAR(32)    NOT NULL,
    category_id    BIGINT         NOT NULL,
    name           VARCHAR(100)   NOT NULL,
    subtitle       VARCHAR(150)   DEFAULT NULL,
    main_image     VARCHAR(255)   NOT NULL,
    image_urls     TEXT           DEFAULT NULL,
    price          DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2) DEFAULT NULL,
    stock          INT            NOT NULL DEFAULT 0,
    sales_volume   BIGINT         NOT NULL DEFAULT 0,
    status         TINYINT        NOT NULL DEFAULT 0,
    sort           INT            NOT NULL DEFAULT 0,
    detail         TEXT           DEFAULT NULL,
    create_time    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted        TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_shop_product_code UNIQUE (product_code)
);

CREATE TABLE shop_gallery_category
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    category_code VARCHAR(30)  NOT NULL,
    category_name VARCHAR(30)  NOT NULL,
    sort          INT          NOT NULL DEFAULT 0,
    status        TINYINT      NOT NULL DEFAULT 1,
    remark        VARCHAR(200) DEFAULT NULL,
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_gallery_category_code UNIQUE (category_code)
);

CREATE TABLE shop_gallery_image
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    file_name     VARCHAR(100) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    url           VARCHAR(500) NOT NULL,
    relative_path VARCHAR(255) NOT NULL,
    content_type  VARCHAR(50)  NOT NULL,
    file_size     BIGINT       NOT NULL DEFAULT 0,
    width         INT          NOT NULL DEFAULT 0,
    height        INT          NOT NULL DEFAULT 0,
    image_group   VARCHAR(20)  NOT NULL DEFAULT 'OTHER',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE shop_user
(
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    user_no          VARCHAR(32)  NOT NULL,
    username         VARCHAR(30)  NOT NULL,
    nickname         VARCHAR(50)  NOT NULL,
    phone            VARCHAR(20)  DEFAULT NULL,
    email            VARCHAR(100) DEFAULT NULL,
    avatar           VARCHAR(255) DEFAULT NULL,
    gender           VARCHAR(10)  NOT NULL DEFAULT 'UNKNOWN',
    birthday         DATE         DEFAULT NULL,
    status           TINYINT      NOT NULL DEFAULT 1,
    last_login_time  DATETIME     DEFAULT NULL,
    remark           VARCHAR(200) DEFAULT NULL,
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_shop_user_no UNIQUE (user_no),
    CONSTRAINT uk_shop_user_username UNIQUE (username),
    CONSTRAINT uk_shop_user_phone UNIQUE (phone)
);

CREATE TABLE sys_setting
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    config_key   VARCHAR(64)  NOT NULL,
    config_value TEXT         DEFAULT NULL,
    config_group VARCHAR(20)  NOT NULL,
    config_name  VARCHAR(100) NOT NULL,
    value_type   VARCHAR(20)  NOT NULL DEFAULT 'STRING',
    sort         INT          NOT NULL DEFAULT 0,
    remark       VARCHAR(255) DEFAULT NULL,
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_sys_setting_key UNIQUE (config_key)
);

CREATE TABLE shop_order
(
    id               BIGINT         NOT NULL AUTO_INCREMENT,
    order_no         VARCHAR(32)    NOT NULL,
    user_id          BIGINT         DEFAULT NULL,
    receiver_name    VARCHAR(50)    NOT NULL,
    receiver_phone   VARCHAR(20)    NOT NULL,
    receiver_address VARCHAR(200)   NOT NULL,
    total_amount     DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    freight_amount   DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    discount_amount  DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    pay_amount       DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    pay_type         VARCHAR(20)    DEFAULT NULL,
    status           TINYINT        NOT NULL DEFAULT 0,
    item_count       INT            NOT NULL DEFAULT 0,
    total_quantity   INT            NOT NULL DEFAULT 0,
    pay_time         DATETIME       DEFAULT NULL,
    ship_time        DATETIME       DEFAULT NULL,
    finish_time      DATETIME       DEFAULT NULL,
    cancel_time      DATETIME       DEFAULT NULL,
    remark           VARCHAR(500)   DEFAULT NULL,
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT uk_shop_order_no UNIQUE (order_no)
);

CREATE TABLE shop_order_item
(
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    order_id      BIGINT         NOT NULL,
    product_id    BIGINT         DEFAULT NULL,
    product_name  VARCHAR(100)   NOT NULL,
    product_image VARCHAR(500)   DEFAULT NULL,
    price         DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    quantity      INT            NOT NULL DEFAULT 1,
    total_amount  DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    create_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

package com.shop.admin.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增菜单请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "新增菜单请求")
public class MenuCreateRequest {

    @Schema(description = "父菜单编号，0 表示顶级菜单", example = "0")
    @NotNull(message = "父菜单编号不能为空")
    @Min(value = 0, message = "父菜单编号不能小于 0")
    private Long parentId = 0L;

    @Schema(description = "菜单名称", example = "商品列表")
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过 50")
    private String menuName;

    @Schema(description = "菜单类型：CATALOG 目录，MENU 菜单", example = "MENU")
    @NotBlank(message = "菜单类型不能为空")
    @Pattern(regexp = "CATALOG|MENU", message = "菜单类型只能是 CATALOG 或 MENU")
    private String menuType;

    @Schema(description = "前端路由地址", example = "/product/list")
    @NotBlank(message = "路由地址不能为空")
    @Size(max = 100, message = "路由地址长度不能超过 100")
    private String routePath;

    @Schema(description = "前端组件路径", example = "product/list/index")
    @Size(max = 150, message = "组件路径长度不能超过 150")
    private String component;

    @Schema(description = "菜单图标", example = "Goods")
    @Size(max = 50, message = "图标长度不能超过 50")
    private String icon;

    @Schema(description = "接口权限编码，菜单类型必填", example = "product:view")
    @Size(max = 100, message = "权限编码长度不能超过 100")
    @Pattern(regexp = "^$|^[a-z][a-z0-9-]*:[a-z][a-z0-9-]*$",
            message = "权限编码格式应为 resource:action")
    private String permissionCode;

    @Schema(description = "排序值，越小越靠前", example = "1")
    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值不能小于 0")
    @Max(value = 9999, message = "排序值不能大于 9999")
    private Integer sort = 0;

    @Schema(description = "是否显示：0 隐藏，1 显示", example = "1")
    @NotNull(message = "显示状态不能为空")
    @Min(value = 0, message = "显示状态只能是 0 或 1")
    @Max(value = 1, message = "显示状态只能是 0 或 1")
    private Integer visible = 1;

    @Schema(description = "状态：0 禁用，1 启用", example = "1")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    private Integer status = 1;
}

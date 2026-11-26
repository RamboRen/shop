package com.shop.admin.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色菜单分配请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "角色菜单分配请求")
public class RoleMenuAssignRequest {

    @Schema(description = "菜单编号集合，空集合表示不分配任何菜单")
    @NotNull(message = "菜单编号集合不能为空")
    @Size(max = 200, message = "单次最多分配 200 个菜单")
    private List<@NotNull(message = "菜单编号不能为空")
            @Positive(message = "菜单编号必须大于 0") Long> menuIds;
}

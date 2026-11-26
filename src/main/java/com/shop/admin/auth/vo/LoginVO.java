package com.shop.admin.auth.vo;

import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.menu.vo.MenuVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

/**
 * 管理员登录响应。
 *
 * @author shop-admin
 */
@Getter
@Schema(description = "管理员登录响应")
public class LoginVO {

    @Schema(description = "JWT 访问令牌")
    private final String token;

    @Schema(description = "令牌类型", example = "Bearer")
    private final String tokenType;

    @Schema(description = "令牌有效期，单位秒", example = "7200")
    private final long expiresIn;

    @Schema(description = "当前管理员信息")
    private final AdminVO admin;

    @Schema(description = "当前角色可显示的菜单树")
    private final List<MenuVO> menus;

    @Schema(description = "当前角色拥有的接口权限编码")
    private final List<String> permissions;

    /**
     * 构造登录响应。
     *
     * @param token       访问令牌
     * @param tokenType   令牌类型
     * @param expiresIn   有效期秒数
     * @param admin       管理员信息
     * @param menus       菜单树
     * @param permissions 接口权限编码
     */
    public LoginVO(String token, String tokenType, long expiresIn, AdminVO admin,
                   List<MenuVO> menus, List<String> permissions) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.admin = admin;
        this.menus = menus;
        this.permissions = permissions;
    }
}

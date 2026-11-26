package com.shop.admin.security;

import com.shop.admin.admin.entity.AdminDO;
import com.shop.admin.common.constant.SecurityConstants;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spring Security 管理员认证主体。
 *
 * @author shop-admin
 */
@Getter
public class AdminPrincipal implements UserDetails {

    /**
     * 管理员编号。
     */
    private final Long adminId;

    /**
     * 登录用户名。
     */
    private final String username;

    /**
     * BCrypt 加密后的密码。
     */
    private final String password;

    /**
     * 昵称。
     */
    private final String nickname;

    /**
     * 角色编码。
     */
    private final String roleCode;

    /**
     * 是否启用。
     */
    private final boolean enabled;

    /**
     * 接口权限编码集合，由角色菜单实时计算。
     */
    private final List<String> permissions;

    public AdminPrincipal(Long adminId, String username, String password, String nickname,
                          String roleCode, boolean enabled, List<String> permissions) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roleCode = roleCode;
        this.enabled = enabled;
        this.permissions = permissions == null ? List.of() : List.copyOf(permissions);
    }

    /**
     * 根据管理员数据对象与权限编码构建认证主体。
     *
     * @param adminDO     管理员数据对象
     * @param permissions 接口权限编码集合
     * @return 认证主体
     */
    public static AdminPrincipal from(AdminDO adminDO, List<String> permissions) {
        boolean accountEnabled = Integer.valueOf(1).equals(adminDO.getStatus());
        return new AdminPrincipal(adminDO.getId(), adminDO.getUsername(), adminDO.getPassword(),
                adminDO.getNickname(), adminDO.getRoleCode(), accountEnabled, permissions);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>(permissions.size() + 1);
        authorities.add(new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + roleCode));
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}

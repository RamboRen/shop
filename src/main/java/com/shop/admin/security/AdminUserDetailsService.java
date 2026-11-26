package com.shop.admin.security;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shop.admin.admin.entity.AdminDO;
import com.shop.admin.admin.mapper.AdminMapper;
import com.shop.admin.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 管理员用户详情服务。
 *
 * @author shop-admin
 */
@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminMapper adminMapper;

    private final MenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminDO adminDO = adminMapper.selectOne(Wrappers.<AdminDO>lambdaQuery()
                .eq(AdminDO::getUsername, username));
        if (adminDO == null) {
            throw new UsernameNotFoundException("管理员不存在");
        }
        return AdminPrincipal.from(adminDO,
                menuService.getPermissionCodesByRole(adminDO.getRoleCode()));
    }
}

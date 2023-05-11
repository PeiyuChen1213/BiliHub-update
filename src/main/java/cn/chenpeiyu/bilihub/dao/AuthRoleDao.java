package cn.chenpeiyu.bilihub.dao;

import cn.chenpeiyu.bilihub.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRoleDao {

    AuthRole getRoleByCode(String code);
}

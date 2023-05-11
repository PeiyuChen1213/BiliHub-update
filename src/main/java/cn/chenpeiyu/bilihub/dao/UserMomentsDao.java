package cn.chenpeiyu.bilihub.dao;

import cn.chenpeiyu.bilihub.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoment userMoment);
}

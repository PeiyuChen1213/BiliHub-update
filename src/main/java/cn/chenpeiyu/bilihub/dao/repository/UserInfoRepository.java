package cn.chenpeiyu.bilihub.dao.repository;

import cn.chenpeiyu.bilihub.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {

}

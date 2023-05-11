package cn.chenpeiyu.bilihub.dao.repository;

import cn.chenpeiyu.bilihub.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VideoRepository extends ElasticsearchRepository<Video, Long> {

    Video findByTitleLike(String keyword);
}

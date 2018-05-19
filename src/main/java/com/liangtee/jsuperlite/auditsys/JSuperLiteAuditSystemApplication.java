package com.liangtee.jsuperlite.auditsys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
//@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchCrudRepository.class))
//@EnableElasticsearchRepositories(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ElasticsearchCrudRepository.class))
public class JSuperLiteAuditSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(JSuperLiteAuditSystemApplication.class, args);
	}
}

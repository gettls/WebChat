package ex.websocket.config.redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;


@Configuration
@Profile("local") // server �� redis �� ���ÿ� �����Ű�� ����
@Slf4j
public class EmbeddedRedisConfig {
	
	@Value("${spring.redis.port}")
	private int redisPort;
	
	private RedisServer redisServer;
	
	
	@PostConstruct
	public void redisServer() {
		log.info("redisServer start");
		redisServer = new RedisServer(redisPort);
		redisServer.start();
	}
	
	@PreDestroy
	public void stopRedis() {
		if(redisServer != null) {
			redisServer.stop();
		}
	}
	
}

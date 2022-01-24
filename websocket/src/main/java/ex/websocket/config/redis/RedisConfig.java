package ex.websocket.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import ex.websocket.service.RedisSubscriber;

//import ����...

@Configuration
public class RedisConfig {

	/**
	  * ���� Topic ����� ���� Bean ����
	  */
	 @Bean
	 public ChannelTopic channelTopic() {
	     return new ChannelTopic("chatroom");
	 }

	 /**
	  * redis�� ����(publish)�� �޽��� ó���� ���� ������ ����
	  */
	 @Bean
	 public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
	                                                           MessageListenerAdapter listenerAdapter,
	                                                           ChannelTopic channelTopic) {
	     RedisMessageListenerContainer container = new RedisMessageListenerContainer();
	     container.setConnectionFactory(connectionFactory);
	     container.addMessageListener(listenerAdapter, channelTopic);
	     return container;
	 }

	 /**
	  * ���� �޽����� ó���ϴ� subscriber ���� �߰�
	  */
	 @Bean
	 public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
	     return new MessageListenerAdapter(subscriber, "onMessage");
	 }
	
	/**
	 * ���ø����̼ǿ��� ����� redisTemplate ����
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(connectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
		return redisTemplate;
	}
}



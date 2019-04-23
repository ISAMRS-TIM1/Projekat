package isamrs.tim1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@SuppressWarnings("deprecation")
@Configuration
/*
 * Koristi autokonfiguraciju da uveze potrebne artifakte kako bi se omogucilo slanje poruka preko web soketa.
 */
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

	/*
	 * Metoda registruje Stomp (https://stomp.github.io/) endpoint i koristi SockJS JavaScript biblioteku
	 * (https://github.com/sockjs)
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/friendsEndpoint").withSockJS();
	}
	
	/*
	 * Metoda konfigurise opcije message brokera. U ovom slucaju klijenti koji hoce da koriste web socket broker
	 * moraju da se konektuju na /topic.
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		/*
		 * Koristi se jednostavan message broker koji cuva poruke u memoriji
		 * i koji salje poruke klijentima na /topic.
		 */
		config.enableSimpleBroker("/friendsInvitation");
		/*
		 * Registrovan je /app prefiks koji se koristi za mapiranje svih poruka.
		 */
		config.setApplicationDestinationPrefixes("/app");
	}

}

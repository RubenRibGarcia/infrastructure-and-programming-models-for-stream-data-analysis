package org.isel.thesis.impads.connectors.rabbitmq.common;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class RabbitMQConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(RabbitMQConfiguration.class);

	private String host;
	private Integer port;
	private String username;
	private String password;

	private RabbitMQConfiguration(String host, Integer port, String username, String password){
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public static Builder builder() {
		return new Builder();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public ConnectionFactory getConnectionFactory()
			throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(this.host);
		factory.setPort(this.port);
		factory.setUsername(this.username);
		factory.setPassword(this.password);

		return factory;
	}

	/**
	 * The Builder Class for {@link RabbitMQConfiguration}.
	 */
	public static class Builder {

		private String host;
		private Integer port;
		private String username;
		private String password;

		private Builder() { }

		public Builder setHost(String host) {
			this.host = host;
			return this;
		}

		public Builder setPort(int port) {
			this.port = port;
			return this;
		}

		public Builder setUserName(String username) {
			this.username = username;
			return this;
		}

		public Builder setPassword(String password) {
			this.password = password;
			return this;
		}

		public RabbitMQConfiguration build(){
			return new RabbitMQConfiguration(host, port, username, password);
		}
	}
}

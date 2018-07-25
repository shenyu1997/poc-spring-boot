package yu.shen.pocboot.common.rest;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(HttpClientConfig.HttpClientConfigurationProperties.class)
public class HttpClientConfig {
    private static Logger logger = LoggerFactory.getLogger("yu.shen.poc.common.rest");

    @ConfigurationProperties("http-client")
    static class HttpClientConfigurationProperties {
        private int maxTotal;
        private int defaultMaxPerRoute;
        private int connectionRequestTimeout;
        private int connectionTimeout;
        private int socketTimeout;
        private int defaultKeepAlive;
        private int maxIdleTime;

        public int getDefaultKeepAlive() {
            return defaultKeepAlive;
        }

        public void setDefaultKeepAlive(int defaultKeepAlive) {
            this.defaultKeepAlive = defaultKeepAlive;
        }

        public int getConnectionRequestTimeout() {
            return connectionRequestTimeout;
        }

        public void setConnectionRequestTimeout(int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }

        public int getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        public int getSocketTimeout() {
            return socketTimeout;
        }

        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public int getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public int getDefaultMaxPerRoute() {
            return defaultMaxPerRoute;
        }

        public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
            this.defaultMaxPerRoute = defaultMaxPerRoute;
        }
    }

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(HttpClientConfigurationProperties httpConnectionPoolConfiguration,
                                                                                 List<HttpConnectionPoolConfiguration> httpConnectionPoolConfigurationList) {
        PoolingHttpClientConnectionManager result = new PoolingHttpClientConnectionManager();
        result.setMaxTotal(httpConnectionPoolConfiguration.getMaxTotal());
        result.setDefaultMaxPerRoute(httpConnectionPoolConfiguration.getDefaultMaxPerRoute());
        for(HttpConnectionPoolConfiguration remoteConfiguration: httpConnectionPoolConfigurationList) {
            HttpHost httpHost = new HttpHost(remoteConfiguration.getHost(), remoteConfiguration.getPort(), remoteConfiguration.getSchema());
            result.setMaxPerRoute(new HttpRoute((httpHost)), remoteConfiguration.getMaxConnection());
        }

        return result;
    }

    @Bean
    public RequestConfig requestConfig(HttpClientConfigurationProperties httpConnectionPoolConfiguration) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(httpConnectionPoolConfiguration.getConnectionRequestTimeout())
                .setConnectTimeout(httpConnectionPoolConfiguration.getConnectionTimeout())
                .setSocketTimeout(httpConnectionPoolConfiguration.getSocketTimeout())
                .build();
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(HttpClientConfigurationProperties httpConnectionPoolConfiguration) {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase
                        ("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return httpConnectionPoolConfiguration.getDefaultKeepAlive() * 1000;
        };
    }

    @Bean
    public HttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
                                 RequestConfig requestConfig,
                                 ConnectionKeepAliveStrategy connectionKeepAliveStrategy) {
        return HttpClientBuilder
                .create()
                .setKeepAliveStrategy(connectionKeepAliveStrategy)
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Component
    static class ConnectionManagerCleanup {
        @Autowired
        private PoolingHttpClientConnectionManager connectionManager;
        @Autowired
        private HttpClientConfigurationProperties httpClientConfigurationProperties;

        @Scheduled(fixedRate = 1000)
        public void idleConnectionMonitor() {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(httpClientConfigurationProperties.getMaxTotal(), TimeUnit.SECONDS);
            logger.trace("connectionManager: {} is clean up idle connections and ExpiredConnections",connectionManager);
        }
    }



}

package webtech.online.course.configs;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    @Value("${spring.elasticsearch.uris}")
    private String ES_ENDPOINT;

    @Value("${spring.elasticsearch.password}")
    private String API_KEY;
    @Bean
    public ElasticsearchClient elasticsearchClient() {
        System.out.println(ES_ENDPOINT + API_KEY + "api key");
        Header[] headers = new Header[] {
                new BasicHeader("Authorization", "ApiKey " + API_KEY)
        };

        RestClient restClient = RestClient.builder(
                        HttpHost.create(ES_ENDPOINT)
                )
                .setDefaultHeaders(headers)
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient,
                new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}

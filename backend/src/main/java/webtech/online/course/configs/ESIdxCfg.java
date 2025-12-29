package webtech.online.course.configs;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

//@Configuration
@Slf4j
public class ESIdxCfg {

    private final ElasticsearchOperations operations;
    @Autowired
    public ESIdxCfg(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    @PostConstruct
    public void createIndex() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:Mapping/*.json");

        ObjectMapper mapper = new ObjectMapper();

        for (Resource resource : resources) {
            String fileName = Objects.requireNonNull(resource.getFilename());
            String indexName = fileName.replace(".json", "");
            IndexOperations indexOps = operations.indexOps(IndexCoordinates.of(indexName));

            // Load JSON
            String json = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            Map<String, Object> map = mapper.readValue(json, new TypeReference<>() {});

            Map<String, Object> settings = (Map<String, Object>) map.get("settings");
            Map<String, Object> mappings = (Map<String, Object>) map.get("mappings");

            if (!indexOps.exists()) {
                log.debug("Creating index: " + indexName);

                if (settings != null) {
                    indexOps.create(settings);
                } else {
                    indexOps.create();
                }

                if (mappings != null) {
                    indexOps.putMapping(Document.from(mappings));
                }

                log.info("Index " + indexName + " created successfully!");
            } else {
                log.info("Index " + indexName + " already exists. Skipping...");
            }
        }
    }
}

package webtech.online.course.configs;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SuggestConfig {
    @Bean
    public Analyzer analyzer() {
        return new StandardAnalyzer();
    }
}

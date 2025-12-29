package webtech.online.course.configs;

import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Component
public class DbDataStoreFactory extends AbstractDataStoreFactory {

    private final JdbcTemplate jdbcTemplate;

    public DbDataStoreFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
        return new DbDataStore<>(this, id, jdbcTemplate);
    }

    private static class DbDataStore<V extends Serializable> extends AbstractDataStore<V> {
        private final JdbcTemplate jdbcTemplate;
        private final String storeId;

        DbDataStore(DbDataStoreFactory factory, String id, JdbcTemplate jdbcTemplate) {
            super(factory, id);
            this.jdbcTemplate = jdbcTemplate;
            this.storeId = id; // id ở đây là tên của DataStore (ví dụ: context của YouTube hoặc Drive)
        }

        @Override
        public Set<String> keySet() throws IOException {
            String sql = "SELECT key_id FROM google_tokens WHERE store_id = ?";
            return new java.util.HashSet<>(jdbcTemplate.queryForList(sql, String.class, storeId));
        }

        @Override
        public Collection<V> values() throws IOException {
            V value = get(getId());
            return value == null ? Collections.emptyList() : Collections.singletonList(value);
        }

        @Override
        public V get(String key) throws IOException {
            String sql = "SELECT token_data FROM google_tokens WHERE store_id = ? AND key_id = ?";
            try {
                byte[] data = jdbcTemplate.queryForObject(sql, byte[].class, storeId, key);
                if (data == null)
                    return null;
                try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                    return (V) ois.readObject();
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public DataStore<V> set(String key, V value) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(value);
            }
            byte[] data = baos.toByteArray();

            String sql = "INSERT INTO google_tokens (store_id, key_id, token_data) VALUES (?, ?, ?) " +
                    "ON CONFLICT (store_id, key_id) DO UPDATE SET token_data = EXCLUDED.token_data";
            jdbcTemplate.update(sql, storeId, key, data);
            return this;
        }

        @Override
        public DataStore<V> clear() throws IOException {
            jdbcTemplate.update("DELETE FROM google_tokens WHERE store_id = ?", storeId);
            return this;
        }

        @Override
        public DataStore<V> delete(String key) throws IOException {
            jdbcTemplate.update("DELETE FROM google_tokens WHERE store_id = ? AND key_id = ?", storeId, key);
            return this;
        }
    }
}

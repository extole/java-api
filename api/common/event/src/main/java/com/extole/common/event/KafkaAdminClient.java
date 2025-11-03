package com.extole.common.event;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.extole.spring.StartFirstStopLast;

@Component
public class KafkaAdminClient implements StartFirstStopLast {
    private final Map<String, AdminClient> edgeClients = new ConcurrentHashMap<>();
    private final Map<String, AdminClient> globalClients = new ConcurrentHashMap<>();

    @Autowired
    public KafkaAdminClient() {
    }

    @Override
    public void stop() {
        edgeClients.values().forEach(client -> client.close());
        globalClients.values().forEach(client -> client.close());
    }

    public Map<String, AdminClient> getAll(KafkaClusterType kafkaClusterType) {
        return Collections.unmodifiableMap(adminClients(kafkaClusterType));
    }

    public AdminClient getInstance(KafkaClusterType kafkaClusterType, String bootstrapServers) {
        Map<String, AdminClient> clients = adminClients(kafkaClusterType);
        return clients.computeIfAbsent(bootstrapServers, (servers) -> createAdminClient(servers));
    }

    private Map<String, AdminClient> adminClients(KafkaClusterType kafkaClusterType) {
        return KafkaClusterType.EDGE == kafkaClusterType ? edgeClients : globalClients;
    }

    private AdminClient createAdminClient(String bootstrapServers) {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(properties);
    }
}

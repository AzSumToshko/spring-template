package com.example.spring_template.config;

//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openssl.PEMParser;
//import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
//import org.flywaydb.core.Flyway;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//import java.io.StringReader;
//import java.security.PrivateKey;
//import java.security.Security;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.util.Base64;
//import java.util.Properties;

@Configuration
public class SnowflakeFlywayConfiguration {
//
//    @Value("${spring.datasource.url}")
//    private String dataSourceUrl;
//
//    @Value("${spring.datasource.username}")
//    private String dataSourceUsername;
//
//    @Value("${spring.datasource.private-key}")
//    private String dataSourcePrivateKey;
//
//    @Value("${spring.flyway.url}")
//    private String flywayUrl;
//
//    @Value("${spring.flyway.username}")
//    private String flywayUsername;
//
//    @Value("${spring.flyway.private-key}")
//    private String flywayPrivateKey;
//
//    @Value("${spring.flyway.deploy.locations}")
//    private String flywayLocations;
//
//    private PrivateKey getPrivateKeyFromString(String key) {
//        try {
//            Security.addProvider(new BouncyCastleProvider());
//
//            String pemFormattedKey = "-----BEGIN PRIVATE KEY-----\n" +
//                    key + "\n-----END PRIVATE KEY-----";
//
//            try (PEMParser pemParser = new PEMParser(new StringReader(pemFormattedKey))) {
//                Object pemObject = pemParser.readObject();
//
//                if (pemObject instanceof PrivateKeyInfo privateKeyInfo) {
//                    return new JcaPEMKeyConverter()
//                            .setProvider(BouncyCastleProvider.PROVIDER_NAME)
//                            .getPrivateKey(privateKeyInfo);
//                } else {
//                    throw new IllegalArgumentException("Unable to parse private key.");
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Invalid private key: " + e.getMessage(), e);
//        }
//    }
//
//    private Connection createSnowflakeConnection(String url, String username, String privateKeyString) throws SQLException {
//        PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);
//
//        Properties props = new Properties();
//        props.put("user", username);
//        props.put("privateKey", privateKey);
//        props.put("authenticator", "snowflake_jwt");
//
//        return DriverManager.getConnection(url, props);
//    }
//
//    private DataSource buildCustomSnowflakeDataSource(String url, String username, String privateKey) {
//        return new DataSource() {
//            @Override
//            public Connection getConnection() throws SQLException {
//                return createSnowflakeConnection(url, username, privateKey);
//            }
//
//            @Override
//            public Connection getConnection(String username, String password) throws SQLException {
//                throw new UnsupportedOperationException("Username/password not supported.");
//            }
//
//            @Override public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
//            @Override public boolean isWrapperFor(Class<?> iface) { throw new UnsupportedOperationException(); }
//            @Override public java.io.PrintWriter getLogWriter() { throw new UnsupportedOperationException(); }
//            @Override public void setLogWriter(java.io.PrintWriter out) { throw new UnsupportedOperationException(); }
//            @Override public void setLoginTimeout(int seconds) { throw new UnsupportedOperationException(); }
//            @Override public int getLoginTimeout() { throw new UnsupportedOperationException(); }
//            @Override public java.util.logging.Logger getParentLogger() { throw new UnsupportedOperationException(); }
//        };
//    }
//
//    @Bean
//    public DataSource customSnowflakeDataSource() {
//        DataSource customDataSource = buildCustomSnowflakeDataSource(dataSourceUrl, dataSourceUsername, dataSourcePrivateKey);
//
//        HikariConfig config = new HikariConfig();
//        config.setDataSource(customDataSource);
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(2);
//
//        return new HikariDataSource(config);
//    }
//
//    @Bean
//    public Flyway flyway() {
//        DataSource flywayDataSource = buildCustomSnowflakeDataSource(flywayUrl, flywayUsername, flywayPrivateKey);
//
//        return Flyway.configure()
//                .dataSource(flywayDataSource)
//                .locations(flywayLocations)
//                .load();
//    }
//
//    @Bean
//    public CommandLineRunner applyMigrations(Flyway flyway) {
//        return args -> {
//            flyway.repair();
//            flyway.migrate();
//        };
//    }
}
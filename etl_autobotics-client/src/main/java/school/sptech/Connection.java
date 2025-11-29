package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Connection {
    private static JdbcTemplate template;

    static {
        BasicDataSource dataSource = new BasicDataSource();
        String bd = System.getenv("BD_IP");

        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + bd + ":3306/autobotics");
        dataSource.setUsername("agente");
        dataSource.setPassword("sptech");

        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(5);
        dataSource.setMaxWaitMillis(5000);

        template = new JdbcTemplate(dataSource);
    }

    public static JdbcTemplate getTemplate() {
        return template;
    }
}
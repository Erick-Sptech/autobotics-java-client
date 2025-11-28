package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class Connection {
    private BasicDataSource dataSource;
    private JdbcTemplate template;

    public Connection(){
        dataSource = new BasicDataSource();


        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/autobotics");
        dataSource.setUsername("agente");
        dataSource.setPassword("sptech");

        this.template = new JdbcTemplate(dataSource);
    }

    public BasicDataSource getDataSource(){
        return dataSource;
    }
    public JdbcTemplate getTemplate() {
        return template;
    }

}

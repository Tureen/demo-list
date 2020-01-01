package club.tulane.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@SpringBootApplication
public class DemoDatasourceApplication implements CommandLineRunner {

	@Autowired
	private DataSource dataSource;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(DemoDatasourceApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		showConnection();
		showData();
	}

	private void showData() {
		jdbcTemplate.queryForList("select * from foo")
				.forEach(row -> log.info("row -> [{}]", row.toString()));
	}

	private void showConnection() throws SQLException {
		log.info("dataSource -> [{}]", dataSource.toString());
		Connection conn = dataSource.getConnection();
		log.info("conn -> [{}]", conn.toString());
		conn.close();
	}

}

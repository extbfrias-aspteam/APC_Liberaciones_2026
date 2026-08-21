package mx.net.asp.asp_pago_services_v2.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Clase para configurar los DataSource y los JdbcTemplate de la base de datos
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

	/**
	 * Metodo para obtener un DataSource a partir de las properties spring.cero
	 * @return objeto DataSource con la conexion creada ceroDataSource
	 */
	@Bean
	@Qualifier("ceroDataSource")
	@ConfigurationProperties(prefix = "spring.cero.datasource.hikari")
	DataSource ceroDataSource() {
		// Se crea y regresa un DataSource
		return DataSourceBuilder.create().build();
	}

	/**
	 * Metodo para obtener un DataSource a partir de las properties spring.procrea
	 * @return objeto DataSource con la conexion creada procreaDataSource
	 */
	@Bean
	@Qualifier("procreaDataSource")
	@ConfigurationProperties(prefix = "spring.procrea.datasource.hikari")
	DataSource procreaDataSource() {
		// Se crea y regresa un DataSource
		return DataSourceBuilder.create().build();
	}
	
	/**
	 * Metodo para obtener un DataSource a partir de las properties spring.izelsti
	 * @return objeto DataSource con la conexion creada izelstiDataSource
	 */
//	@Bean
//	@Qualifier("izelstiDataSource")
//	@ConfigurationProperties(prefix = "spring.izelsti")
//	DataSource izelstiDataSource() {
//		// Se crea y regresa un DataSource
//		return DataSourceBuilder.create().build();
//	}

	/**
	 * Metodo para obtener un JdbcTemplate a partir del DataSource 
	 * @param ceroDataSource objeto DataSource del tipo ceroDataSource
	 * @return objeto JdbcTemplate para realizar operaciones en la bd
	 */
	@Bean
	@Qualifier("ceroJdbcTemplate")
	JdbcTemplate ceroJdbcTemplate(@Qualifier("ceroDataSource") DataSource ceroDataSource) {
		// Se crea y se regresa un JdbcTemplate
		return new JdbcTemplate(ceroDataSource);
	}

	/**
	 * Metodo para obtener un JdbcTemplate a partir del DataSource
	 * @param ceroDataSource objeto DataSource del tipo ceroDataSource
	 * @return objeto JdbcTemplate para realizar operaciones en la bd
	 */
	@Bean
	@Primary
	@Qualifier("namedCeroJdbcTemplate")
	NamedParameterJdbcTemplate namedParameterCeroJdbcTemplate(@Qualifier("ceroDataSource") DataSource ceroDataSource) {
		// Se crea y se regresa un JdbcTemplate
		return new NamedParameterJdbcTemplate(ceroDataSource);
	}

	/**
	 * Metodo para obtener un JdbcTemplate a partir del DataSource 
	 * @param procreaDataSource objeto DataSource del tipo procreaDataSource
	 * @return objeto JdbcTemplate para realizar operaciones en la bd
	 */
	@Bean
	@Qualifier("procreaJdbcTemplate")
	JdbcTemplate procreaJdbcTemplate(@Qualifier("procreaDataSource") DataSource procreaDataSource) {
		// Se crea y se regresa un JdbcTemplate
		return new JdbcTemplate(procreaDataSource);
	}

	/**
	 * Metodo para obtener un JdbcTemplate a partir del DataSource
	 * @param procreaDataSource objeto DataSource del tipo procreaDataSource
	 * @return objeto JdbcTemplate para realizar operaciones en la bd
	 */
	@Bean
	@Qualifier("namedProcreaJdbcTemplate")
	NamedParameterJdbcTemplate procreaNamedJdbcTemplate(@Qualifier("procreaDataSource") DataSource procreaDataSource) {
		// Se crea y se regresa un JdbcTemplate
		return new NamedParameterJdbcTemplate(procreaDataSource);
	}
	// Configuración de TransactionManager para ceroDataSource
	@Bean
	public PlatformTransactionManager transactionManagerCero(@Qualifier("ceroDataSource") DataSource ceroDataSource) {
		return new DataSourceTransactionManager(ceroDataSource);
	}

	// Configuración de TransactionManager para procreaDataSource
	@Bean
	public PlatformTransactionManager transactionManagerProcrea(@Qualifier("procreaDataSource") DataSource procreaDataSource) {
		return new DataSourceTransactionManager(procreaDataSource);
	}
	/**
	 * Metodo para obtener un JdbcTemplate a partir del DataSource 
	 * @param izelstiDataSource objeto DataSource del tipo izelstiDataSource
	 * @return objeto JdbcTemplate para realizar operaciones en la bd
	 */
//	@Bean
//	@Qualifier("izelstiJdbcTemplate")
//	JdbcTemplate izelstiJdbcTemplate(@Qualifier("izelstiDataSource") DataSource izelstiDataSource) {
//		// Se crea y se regresa un JdbcTemplate
//		return new JdbcTemplate(izelstiDataSource);
//	}
}
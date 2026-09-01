package com.mx.asp.pago.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.mx.asp.pago.record.AuthClientRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthClientLookupRepository {

	private static final String FIND_ACTIVE_AUTH_CLIENT_BY_CLABE = """
			select idpblu, username, password
			from validaciones_banco.auth_clients
			where clabe = cast(? as numeric)
			  and is_active = true
			limit 1
			""";

	@Qualifier("authClientsJdbcTemplate")
	private final JdbcTemplate authClientsJdbcTemplate;

	public Optional<AuthClientRecord> findActiveAuthClientByClabe(String clabe) {
		List<AuthClientRecord> results = authClientsJdbcTemplate.query(
				FIND_ACTIVE_AUTH_CLIENT_BY_CLABE,
				(rs, rowNum) -> new AuthClientRecord(
						rs.getBigDecimal("idpblu").toPlainString(),
						rs.getString("username"),
						rs.getString("password")),
				clabe);

		return results.stream().findFirst();
	}
}

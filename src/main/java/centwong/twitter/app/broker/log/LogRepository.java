package centwong.twitter.app.broker.log;

import centwong.twitter.entity.DbLog;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends R2dbcRepository<DbLog, Long> { }

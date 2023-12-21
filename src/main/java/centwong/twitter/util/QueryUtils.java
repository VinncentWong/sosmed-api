package centwong.twitter.util;

import centwong.twitter.entity.PgParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class QueryUtils {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static Query generateSqlExtension(Object param){

        log.info("accepting param on QueryUtils: {}", param);

        if(param == null){
            throw new RuntimeException("param generate sql extension can't be null");
        }
        List<Criteria> criterias = new ArrayList<>();
        var clazz = param.getClass();
        var fields = clazz.getDeclaredFields();

        PgParam pg = null;

        for(var f: fields){
            try{
                f.setAccessible(true);
                String columnName;
                if(f.isAnnotationPresent(Column.class)){
                    var columnAnnotation = f.getAnnotation(Column.class);
                    columnName = columnAnnotation.value();
                } else {
                    columnName = f.getName();
                }

                var columnValue = f.get(param);

                if(columnValue != null && f.getType() != PgParam.class && !Collection.class.isAssignableFrom(columnValue.getClass())) {
                    criterias.add(
                            Criteria
                                    .where(columnName)
                                    .is(columnValue)
                    );
                }

                if(columnValue != null && Collection.class.isAssignableFrom(columnValue.getClass())){
                    log.info("catch list fields with value: {}", columnValue);
                    criterias.add(
                            Criteria
                                    .where(columnName.substring(0, columnName.length() - 1))
                                    .in((List<Object>)columnValue)
                    );
                }

                if(f.getType() == PgParam.class){
                    if(columnValue != null){
                        pg = (PgParam) columnValue;
                    }
                }
            } catch(Exception e){
                log.error("error occurred when generating Query with message {}", e.getMessage());
                throw e;
            } finally {
                f.setAccessible(false);
            }
        }

        var coreCriteria = Criteria.empty();
        for(var c: criterias){
            coreCriteria = coreCriteria
                    .and(c);
        }

        Query query;

        if(pg != null){
            log.info("pg attribute found on QueryUtils with pg = {}", pg);
            if(pg.isActive()){
                coreCriteria = coreCriteria
                        .and(
                                Criteria
                                        .where("deleted_at")
                                        .isNull()
                        );
            }

            query = Query
                    .query(coreCriteria)
                    .offset(pg.getPage())
                    .limit((int)pg.getLimit());
        } else {
            log.info("pg attribute not found on QueryUtils");
            query = Query
                    .query(
                            coreCriteria
                                    .and(
                                            Criteria.where("deleted_at")
                                                    .isNull()
                                    )
                    )
                    .limit(1);
        }

        return query;
    }
}

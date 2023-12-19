package centwong.twitter.util;

import centwong.twitter.entity.PgParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryUtils {

    @SneakyThrows
    public static Query generateSqlExtension(Object param){
        if(param == null){
            throw new RuntimeException("param generate sql extension can't be null");
        }
        List<Criteria> criterias = new ArrayList<>();
        var clazz = param.getClass();
        var fields = clazz.getFields();

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

                if(columnValue != null && f.getType() != PgParam.class) {
                    criterias.add(
                            Criteria
                                    .where(columnName)
                                    .is(columnValue)
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
            coreCriteria
                    .and(c);
        }

        Query query;

        if(pg != null){
            query = Query
                    .query(coreCriteria)
                    .offset(pg.getPage())
                    .limit(pg.getLimit().intValue());
        } else {
            query = Query.query(coreCriteria);
        }

        return query;
    }
}

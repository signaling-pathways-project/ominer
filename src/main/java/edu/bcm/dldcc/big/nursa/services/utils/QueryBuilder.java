package edu.bcm.dldcc.big.nursa.services.utils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexey on 5/22/15.
 */
public class QueryBuilder {

    private List<String> columns = new ArrayList<String>();

    private List<String> from = new ArrayList<String>();

    private List<String> where = new ArrayList<String>();

    private List<String> joins = new ArrayList<String>();

    private List<String> leftJoin = new ArrayList<String>();

    private String ifEmptyColumns = "*";

    private Map<String, Object> namedParameter = new HashMap<String, Object>();

    private List<String> groupBy = new ArrayList<String>();

    
    
    public Map<String, Object> getNamedParameter() {
		return namedParameter;
	}

	public QueryBuilder addNamedParameter (String name, Object value) {
        namedParameter.put(name, value);
        return this;
    }

    public QueryBuilder join(String j) {
        joins.add(j);
        return this;
    }

    public QueryBuilder leftJoin(String j) {
        leftJoin.add(j);
        return this;
    }

    public QueryBuilder() {

    }

    public QueryBuilder column(String c) {
        columns.add(c);
        return this;
    }

    public QueryBuilder from(String f) {
        from.add(f);
        return this;
    }

    public QueryBuilder where(String w) {
        where.add(w);
        return this;
    }

    public QueryBuilder setIfEmptyColumns(String s) {
        ifEmptyColumns = s;
        return this;
    }

    public QueryBuilder groupBy(String group) {
        groupBy.add(group);
        return this;
    }

    private void concatinate(StringBuilder sql, List<String> list, String startWith ,String separater) {
        String s = "";
        if (list.size() > 0) {
            sql.append(startWith);
            for (String element : list) {
                if (!"".equals(element)) {
                    sql.append(s);
                    s = separater;
                    sql.append(element);
                }
            }
        }
    }
    
    public Query buildQuery(EntityManager entityManager , String mapperName) {
        String queryStr = this.toString();
        Query q;
        if (mapperName != null) {
            q = entityManager.createNativeQuery(queryStr, mapperName);
        } else {
            q = entityManager.createNativeQuery(queryStr);
        }
        for (Map.Entry<String, Object> entry : namedParameter.entrySet() ) {
            q.setParameter(entry.getKey(),entry.getValue());
        }
        return q;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder("select ");

        if (columns.size()  == 0) {
            string.append(ifEmptyColumns);
        } else {
            concatinate(string, columns, "", ", ");
        }
        concatinate(string, from," from ", ", ");
        concatinate(string, joins, " inner join ", " inner join ");
        concatinate(string, leftJoin," left join " , " left join ");
        concatinate(string, where, " where " , " and ");

        if (groupBy.size()>0) {
            concatinate(string, groupBy, " group by ( ", ", ");
            string.append(" )");
        }

        return string.toString();
    }

    
}

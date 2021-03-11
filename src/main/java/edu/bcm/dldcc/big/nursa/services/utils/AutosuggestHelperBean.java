package edu.bcm.dldcc.big.nursa.services.utils;

import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestList;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestListImpl;
import edu.bcm.dldcc.big.nursa.model.omics.dto.AutosuggestTermImpl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexey on 3/3/15.
 * @author mcowiti Make this a concurrent server component
 */
@Stateless
@TransactionAttribute(value=TransactionAttributeType.NOT_SUPPORTED)
public class AutosuggestHelperBean
{

	@PersistenceContext(unitName = "NURSA")
    private EntityManager entityManager;
	
    //===============
    //This method basically takes a tuple array (as returned by native queries) and maps it against a provided POJO class
    // by look for a constructor that has the same number of fields and of the same type.
    //
    private  <T> T map(Class<T> type, Object[] tuple)
    {
        List<Class<?>> tupleTypes = new ArrayList();
        for(Object field : tuple){
            tupleTypes.add(field.getClass());
        }
        try {
            Constructor<T> ctor = type.getConstructor(tupleTypes.toArray(new Class<?>[tuple.length]));
            return ctor.newInstance(tuple);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private  <T> List<T> map(Class<T> type, List<Object[]> records)
    {
        List<T> result = new LinkedList();
        for(Object[] record : records){
            result.add(map(type, record));
        }
        return result;
    }

    public  <T> List<T> getResultListWithMapping (Query query, Class<T> type)
    {
        @SuppressWarnings("unchecked")
        List<Object[]> records = query.getResultList();
        return map(type, records);
    }

    private  Query quryHelper(String sqlStr, String regExpr,   Integer max)
    {
        Query query = this.entityManager.createQuery(sqlStr);
        query.setParameter("expr", regExpr);
        query.setMaxResults(max);
        return query;
    }

    private  Query nativeQueryHelper(String queryStr, String regExpr,  Integer max)
    {
        Query query = this.entityManager.createNativeQuery(queryStr);
        query.setParameter("query", regExpr);
        query.setMaxResults(max);
        return query;
    }

    private  AutosuggestList getAutosuggestListNativeQ(String sqlString, String param, Integer max)
    {
        Query startsQuery =  nativeQueryHelper(sqlString, startsWithRegExpr(param), max);
        List<AutosuggestTermImpl> startsWithList = getResultListWithMapping(startsQuery, AutosuggestTermImpl.class);

        Query containsQuery =  nativeQueryHelper(sqlString, containsRegExpr(param), max);
        List<AutosuggestTermImpl> containsList = getResultListWithMapping(containsQuery, AutosuggestTermImpl.class);

        return new AutosuggestListImpl(startsWithList,containsList);
    }

    public  AutosuggestList autoSuggestGOTerm(String query, Integer max)
    {
        String sqlStr = SQLAutosuggests.GOTERM;
        Query startsWithQuery = quryHelper(sqlStr, startsWithRegExpr(query), max);
        List<String> startsWithList= startsWithQuery.getResultList();

        Query containsQuer = quryHelper(sqlStr, containsRegExpr(query), max);
        List<String> containsList = containsQuer.getResultList();

        return new AutosuggestListImpl(startsWithList, containsList);

    }

    /**
     * 3.8.2018 Since not maintaining molsynon anymore, move to using gene.
     * If sending back an ID to query post autocomplete, change query to use egeneid instead of mid
     * @param query
     * @param max
     * @return
     */
    public  AutosuggestList autoSuggestGene(String query, Integer max)
    {
        String sqlString = SQLAutosuggests.GENE_VIAGENEINFO; //.GENE

        AutosuggestList list = getAutosuggestListNativeQ(sqlString, query, max);
        return list;
    }

    public  AutosuggestList autoSuggestMolecule(String query, Integer max)
    {
        String sqlString = SQLAutosuggests.MOLECULE;

        AutosuggestList list = getAutosuggestListNativeQ(sqlString, query, max);
        return list;
    }

    public  AutosuggestList autoSuggestDisease(String query, Integer max)
    {
        String sqlString = SQLAutosuggests.DISEASE;

        return getAutosuggestListNativeQ(sqlString, query, max);
    }

    public  AutosuggestList autoSuggesTissue(String query, Integer max)
    {
        String sqlStr = SQLAutosuggests.TISSUES;

        Query startsQuery =  nativeQueryHelper(sqlStr, startsWithRegExpr(query), max);
        List<AutosuggestTermImpl> startsWithList = getResultListWithMapping(startsQuery, AutosuggestTermImpl.class);

        Query containsQuery =  nativeQueryHelper(sqlStr, containsRegExpr(query), max);
        List<AutosuggestTermImpl> containsList = getResultListWithMapping(containsQuery, AutosuggestTermImpl.class);

        return new AutosuggestListImpl(startsWithList, containsList);
    }

    private static String startsWithRegExpr(final String query)
    {
        return query.toUpperCase()+"%";
    }

    private static String containsRegExpr(final String query)
    {
        return "_%"+query.toUpperCase()+"%";
    }

    /**
     * TODO this needs be global
     * @param entityManager
     * @return
     */
    public  List getAllTissues(EntityManager entityManager) {
        String sqlStr = SQLAutosuggests.ALLTISSUES;
        Query q = this.entityManager.createNativeQuery(sqlStr);
        return getResultListWithMapping(q, AutosuggestTermImpl.class);
    }
}

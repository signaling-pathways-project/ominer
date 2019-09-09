package edu.bcm.dldcc.big.nursa.services.rest.local.oai;


import edu.bcm.dldcc.big.nursa.model.oai.ItemNursa;
import edu.bcm.dldcc.big.nursa.model.oai.MetadataNursa;
import edu.bcm.dldcc.big.nursa.services.utils.QueryBuilder;
import org.dspace.xoai.dataprovider.exceptions.IdDoesNotExistException;
import org.dspace.xoai.dataprovider.exceptions.OAIException;
import org.dspace.xoai.dataprovider.filter.ScopedFilter;
import org.dspace.xoai.dataprovider.handlers.results.ListItemIdentifiersResult;
import org.dspace.xoai.dataprovider.handlers.results.ListItemsResults;
import org.dspace.xoai.dataprovider.model.InMemoryItem;
import org.dspace.xoai.dataprovider.model.Item;
import org.dspace.xoai.dataprovider.repository.ItemRepository;
import org.dspace.xoai.model.oaipmh.Metadata;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexey on 10/15/15.
 */

public class ItemRepositoryNursa implements ItemRepository {

    EntityManager entityManager;

    public ItemRepositoryNursa(EntityManager entityManager) {
        super();
        this.entityManager = entityManager;
    }

    private static final String baseItemSelect = "SELECT one.doi, one.releaseDate, one.name, one.regmol as regMolList, one.authorslist, one.description," +
            "   one.repo, one.pubmedid, two.tname as tissueNames from " +
            "(SELECT  id, " +
            "         name, " +
            "      listagg(mlname, ',') WITHIN GROUP (ORDER by mlname) as regmol," +
            "      AUTHORSLIST," +
            "      DESCRIPTION," +
            "      releaseDate," +
            "      doi," +
            "      REPO," +
            "      pubmedid" +
            " from " +
            "  (SELECT  distinct (mlsn.name)  as mlname, " +
            "           dset.id as id," +
            "           dset.NAME as name, "+
            "           article.AUTHORSLIST, " +
            "          dset.DESCRIPTION, " +
            "          dset.RELEASE_DATE as releaseDate," +
            "          dset.DOI_DOI as doi," +
            "          dset.REPO," +
            "          r.pubmedid" +
            "    FROM NURSADATASET dset" +
            "      LEFT JOIN NURSADATASET_MOLECULE dsetmol on dset.ID = dsetmol.NURSADATASET_ID" +
            "      LEFT JOIN MOLECULE ml on ml.ID = dsetmol.MOLECULES_ID" +
            "      LEFT JOIN MOLSYNON mlsn on mlsn.ID = ml.OFFICIAL_ID" +
            "      LEFT JOIN TMEXPERIMENT exp on dset.ID = exp.DATASET_ID" +
            "      LEFT JOIN TISSUE t on t.ID = exp.TISSUESOURCE_ID" +
            "      LEFT JOIN REFERENCE r on r.ID = dset.REFERENCE_ID" +
            "      LEFT JOIN REFERENCEARTICLE article on article.PUBMEDID = r.ARTICLE_PUBMEDID" +
            "    WHERE dset.ACTIVE = 1)" +
            "    GROUP BY id, name, authorslist, description, releaseDate, doi, repo, pubmedid) one LEFT JOIN " +
            "  (SELECT id," +
            "     listagg(tname, ',') WITHIN GROUP (ORDER by tname) as tname FROM" +
            "     (" +
            "       select distinct(t.name) as tname, dset.ID" +
            "       FROM NURSADATASET dset" +
            "         inner join TMEXPERIMENT exp on dset.ID = exp.DATASET_ID" +
            "         INNER JOIN TISSUE t on t.ID = exp.TISSUESOURCE_ID" +
            "       WHERE  dset.ACTIVE = 1)" +
            "   GROUP BY  id) two on one.id = two.id";

    private ListItemIdentifiersResult getItemIdentifiersHelper(int offset, int length, Date from, Date until) {

        QueryBuilder builder = new QueryBuilder();
        builder.from("NURSADATASET dset")
                .where("dset.ACTIVE = 1");
        builder.setIfEmptyColumns("countDatapoints(*)");

        if (null != from) {
            builder.where("dset.RELEASE_DATE >= :from").addNamedParameter("from", from);
        }
        if (null != until) {
            builder.where("dset.RELEASE_DATE <= :until").addNamedParameter("until", until);
        }

        Query countQuery = builder.buildQuery(entityManager, null);

        int count = ((BigDecimal)countQuery.getSingleResult()).intValue();

        List result = builder.column("dset.DOI_DOI as identifier")
                            .column("dset.RELEASE_DATE")
                            .buildQuery(entityManager, "ItemIdentifierNursa")
                            .setFirstResult(offset)
                            .setMaxResults(length)
                            .getResultList();

        return new ListItemIdentifiersResult((count  > offset + length), result,count);
    }

    private Metadata convertToXoaiMetada(MetadataNursa metodata) {

        InMemoryItem item = new InMemoryItem();

        if (null != metodata.getDoi()) {
            item.with("identifier", metodata.getDoi());
        }
        if (null != metodata.getName()) {
            item.with("title", metodata.getName());
        }
        if (null != metodata.getAuthorslist()) {
            item.with("creator", metodata.getAuthorslist());
        }
        if (null != metodata.getDescription()) {
            item.with("description", metodata.getDescription());
        }
        item.with("publisher","Nuclear Receptor Signaling Atlas (NURSA)");

        if (null != metodata.getReleaseDate()) {
            item.with("date", metodata.getReleaseDate());
        }
        item.with("type","Dataset");

        if(null != metodata.getRepo()) {
            item.with("source", metodata.getRepo());
        }

        if (null != metodata.getPubmedid()) {
            item.with("relation", "PMID:"+metodata.getPubmedid());
        }

        StringBuilder subjectBuilder =  new StringBuilder("NURSA, Transcriptomine");
        if (null != metodata.getRegMolList()) {
            subjectBuilder.append(", ");
            subjectBuilder.append(metodata.getRegMolList());
        }
        item.with("subject", subjectBuilder.toString());
        item.with("rights","Licensing: Use of this dataset is governed by a Creative Commons Attribution 3.0 license, which provides for sharing, adaptation and both non-commercial and commercial re-use, as long as this dataset is cited.");

        return item.getMetadata();

    }


    private ListItemsResults getItemsHelper(int offset, int length, Date from, Date until) {
        QueryBuilder builder = new QueryBuilder();
        StringBuilder finalSql = new StringBuilder(baseItemSelect);
        builder.setIfEmptyColumns("countDatapoints(*)")
                .from("NURSADATASET dset")
                .leftJoin("REFERENCE r on r.ID = dset.REFERENCE_ID")
                .leftJoin("REFERENCEARTICLE article on article.PUBMEDID = r.ARTICLE_PUBMEDID")
                .where("dset.ACTIVE = 1");

        boolean whereSet = false;

        if (null != from) {
            whereSet = true;
            builder.where("dset.RELEASE_DATE >= :from").addNamedParameter("from", from);
            finalSql.append( " WHERE one.releaseDate >= :from ");
        }
        if (null != until) {
            if (whereSet) {
                finalSql.append(" and ");
            }
            else {
                finalSql.append(" where ");
            }
            finalSql.append(" one.releaseDate <= :until ");
            builder.where("dset.RELEASE_DATE <= :until").addNamedParameter("until", until);
        }

        Query countQuery = builder.buildQuery(entityManager, null);

        int count = ((BigDecimal)countQuery.getSingleResult()).intValue();

        Query selectQuery = entityManager.createNativeQuery(finalSql.toString(), "MetadataNursa");

         if(null != from ){
             selectQuery.setParameter("from", from);
         }
        if ( null != until ) {
            selectQuery.setParameter("until", until);
        }

        List<MetadataNursa> result = selectQuery
                                        .setFirstResult(offset)
                                        .setMaxResults(length)
                                        .getResultList();

        List<Item> items = new ArrayList<Item>(result.size());

        for (MetadataNursa metodata : result) {
            ItemNursa item = new ItemNursa(convertToXoaiMetada(metodata));
            item.setDatestamp(metodata.getReleaseDate())
                    .setIdentifier(metodata.getDoi());
            items.add(item);
        }

        return new ListItemsResults((count  > offset + length), items, count);
    }

    @Override
    public Item getItem(String identifier) throws IdDoesNotExistException, OAIException {

        Query query = entityManager.createNativeQuery(baseItemSelect + " WHERE one.doi = :doi","MetadataNursa");

        query.setParameter("doi", identifier);
//        List<MetadataNursa> resultList = AutosuggestHelper.getResultListWithMapping(query, MetadataNursa.class);

        List resultList = query.getResultList();
        if (resultList.size() == 0) {
            throw new IdDoesNotExistException("Did not find item with id"+identifier);
        }
        MetadataNursa metadataNursa = (MetadataNursa) resultList.get(0);

        ItemNursa item = new ItemNursa(convertToXoaiMetada(metadataNursa));
        item.setDatestamp(metadataNursa.getReleaseDate())
                .setIdentifier(metadataNursa.getDoi());

        return item;
    }

    @Override
    public ListItemIdentifiersResult getItemIdentifiers(List<ScopedFilter> filters, int offset, int length) throws OAIException {
        return getItemIdentifiersHelper(offset, length, null, null);
    }

    @Override
    public ListItemIdentifiersResult getItemIdentifiers(List<ScopedFilter> filters, int offset, int length, Date from) throws OAIException {
        return getItemIdentifiersHelper(offset, length, from, null);
    }

    @Override
    public ListItemIdentifiersResult getItemIdentifiersUntil(List<ScopedFilter> filters, int offset, int length, Date until) throws OAIException {
        return getItemIdentifiersHelper(offset, length, null, until);
    }

    @Override
    public ListItemIdentifiersResult getItemIdentifiers(List<ScopedFilter> filters, int offset, int length, Date from, Date until) throws OAIException {
        return getItemIdentifiersHelper(offset, length, from, until);
    }

    // We dont support setSpec
    @Override
    public ListItemIdentifiersResult getItemIdentifiers(List<ScopedFilter> filters, int offset, int length, String setSpec) throws OAIException {
        return null;
    }
    // We dont support setSpec
    @Override
    public ListItemIdentifiersResult getItemIdentifiers(List<ScopedFilter> filters, int offset, int length, String setSpec, Date from) throws OAIException {
        return null;
    }
    // We dont support setSpec
    @Override
    public ListItemIdentifiersResult getItemIdentifiersUntil(List<ScopedFilter> filters, int offset, int length, String setSpec, Date until) throws OAIException {
        return null;
    }
    // We dont support setSpec
    @Override
    public ListItemIdentifiersResult getItemIdentifiers(List<ScopedFilter> filters, int offset, int length, String setSpec, Date from, Date until) throws OAIException {
        return null;
    }

    @Override
    public ListItemsResults getItems(List<ScopedFilter> filters, int offset, int length) throws OAIException {
        return getItemsHelper(offset, length, null, null);
    }

    @Override
    public ListItemsResults getItems(List<ScopedFilter> filters, int offset, int length, Date from) throws OAIException {
        return getItemsHelper(offset, length, from, null);
    }

    @Override
    public ListItemsResults getItemsUntil(List<ScopedFilter> filters, int offset, int length, Date until) throws OAIException {
        return getItemsHelper(offset, length, null, until);
    }

    @Override
    public ListItemsResults getItems(List<ScopedFilter> filters, int offset, int length, Date from, Date until) throws OAIException {
        return getItemsHelper(offset, length, from, until);
    }

    @Override
    public ListItemsResults getItems(List<ScopedFilter> filters, int offset, int length, String setSpec) throws OAIException {
        return null;
    }

    @Override
    public ListItemsResults getItems(List<ScopedFilter> filters, int offset, int length, String setSpec, Date from) throws OAIException {
        return null;
    }

    @Override
    public ListItemsResults getItemsUntil(List<ScopedFilter> filters, int offset, int length, String setSpec, Date until) throws OAIException {
        return null;
    }

    @Override
    public ListItemsResults getItems(List<ScopedFilter> filters, int offset, int length, String setSpec, Date from, Date until) throws OAIException {
        return null;
    }
}

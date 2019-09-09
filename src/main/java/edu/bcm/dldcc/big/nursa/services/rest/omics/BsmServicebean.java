package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView;
import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView_;
import edu.bcm.dldcc.big.nursa.model.omics.BsmInactiveView;
import edu.bcm.dldcc.big.nursa.model.omics.BsmInactiveView_;
import edu.bcm.dldcc.big.nursa.model.omics.BsmView;
import edu.bcm.dldcc.big.nursa.model.omics.BsmView_;
import edu.bcm.dldcc.big.nursa.model.omics.dto.BsmReport;
import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BSM Rest services Bean
 * @author mcowiti
 */
@Stateless
public class BsmServicebean  {

    @PersistenceContext(unitName = "NURSA")
    private EntityManager em;

    public static final String MISSING_BSM_SYMBOL="Need provide a BSM Symbol";
    public static final String MISSING_BSM_SYMBOL_IN_LIST="No BSM with that symbol in this list : ";


    public void collectNodes(PathwayNodesView path, List<PathwayNodesView> paths){
        StringBuilder sb= new StringBuilder();
        List<String> nodesList=paths.stream()
                .map(e->e).distinct()
                .map(e->e.getNode())
                .collect(Collectors.toList());

        path.setNodesList(nodesList);
        path.setNodes(String.join(",", nodesList));
    }
    public List<PathwayNodesView> getExperimentPathways(Set<String> nodes){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<PathwayNodesView> criteria = cb.createQuery(PathwayNodesView.class);
        Root<PathwayNodesView> pathRoot = criteria.from(PathwayNodesView.class);
        Expression<String> exp =  pathRoot.get(PathwayNodesView_.node);
        criteria.select(pathRoot)
                .where(exp.in(new ArrayList<String>(nodes)));

        return em.createQuery(criteria).getResultList();
    }


    public List<BsmNode> getBsmViewBySQL(){

        //TODO 2B JPA, failed because View was not well defined fails on cb.isNotNull(bsmViewRoot.get(BsmView_.bsm)
        String SQL_SELECT_BSM_SYMBOL_NODE ="select bsm,node from BSMS_VIEW where bsm is not null";

        org.hibernate.Query q = null;
        q = (org.hibernate.Query) (em.unwrap(Session.class)).createSQLQuery(SQL_SELECT_BSM_SYMBOL_NODE);
        ((SQLQuery) q).addScalar("bsm", StandardBasicTypes.STRING);
        ((SQLQuery) q).addScalar("node", StandardBasicTypes.STRING);

        q.setResultTransformer(Transformers.aliasToBean(BsmNode.class));
        return (List<BsmNode>) q.list();
    }

    public List<BsmInactiveView> getInactiveBsmNodeMappings(){

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BsmInactiveView> criteria = cb.createQuery(BsmInactiveView.class);
        Root<BsmInactiveView> pathRoot = criteria.from(BsmInactiveView.class);

        criteria.select(pathRoot).where(cb.isNotNull(pathRoot.get(BsmInactiveView_.bsm)));
        return em.createQuery(criteria).getResultList();
    }

    public List<BsmView> getBsmBySymbol(String symbol,boolean multiple){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BsmView> criteria = cb.createQuery(BsmView.class);
        Root<BsmView> bsmViewRoot = criteria.from(BsmView.class);

        if(multiple){
            List<String> bsms=new ArrayList<String>();
            if(symbol.contains(","))
                bsms=Arrays.asList(symbol.split(","));
            if(symbol.contains(";"))
                bsms=Arrays.asList(symbol.split(";"));

            bsms=bsms.stream().map(String :: trim).collect(Collectors.toList());

            Expression<String> exp =  bsmViewRoot.get(BsmView_.bsm);
            criteria.select(bsmViewRoot)
                    .where(exp.in(new ArrayList<String>(bsms)));
        }else
            criteria.select(bsmViewRoot).where(cb.equal(bsmViewRoot.get(BsmView_.bsm), symbol));

        return em.createQuery(criteria).getResultList();
    }

    /**
     * JPQL is simpler, still uses Entity object Model
     * @return
     */
    public List<BsmView> getAllSPPNamedBsms(){
        TypedQuery<BsmView> q=em.createQuery("select b from BsmView b where b.bsm is not null",BsmView.class);
        return q.getResultList();
    }
}

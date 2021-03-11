package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.omics.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mcowiti
 * Pathway Node Family Service
 */
public class PathwayServiceBean {
    @PersistenceContext(unitName = "NURSA")
    private EntityManager em;

    public static final String PATHNAME_OTHER="Other";

    public List<SignalingPathway> getSignalingPathways(PathwayCategory type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SignalingPathway> criteria = cb.createQuery(SignalingPathway.class);
        Root<SignalingPathway> pathRoot = criteria.from(SignalingPathway.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.type),type));
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.active),1));
        criteria.select(pathRoot).where(predicates.toArray(new Predicate[]{}));

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.asc(pathRoot.get(SignalingPathway_.displayOrder)));
        orderList.add(cb.asc(pathRoot.get(SignalingPathway_.name)));
        criteria.orderBy(orderList);
        //single column order criteria.orderBy(cb.asc(pathRoot.get(SignalingPathway_.name)));
        return em.createQuery(criteria).getResultList();
    }

    public List<SignalingPathway> getModelsAndDiseases(){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SignalingPathway> criteria = cb.createQuery(SignalingPathway.class);
        Root<SignalingPathway> pathRoot = criteria.from(SignalingPathway.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.type),PathwayCategory.type));
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.name),PATHNAME_OTHER));
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.active),1));
        criteria.select(pathRoot).where(predicates.toArray(new Predicate[]{}));

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.asc(pathRoot.get(SignalingPathway_.displayOrder)));
        orderList.add(cb.asc(pathRoot.get(SignalingPathway_.name)));
        criteria.orderBy(orderList);
        return em.createQuery(criteria).getResultList();
    }

    public List<SignalingPathway> getNamedSignalingPathways(PathwayCategory levelType, String[] names){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SignalingPathway> criteria = cb.createQuery(SignalingPathway.class);
        Root<SignalingPathway> pathRoot = criteria.from(SignalingPathway.class);

        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.type),levelType));
        predicates.add(cb.equal(pathRoot.get(SignalingPathway_.active),1));
        if(names != null){
            Expression<String> nameExp = pathRoot.get(SignalingPathway_.name);
            predicates.add(nameExp.in(Arrays.asList(names)));
        }
        criteria.select(pathRoot).where(predicates.toArray(new Predicate[]{}));

        List<Order> orderList = new ArrayList<Order>();
        orderList.add(cb.asc(pathRoot.get(SignalingPathway_.displayOrder)));
        orderList.add(cb.asc(pathRoot.get(SignalingPathway_.name)));
        criteria.orderBy(orderList);
        return em.createQuery(criteria).getResultList();
    }
}

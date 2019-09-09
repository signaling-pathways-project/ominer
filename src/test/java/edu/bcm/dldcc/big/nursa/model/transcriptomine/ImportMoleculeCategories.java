package edu.bcm.dldcc.big.nursa.model.transcriptomine;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;




//@RunWith(Arquillian.class)
public class ImportMoleculeCategories {
/*
	@PersistenceContext
	private EntityManager objectEntityManager;

	@Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "moleCategoryTest.war")
             .addPackages(true, "edu.bcm.dldcc.big.nursa.model","edu.bcm.dldcc.big.nursa.util",
            		 "org.joda.time","edu.bcm.dldcc.big.security.model","edu.bcm.dldcc.big.util.qualifier")
             .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
	
	@Test
	public void test() {
		
		assert true;
	}
	
	@Test
	public void importCategory(){
		
		assert this.objectEntityManager!=null;
		
		CriteriaBuilder cb = this.objectEntityManager.getCriteriaBuilder();

		CriteriaQuery<MoleculeCategory> cq = cb.createQuery(MoleculeCategory.class);
		Root<MoleculeCategory> molCat = cq.from(MoleculeCategory.class);
		
		
		MoleculeCategory cat= new MoleculeCategory();
		cat.setName("Estrogen related receptor (ERR) ligands");
		cat.setAbbreviation("ERR ligands");
		
		this.objectEntityManager.persist(cat);
		
		this.objectEntityManager.flush();
		
		cq.select(molCat);
		List<MoleculeCategory> list=this.objectEntityManager.createQuery(cq).getResultList();
		
		assert list.size() ==1;
	}
	
*/
}

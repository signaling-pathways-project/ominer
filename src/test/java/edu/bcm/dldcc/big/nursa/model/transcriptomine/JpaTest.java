package edu.bcm.dldcc.big.nursa.model.transcriptomine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

public class JpaTest {

	private EntityManagerFactory factory;

	
	@Before
	  public void setUp() throws Exception {
	    factory = Persistence.createEntityManagerFactory("NURSA");
	    EntityManager em = factory.createEntityManager();

	    // Begin a new local transaction so that we can persist a new entity
	    em.getTransaction().begin();
	    try{
			
			/*MicroArrayExperiment micro= new MicroArrayExperiment();
			//micro.setId(id);
			micro.setType("RNA assay");
			micro.setName("Microarray Experiment");
			em.persist(micro);
			em.getTransaction().commit();

		    em.close();
		    */
			
		}finally{
			if(em!=null)
				em.close();
		}
	    
	  }

	 // @Test
	  public void checkAvailablePeople() {

	    EntityManager em = factory.createEntityManager();

	    em.close();
	  }
}

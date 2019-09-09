package edu.bcm.dldcc.big.nursa.model.transcriptomine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ImportMicroarrayExperimentType {

	@PersistenceContext
	private EntityManager objectEntityManager;
	
	@Deployment
	public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "microExpType.war")
             .addPackages(true, "edu.bcm.dldcc.big.nursa.model",
            		 "edu.bcm.dldcc.big.nursa.util",
            		 "edu.bcm.dldcc.big.nursa.data",
            		 "edu.bcm.dldcc.big.util.qualifier",
            		 "org.joda.time","org.primefaces.model")
             .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
             .addAsWebInfResource(new StringAsset("<faces-config version=\"2.0\"/>"), "faces-config.xml")                            
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        //to trigger JSF need faces-config?
    }
	
	
	@Test
	public void importCategory(){
		
		assert this.objectEntityManager!=null;
		
	}
	/*
    public static JavaArchive createDeployment() {
		return ShrinkWrap.create(JavaArchive.class, "microExpTest.jar")
				.addPackages(true, "edu.bcm.dldcc.big.nursa.model",
	            		 "edu.bcm.dldcc.big.nursa.util",
	            		 "edu.bcm.dldcc.big.nursa.data",
	            		 "edu.bcm.dldcc.big.util.qualifier",
	            		 "org.joda.time","org.primefaces.model","org.omnifaces")
	             .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
	}*/
    

}

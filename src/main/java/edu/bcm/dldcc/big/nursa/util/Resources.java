package edu.bcm.dldcc.big.nursa.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.util.ProjectStageProducer;

import edu.bcm.dldcc.big.nursa.util.qualifier.NoConversationDatabase;
import edu.bcm.dldcc.big.nursa.util.qualifier.NursaProjectStage;
import edu.bcm.dldcc.big.nursa.util.qualifier.UserDatabase;

@ApplicationScoped
@Named
public class Resources {
	
	
	@PersistenceUnit(unitName = "NURSA")
	private EntityManagerFactory entityManager;

	@UserDatabase
	@Produces
	@ConversationScoped
	public EntityManager  createEntityManager() {
        return this.entityManager.createEntityManager();
	}
	
	@PersistenceUnit(unitName = "NURSA")
	private EntityManagerFactory noConvoEntityManager;

	@NoConversationDatabase
	@Produces
	public EntityManager  createnoConvoEntityManager() {
        return this.noConvoEntityManager.createEntityManager();
	}

	@Inject
	@ConfigProperty(name = "schema") 
    private String schema; 
	
	@Produces
	@Named("nursaDbSchema")
	@NursaProjectStage
	public String getSchema() {
		return this.schema;
	}
	
	@Produces
	@NursaProjectStage
	@Named
    public ProjectStage currentProjectStage() {
        return ProjectStageProducer.getInstance().getProjectStage();
    }

	@Produces
	@Named("swaggerUrl")
	public String getSupportAddress(){
	    return ConfigResolver.getProjectStageAwarePropertyValue("swagger.url");
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T lookup(BeanManager manager, Class<T> beanClass,
			Annotation... qualifiers) {
		return (T) lookup(manager, (Type) beanClass, qualifiers);
	}

	@SuppressWarnings({ "unchecked" })
	public static Object lookup(BeanManager manager, Type beanType,
			Annotation... qualifiers) {
		Set<?> beans = manager.getBeans(beanType, qualifiers);
		if (beans.size() != 1) {
			if (beans.size() == 0) {
				throw new RuntimeException("No beans of class " + beanType
						+ "found.");
			} else {
				throw new RuntimeException("Multiple beans of class "
						+ beanType + " found: " + beans + ".");
			}
		}

		@SuppressWarnings("rawtypes")
		Bean myBean = (Bean) beans.iterator().next();

		return manager.getReference(myBean, beanType,
				manager.createCreationalContext(myBean));
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> T lookup(Class<T> beanClass, Annotation... qualifiers) {
		return (T) lookup((Type) beanClass, qualifiers);
	}

	public static Object lookup(Type beanType, Annotation... qualifiers) {
		return lookup(getBeanManager(), beanType, qualifiers);
	}

	@SuppressWarnings({ "unchecked" })
	public static <T> T lookup(BeanManager manager, String name) {
		Set<?> beans = manager.getBeans(name);
		if (beans.size() != 1) {
			if (beans.size() == 0) {
				throw new RuntimeException("No beans with name " + name
						+ "found.");
			} else {
				throw new RuntimeException("Multiple beans with name " + name
						+ " found: " + beans + ".");
			}
		}

		Bean<T> myBean = (Bean<T>) beans.iterator().next();

		return (T) manager.getReference(myBean, myBean.getBeanClass(),
				manager.createCreationalContext(myBean));
	}

	public static <T> T lookup(String name) {
		return Resources.<T> lookup(getBeanManager(), name);
	}

	private static BeanManager getBeanManager() {
		try {
			return (BeanManager) new InitialContext()
					.lookup("java:comp/BeanManager");
		} catch (NamingException e) {
			try {
				return (BeanManager) new InitialContext()
						.lookup("java:app/BeanManager");
			} catch (NamingException e1) {
				throw new RuntimeException(e1);
			}

		}
	}
}

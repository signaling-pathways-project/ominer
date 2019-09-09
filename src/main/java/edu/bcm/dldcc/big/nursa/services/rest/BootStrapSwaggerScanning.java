package edu.bcm.dldcc.big.nursa.services.rest;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import io.swagger.jaxrs.config.BeanConfig;
import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * Servlet to Bootstrap BeanConfig for swagger.json definition
 * You also need restEasy autoScan web.xml context param set
 * @author mcowiti
 *
 */
public class BootStrapSwaggerScanning extends HttpServlet {
    
	private static final long serialVersionUID = -163397908752505039L;

	@Inject
    @ConfigProperty(name="swagger.url")
    private String swaggerUrl;

	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("2.0.0");
        beanConfig.setSchemes(new String[]{"http","https"});
        beanConfig.setDescription("SignalingPathway Project API RESTful Interface");
        beanConfig.setHost(swaggerUrl);
        beanConfig.setBasePath("/rest");
        //only show current public
        beanConfig.setResourcePackage("edu.bcm.dldcc.big.nursa.services.rest.pubapi.current");
        //TODO if having jackson errors if disable scan
        beanConfig.setScan(true);
        beanConfig.setPrettyPrint(true);
        
    }
}

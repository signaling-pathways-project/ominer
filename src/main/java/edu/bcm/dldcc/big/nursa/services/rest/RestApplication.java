package edu.bcm.dldcc.big.nursa.services.rest;

import javax.ws.rs.core.Application;

import javax.ws.rs.ApplicationPath;
import java.util.Set;


/**
 * Expose data via REST
 * @author mcowiti
 *
 */
@ApplicationPath("/rest")
public class RestApplication extends Application
{

   // Set singletons=getSingletons(new ServerCacheFeature());

}
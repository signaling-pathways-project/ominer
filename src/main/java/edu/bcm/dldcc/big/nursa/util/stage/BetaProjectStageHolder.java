package edu.bcm.dldcc.big.nursa.util.stage;

import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.api.projectstage.ProjectStageHolder;


/**
 * Singleton for Beta project stage
 * To use: refer to in META-INF/services
 * @author mcowiti
 *
 */
public class BetaProjectStageHolder implements ProjectStageHolder {

  public static final class Demo extends ProjectStage
  {

    private static final long serialVersionUID = -903550970212353673L;
     
  }

  public static final Demo Demo = new Demo();
}

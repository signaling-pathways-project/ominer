package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.cistromic.PathwayNodesView;
import edu.bcm.dldcc.big.nursa.model.omics.BsmView;
import edu.bcm.dldcc.big.nursa.model.omics.dto.BsmReport;
import edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BSM Rest Service Bean
 * @author mcowiti
 */
public class BsmRestServiceBean implements BsmRestService {

    @Inject
    private BsmServicebean bsmServicebean;

    @Inject
    private RestApiUrlsProducer urls;


    /**
     * BSM info calls on BSM name on click (on y-axis), but this a bit trickly,so it is not yet implemented
     * so allowing multiple symbols since BSM Info is now anchored on the datapoint itself instead of the y-axis BSM name.
     * If we are able to anchor at BSM name, there wont be need for multiple BSMs to the call
     * @param symbol
     * @return
     */
    @Override
    public Response getBsmInfoReport(String symbol) {
        if(symbol == null || symbol.trim().length() ==0)
            return Response.status(400).entity(BsmServicebean.MISSING_BSM_SYMBOL).build();

        boolean multiple=(symbol.contains(",") || symbol.contains(";"));
        List<BsmView> bsms=bsmServicebean.getBsmBySymbol(symbol,multiple);

        if(bsms.size() == 0)
            return Response.status(400).entity(BsmServicebean.MISSING_BSM_SYMBOL_IN_LIST+symbol).build();

        List<BsmView> bsmLists=new ArrayList<BsmView>();
        Map<String, List<BsmView>> byBsm
                = bsms.stream().collect(Collectors.groupingBy(BsmView::getBsm));
        BsmView justOneBsm;
        for(Map.Entry<String, List<BsmView>> entry:byBsm.entrySet()){
            justOneBsm=entry.getValue().get(0);
            if(justOneBsm.getPubchemId()!=null){
                justOneBsm.setPubchemUrl(urls.getNcbiPubchemCompoundLink()+justOneBsm.getPubchemId());
            }
            if (justOneBsm.getIuphar() != null) {
                StringBuilder sb = new StringBuilder(urls.getIupharLink());
                justOneBsm.setUrl(sb.append(justOneBsm.getIuphar()).toString());
            }
            bsmLists.add(justOneBsm);
        }

        Set<String> nodes= new HashSet<String>();
        for(BsmView absm:bsms)
            nodes.add(absm.getNode());

        List<PathwayNodesView> lpathways=bsmServicebean.getExperimentPathways(nodes);

        Map<Long, List<PathwayNodesView>> byPaths
                = lpathways.stream().collect(Collectors.groupingBy(PathwayNodesView::getFamilyid));

        List<PathwayNodesView> pathways=new ArrayList<PathwayNodesView>();
        PathwayNodesView path=null;
        for(Map.Entry<Long, List<PathwayNodesView>> entry:byPaths.entrySet()){
            path=entry.getValue().get(0);
            bsmServicebean.collectNodes(path,entry.getValue());
            pathways.add(path);
        }

        return Response.ok().entity(new BsmReport(bsmLists,pathways)).build();
    }
}

package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.services.TranscriptomineService;
import edu.bcm.dldcc.big.nursa.services.rest.local.transscriptomine.TranscriptomineQueryServiceRestApi;
import edu.bcm.dldcc.big.nursa.services.utils.FileHelper;
import edu.bcm.dldcc.big.nursa.services.utils.InputStreamWithFileDeletion;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Porting Citation  Management functions
 * @author amcowiti
 */
@Stateless
public class CitationsBean implements CitationsService {

    private static Logger log = Logger.getLogger(CitationsBean.class.getName());

    @Context
    private ServletContext context;

    @Inject
    private TranscriptomineService omineService;

    @Inject
    private DatasetServiceBean datasetServiceBean;

    @Inject
    private FileHelper fileHelper;


    @Override
    public Response downloadDatasetAsEndNote(String datasetId) {
        InputStream targetStream = null;//context.getResourceAsStream("/WEB-INF/citation.ris");
        File f = null;
        try {
            NURSADataset dset = this.datasetServiceBean.findById(Long.parseLong(datasetId));
            if (dset != null) {
                f = this.fileHelper.generateRisFile(dset);
                targetStream = new InputStreamWithFileDeletion(f);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Cannot write results dataset to the temp file. Will send the error file", e);
            targetStream = context.getResourceAsStream("/WEB-INF/citation.ris");
        }

        Response.ResponseBuilder response = Response.ok(targetStream);
        response.header("Content-Disposition", "attachment; filename=" + "citation" + ".ris");
        return response.build();
    }
}

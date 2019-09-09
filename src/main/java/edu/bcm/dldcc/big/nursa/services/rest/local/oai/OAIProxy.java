package edu.bcm.dldcc.big.nursa.services.rest.local.oai;

import com.lyncode.xml.exceptions.XmlWriteException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.apache.log4j.Logger;
import org.dspace.xoai.dataprovider.DataProvider;
import org.dspace.xoai.dataprovider.exceptions.OAIException;
import org.dspace.xoai.dataprovider.model.MetadataFormat;
import org.dspace.xoai.dataprovider.parameters.OAIRequest;
import org.dspace.xoai.dataprovider.repository.InMemorySetRepository;
import org.dspace.xoai.dataprovider.repository.Repository;
import org.dspace.xoai.dataprovider.repository.RepositoryConfiguration;
import org.dspace.xoai.model.oaipmh.DeletedRecord;
import org.dspace.xoai.model.oaipmh.Granularity;
import org.dspace.xoai.model.oaipmh.OAIPMH;
import org.dspace.xoai.xml.XmlWriter;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by alexey on 10/12/15.
 * @author mcowiti
 */

@Stateless
@Path("/oai")
@Api(value = "/oai/",tags="Datasets by OAI(OpenAchieves Initiative) (used by TReuters)")
public class OAIProxy {
    private static Logger log = Logger.getLogger(OAIProxy.class);
    private SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @PersistenceContext(unitName = "NURSA")
    private EntityManager entityManager;

    private static TransformerFactory factory = TransformerFactory.newInstance();

    //@ApiSecured
    @GET
    @Path("/")
    @ApiOperation(value = "Provide OAI compliant Datasets Items.",
    notes = "Dateset Repo, Provide OAI compliant Datasets Items.")
    @ApiResponses(value = { 
	@ApiResponse(code = 200, message="OAI Data Successful"),
	@ApiResponse(code = 500, message = "Internal Server Error", response = String.class)})
    public Response handelRequest(@Context UriInfo uriInfo) {
    	
        org.dspace.xoai.dataprovider.model.Context context = org.dspace.xoai.dataprovider.model.Context.context();

        InputStream xslt = OAIProxy.class.getClassLoader().getResourceAsStream("oai_dc.xsl");

        MetadataFormat dcFormat = null;
        try {
            dcFormat = MetadataFormat.metadataFormat("oai_dc").withTransformer(factory.newTransformer(new StreamSource(xslt)));
        } catch (TransformerConfigurationException e) {
            log.error("Failed to read a transformation file."+e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error while reading transformation file").build();
        }

        context.withMetadataFormat(dcFormat);

        RepositoryConfiguration configuration = new RepositoryConfiguration();

        configuration
                .withRepositoryName("NURSA")
                .withBaseUrl("https://nursa.org/nursa/rest/oai")
                .withAdminEmail("Alexey.Naumov@bcm.edu")
                .withEarliestDate(getFirstDate())
                .withDeleteMethod(DeletedRecord.NO)
                .withMaxListRecords(100)
                .withMaxListSets(100)
                .withMaxListIdentifiers(100)
                .withGranularity(Granularity.Second);

        Repository nursaRepository = Repository.repository()
                                            .withItemRepository(new ItemRepositoryNursa(entityManager))
                                            .withConfiguration(configuration)
                                            .withSetRepository(new InMemorySetRepository().doesNotSupportSets());

        DataProvider nursaDataProvider = new DataProvider(context,nursaRepository);

        OAIRequest request = new OAIRequest(uriInfo.getQueryParameters());

        OAIPMH oaipmh = null;
        try {
            oaipmh = nursaDataProvider.handle(request);
        } catch (OAIException e) {
            log.error("Error while processing the request: "+uriInfo.getQueryParameters(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error while processing request").build();
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        XmlWriter writer = null;
        try {
            writer = new XmlWriter(stream);
            writer.writeStartDocument();
            writer.write(oaipmh);
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            log.error("Unexpected error while generating xml for request: " + uriInfo.getQueryParameters(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error while generating xml.").build();
        } catch (XmlWriteException e) {
            log.error("Unexpected error while writing the output for request: " + uriInfo.getQueryParameters(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unexpected error while writing the output.").build();
        }

        String xmlToHack = stream.toString();
        //=====================================================================
        // if we have metadata in oai_dc format than we manually have to fix xml
        // this bug is hapend due to the problem how DSpace/lyncode implemented xml writing
        // this potentially can cause problems for xmls with huge metadata, due to dum in memory string manipulation
        // in order to fix this, need to fix xoai library or implement xml writing on our own.
        // ========================================================================
        if(xmlToHack.contains("oai_dc:dc")) {
            xmlToHack = xmlToHack.replace("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"", "");
            xmlToHack =  xmlToHack.replace("<oai_dc:dc", "<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        }

        Response.ResponseBuilder response = Response.ok((Object) xmlToHack);
        response.header("Content-Type", "application/xml");
        return response.build();
    }

    private Date getFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, Calendar.MARCH);
        cal.set(Calendar.DAY_OF_MONTH, 28);
        return cal.getTime();
    }
}

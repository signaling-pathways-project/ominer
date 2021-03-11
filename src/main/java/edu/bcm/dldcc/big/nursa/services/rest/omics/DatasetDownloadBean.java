package edu.bcm.dldcc.big.nursa.services.rest.omics;

import edu.bcm.dldcc.big.nursa.data.SQL;
import edu.bcm.dldcc.big.nursa.services.utils.InputStreamWithFileDeletion;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;

import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dynamic file downloads Bean
 * @author amcowiti
 */
@Stateless
public class DatasetDownloadBean {

    private static Logger log = Logger.getLogger(DatasetDownloadBean.class.getName());

    @PersistenceContext(unitName = "NURSA")
    private EntityManager em;

    @Context
    private ServletContext context;



    public Integer getNumberOfDatasetRows(Integer id,boolean isTx){
        String sql=(isTx)?"select count(*) from DOWNLOAD_DATASET_TX where id=:id":
                "select count(*) from DOWNLOAD_DATASET_CIS where id=:id";
        Query countDatasets = em.createNativeQuery(sql);
        countDatasets.setParameter("id", id);
        return  ((BigDecimal) countDatasets.getSingleResult()).intValueExact();
    }

    //@AccessTimeout(value = 2, unit = TimeUnit.MINUTES) @Asynchronous
    public Response  downloadPagedData(Integer id,long totalRows,
            final int pageSize,
            final int pages,
            final Integer startPage,final boolean isTranscriptomic){

        String sql=(isTranscriptomic)?"select name, symbol,foldchange,pvalue "+
                " from  ( select  rownum rn, name,symbol,foldchange,pvalue "+
                "from  DOWNLOAD_DATASET_TX  where id=:id and rownum <=:strt ) "+
                "where rn > :nxt":
                "select name, symbol,score "+
                        " from  ( select  rownum rn, name,symbol,score "+
                        "from  DOWNLOAD_DATASET_CIS  where id=:id and rownum <=:strt ) "+
                        "where rn > :nxt";

        long b=System.currentTimeMillis();

        StatelessSession session=null;
        InputStream targetStream = null;
        String filename=null;
        long size=0L;
        File f=null;
        try{
            session=(em.getEntityManagerFactory().unwrap(SessionFactory.class))
                    .openStatelessSession();

            filename=(id>100)?Long.toString(id):Long.toString(id)+"__";
            f = File.createTempFile(filename,".tsv");
            PrintWriter dos = new PrintWriter(f);

            int strt=0;
            int nxt=0;
            List<Object[]> all= new ArrayList<>();
            for(int i=0;i<pages;i++){
                strt=startPage+(pageSize*(i+1));
                nxt=startPage+(i*pageSize);
                List<Object[]> list=(List<Object[]>)session.createSQLQuery(sql)
                        .setParameter("strt", strt)
                        .setParameter("nxt", nxt)
                        .setParameter("id",id)
                        .list();

                if(list== null || list.size()==0){
                    log.log(Level.SEVERE,"No dataset information for id "+id);
                    break;
                }
                log.log(Level.FINE,"page "+i+" read in ms=>"+(System.currentTimeMillis()-b));
                all.addAll(list);
            }

            if((System.currentTimeMillis()-b)>20000)
                log.log(Level.WARNING,"Too long rows read time. All rows / in ms=>"+totalRows+"/" +(System.currentTimeMillis()-b));

            if(all.size() ==0)
                none(isTranscriptomic,dos);
            else {
                if (isTranscriptomic)
                    txData(all, dos);
                else
                    cisData(all, dos);
            }

            targetStream = new InputStreamWithFileDeletion(f);
            dos.close();
            size=f.length();

            if((System.currentTimeMillis()-b)>20000)
                log.log(Level.WARNING,"# Rows/File/Size => "+totalRows+"/"+filename+"/"+ size +"(B), created for download in(ms) " +(System.currentTimeMillis()-b));

            Response.ResponseBuilder response = Response.ok((Object) targetStream);
            response.header("Content-Disposition","attachment; filename=" + Long.toString(id) + ".tsv");
            return response.build();

        }catch (IOException e){
            //targetStream = context.getResourceAsStream("/WEB-INF/error.xls");
            log.log(Level.SEVERE,"Error Printing Tab Delimited File");
            e.printStackTrace();
        }finally{
            session.close();
            try{
                f.delete();
            }catch (Exception ex){
                f.deleteOnExit();
            }
        }
        return  Response.noContent().build();
    }

    /**
     * NB: some names have newlines
     * @param list
     * @param dos
     */
    private void txData(List<Object[]> list,PrintWriter dos){
        dos.println("Experiment Name\tSymbol\tFold Change\tp-value\t");
        for (Object[] data: list)
        {
            dos.print(((String)data[0]).replace("\n", "").replace("\r", "")+"\t");
            dos.print(data[1]+"\t");
            dos.print(data[2]+"\t");
            dos.print(data[3]+"\t");
            dos.println();
        }
    }

    private void cisData(List<Object[]> list,PrintWriter dos){
        dos.println("Experiment Name\tSymbol\tBinding Score\t");
        for (Object[] data: list)
        {
            dos.print(((String)data[0]).replace("\n", "").replace("\r", "")+"\t");
            dos.print(data[1]+"\t");
            dos.print(data[2]+"\t");
            dos.println();
        }
    }

    private void none(boolean isTx,PrintWriter dos){
        if(isTx)
            dos.println("Experiment Name\tSymbol\tFold Change\tp-value\t");
        else
            dos.println("Experiment Name\tSymbol\tBinding Score\t");

        dos.print("No dataset information found\t");
        dos.println();
    }
}

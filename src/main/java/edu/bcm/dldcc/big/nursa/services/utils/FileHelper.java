package edu.bcm.dldcc.big.nursa.services.utils;

import edu.bcm.dldcc.big.nursa.model.omics.*;
import edu.bcm.dldcc.big.nursa.model.omics.dto.OmicsDatapoint;
import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SimpleQueryForm;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.ExpAnnotationViewEntity;
import edu.bcm.dldcc.big.nursa.services.PubmedAbstractBean;
import edu.bcm.dldcc.big.nursa.services.rest.omics.CitationsBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by alexey on 4/6/15.
 */

@Stateless
public class FileHelper implements Serializable
{
    private static Logger log = Logger.getLogger(CitationsBean.class.getName());

    @Inject
    private PubmedAbstractBean pubmedAbstractBean;

   // @Inject private ReferenceUtil referenceUtil;


    private static final int numberOfTranscriptomicColumns = 9;//10
    private static final int numberOfCistromicColumns = 7;

    public  File generateTranscriptomicQueryExelBook(List<OmicsDatapoint> datapointList, SimpleQueryForm queryForm, Integer maxNumber) throws IOException
    {
        File f = File.createTempFile(Long.toString(System.currentTimeMillis()),".xls");

        SXSSFWorkbook book = new SXSSFWorkbook(100);
        book.setCompressTempFiles(true);
        SXSSFSheet sheet = (SXSSFSheet) book.createSheet("Data");

        // Row indexes
        int idx = 0;

        // Create header
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, numberOfTranscriptomicColumns));
        Row title = sheet.createRow(idx++);
        Cell headerCell = title.createCell(0);
        headerCell.setCellValue(queryForm.toString());

        // insert empty line
        sheet.createRow(idx++);

        if (datapointList.size() == 0 || datapointList.size() >= maxNumber) {
            boolean none=(datapointList.size() == 0);
            toManyResultsMsg(sheet, none,maxNumber,numberOfTranscriptomicColumns);
        } else {
            addTranscriptomicHeaders(sheet.createRow(idx++));
            for (OmicsDatapoint datapoint : datapointList) {
                Row row = sheet.createRow(idx++);
                fillTranscriptomicRow(row, datapoint);
            }
        }

        FileOutputStream out = new FileOutputStream(f);
        book.write(out);
        out.close();
        return f;
    }
    
    public  File generateCistromicQueryExelBook(List<OmicsDatapoint> datapointList,
    		SimpleQueryForm queryForm, 
    		Integer maxNumber) throws IOException{
    	
        File f = File.createTempFile(Long.toString(System.currentTimeMillis()),".xls");

        SXSSFWorkbook book = new SXSSFWorkbook(100);
        book.setCompressTempFiles(true);
        SXSSFSheet sheet = (SXSSFSheet) book.createSheet("Data");

        int idx = 0;

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, numberOfCistromicColumns));
        Row title = sheet.createRow(idx++);
        Cell headerCell = title.createCell(0);
        headerCell.setCellValue(queryForm.toString());

        sheet.createRow(idx++);

        if (datapointList.size() == 0 || datapointList.size() >= maxNumber) {
            boolean none=(datapointList.size() == 0);
            toManyResultsMsg(sheet, none,maxNumber,numberOfCistromicColumns);
        } else {
            addCistromicHeaders(sheet.createRow(idx++));

            for (OmicsDatapoint datapoint : datapointList) {
                Row row = sheet.createRow(idx++);
                fillCistromicRow(row, datapoint);
            }
        }

        FileOutputStream out = new FileOutputStream(f);
        book.write(out);
        out.close();
        return f;
    }

    private  void addTranscriptomicHeaders(Row row)
    {
        int index = 0;
        fillCell(row, index++,"Experiment Name");
        fillCell(row, index++,"Species");
        fillCell(row, index++,"Tissue");
        fillCell(row, index++,"Probe");
        fillCell(row, index++,"Symbol");
        fillCell(row, index++,"Fold Change");
        fillCell(row, index++,"P-value");
        fillCell(row, index++,"BSM");
    }
    
    private  void addCistromicHeaders(Row row)
    {
        int index = 0;
        fillCell(row, index++,"Experiment Name");
        fillCell(row, index++,"Species");
        fillCell(row, index++,"Tissue");
        fillCell(row, index++,"Symbol");
        fillCell(row, index++,"Binding Score");
        fillCell(row, index++,"IPAGS|BSM|Other AGS");
    }


    private  void fillTranscriptomicRow(Row row, OmicsDatapoint datapoint) {

        if(null == datapoint)
            return;

        int index = 0;

        fillCell(row, index++, datapoint.getExperimentName() );
        fillCell(row, index++, datapoint.getSpeciesCommonName());
        fillCell(row, index++, datapoint.getTissue());
        fillCell(row, index++, datapoint.getProb());
        fillCell(row, index++, datapoint.getSymbol());
        fillCell(row, index++, datapoint.getFoldChange());
        if (null != datapoint.getPvalue()) {
            fillCell(row, index++, datapoint.getPvalue());
        }else
            fillCell(row, index++, "");

        fillCell(row, index++, datapoint.getBsms());

    }
    
    private  void fillCistromicRow(Row row, OmicsDatapoint datapoint) {

        if(null == datapoint)
            return;

        int index = 0;

        fillCell(row, index++, datapoint.getExperimentName() );
        fillCell(row, index++, datapoint.getSpeciesCommonName());
        fillCell(row, index++, datapoint.getTissue());
        fillCell(row, index++, datapoint.getSymbol());
        fillCell(row, index++, datapoint.getScore());
        
        fillCell(row, index++, datapoint.getMolecules()!=null ? getBsmAndNodes(datapoint) : "");
        
    }
    
    /**
     * Might need to include BSM/Other
     * @param data
     * @return
     */
    private String getBsmAndNodes(OmicsDatapoint data){
    	StringBuilder sb= new StringBuilder(data.getMolecules());
    	if(data.getBsms() != null && data.getBsms().trim().length() >0)
    		sb.append("|").append(data.getBsms());
    	if(data.getOmolecules()!=null && data.getOmolecules().trim().length() >0)
    		sb.append("|").append(data.getOmolecules());
    	
    	return sb.toString().replaceAll(",", ";");
    }
    
    private  void fillCell(Row r, int index, String value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }

    private  void fillCell(Row r, int index, Double value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }
    
    private  void fillCell(Row r, int index, Integer value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }


    private  String regMolString(List<ExpAnnotationViewEntity> list)
    {
        StringBuilder results = new StringBuilder();
        String delimiter = "";
        for(ExpAnnotationViewEntity annotation : list)
        {
            if (null != annotation.getMolsynonName())
            {
                results.append(delimiter);
                results.append(annotation.getMolsynonName());
                delimiter = ";";
            }
        }
        return results.toString();
    }

    private  String timeOfTreatmentToString(List<ExpAnnotationViewEntity> annotations)
    {
        StringBuilder result = new StringBuilder();
        String delimiter = "";
        for (ExpAnnotationViewEntity annotation : annotations)
        {
            if(null != annotation.getTimehours()&& !"".equals(annotation.getTimehours())) {
                result.append(delimiter);
                delimiter = ";";
                result.append(annotation.getMolsynonName());
                result.append("|");
                result.append(annotation.getTimehours());
                result.append(" ");
                result.append(annotation.getTimeunit());
            }
        }
        return result.toString();
    }


    private  String concentrationToString(List<ExpAnnotationViewEntity> annotations)
    {
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (ExpAnnotationViewEntity annotation: annotations)
        {
            if (null != annotation.getQuantity()&& ! "".equals(annotation.getQuantity())) {
                sb.append(delimiter);
                delimiter = ";";
                sb.append(annotation.getMolsynonName());
                sb.append(" | ");
                sb.append(annotation.getQuantity());
                sb.append(" ");
                sb.append(annotation.getQuantityUnit());
            }
        }
        return sb.toString();
    }

   private  void toManyResultsMsg(SXSSFSheet sheet, boolean none,int maxNumber,int number) {
       sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, number));
       Row title = sheet.createRow(1);
       if(!none)
           fillCell(title, 0, "Your query returned too many results, the max number or results that you can download is "+maxNumber);
       else
           fillCell(title, 0, "Your query returned no  results, please relax the criteria  ");
   }

    private  void setAuthorAndYear(String authors, String year, RisBuilder builder)
    {
        builder.setPY(year);
        String[] authorsArray = authors.split(",");
        for (String author : authorsArray) {
            builder.addAU(author.trim());
        }
    }

    //FIXME need to change the NURSA references to SPP
    public  File generateRisFile(NURSADataset dset) throws IOException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        RisBuilder builder = new RisBuilder();
        builder.setTY("DATA")
               .setA2("Signaling Pathways Project")
               .setDB("Signaling Pathways Project")
               .setDP("Signaling Pathways Project")
               .setPB("Signaling Pathways Project")
               .setLA("English")
               .setY2(dateFormat.format(new Date()));

        String ab = dset.getDescription();
        if (checkStr(ab)) {
            builder.setAB(newLinesCleanUp(ab));
        }

        String dsetDoi = dset.getDoi().getDoi();
        if (checkStr(dsetDoi)) {
            builder.setAN("dx.doi.org/"+dsetDoi);
            builder.setDO(dsetDoi);
            builder.setUR("dx.doi.org/" + dsetDoi);
        }

        //Reference r = null;
        String pubmed=null;
        if(dset.getReference()!=null){
            //BsmView bsmView=dset.getReferencesByBsm().get(0);
            //pubmed=(bsmView.getEvidenceType()== BsmView.EvidenceType.pubmed)?bsmView.getEvidence():null;
            pubmed=dset.getReference().getPubmedId();
        }

        boolean hasContributors=false;
        //TODO Apollo FR 10.2016, either not both, need consoldate with NURSADataset#getDatasetCitation
        if ( null != dset.getRepo() && dset.getRepo().startsWith("GSE"))
        {
            String authors = dset.getContributors();
            hasContributors=(authors.length() >1 && dset.getPublished()!=null);
            if(hasContributors)
            	setAuthorAndYear(authors, dateFormat.format(dset.getPublished()), builder );
        }
        /* pre Feb 2018
        if(null != dset.getReferences() && dset.getReferences().size() >0) {
            r = dset.getReferences().get(0);
            String authors = r.getArticle().getAuthorsList();
            if(!hasContributors)
            	setAuthorAndYear(authors, r.getArticle().getPublishYear(), builder);
        }*/
        if( pubmed != null) {

            String authors=pubmedAbstractBean.getCitationAuthors(dset.getContributors());
            //authors=referenceUtil.getCitationAuthors(dset.getContributors());

            if(!hasContributors)
                setAuthorAndYear(authors, pubmedAbstractBean.findArticle(pubmed).getPublishYear(), builder);
        }


        builder.addKW("Signaling Pathways Project");
        builder.addKW("Transcriptomine");
        builder.addNote("This is a secondary derivative version of the original dataset, the details of which are as follows:");

        Set<String> probTypes = new HashSet<String>();
        for (Experiment experiment : dset.getExperiments()) {
            builder.addKW(experiment.getTissueSource().getName());

            /*FIXME we are removing these
            for (ExperimentBaseAnnotation annotation : experiment.getAnnotations()) {
                MoleculeTreatment treatment = (MoleculeTreatment) annotation;
                if (null != treatment && null != treatment.getMolecule() && null != treatment.getMolecule().getName() && null != treatment.getMolecule().getName().getName()) {
                    builder.addKW(treatment.getMolecule().getName().getName());
                }
            }*/

            String platform = experiment.getPlatformName();
            if (checkStr(platform)) {
                probTypes.add(experiment.getPlatformName());
            }
        }

        if (probTypes.size() >0) {
            StringBuilder note= new StringBuilder("Primary Dataset Platform: ");
            String prefix = "";
            for (String type : probTypes) {
                note.append(prefix);
                prefix = ", ";
                note.append(type);
            }
            builder.addNote(note.toString());
        }
        String repo = dset.getRepo();

        if (checkStr(repo)) {
            builder.setET("This is version 1.0 of an annotated derivative of the original dataset, which can be found in "+repo);

            builder.addNote("Primary Dataset Repository: "+repoToSource(repo))
                    .addNote("Primary Dataset Repository Accession: ="+repo);
        }

        if (pubmed != null && checkStr(pubmed)) {
            builder.addNote("Primary Dataset Journal Article Title: "+newLinesCleanUp(pubmedAbstractBean.findArticle(pubmed).getArticleTitle()))
                    .addNote("Primary Dataset PMID: "+pubmed);
        }
        builder.addNote("Licensing: Use of this dataset is governed by a Creative Commons Attribution 3.0 license, which provides for sharing, adaptation and both non-commercial and commercial re-use, as long as this dataset is cited.");

        builder.addNote("Persistence: SignalingPathway Project  is committed to maintaining persistent identifiers that will continue to resolve to a landing page providing metadata describing the data, including elements of stewardship, provenance, and availability.");

        DateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
        builder.setSE(df.format(dset.getReleaseDate()));

        builder.setTI(dset.getName());


        File f = File.createTempFile(Long.toString(System.currentTimeMillis()),".ris");


        FileOutputStream fop = new FileOutputStream(f);
        if (!f.exists()) {
            f.createNewFile();
        }

        byte[] contentInBytes = builder.generateString().getBytes();

        fop.write(contentInBytes);
        fop.flush();
        fop.close();
        return f;
    }

    private  String repoToSource(String repo) {
        String dataSource =null;
        if(repo.startsWith("GEO")){
            dataSource= "GEO";
        }else if(repo.startsWith("E")){
            dataSource= "ArrayExpress";
        }else if(repo.startsWith("NURSA") || repo.contains("10.1621")){
            return "NURSA.";
        }
        return dataSource;
    }

    private  String newLinesCleanUp(String str) {
        if (null == str) {
            return "";
        }
        return str.replace("\r\n", "").replace("\n","");
    }

    private  boolean checkStr(String str) {
        return null != str && !"".equals(str);
    }
}

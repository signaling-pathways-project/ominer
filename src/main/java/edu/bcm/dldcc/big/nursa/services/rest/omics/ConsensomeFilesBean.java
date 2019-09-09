package edu.bcm.dldcc.big.nursa.services.rest.omics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;

import edu.bcm.dldcc.big.nursa.model.omics.Consensome;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import edu.bcm.dldcc.big.nursa.model.cistromic.CisConsensome;
import edu.bcm.dldcc.big.nursa.model.transcriptomic.dto.ConsensomeData;

/**
 * Current Consensome File download bean
 * For archived data, see other mongodb-based methods
 * @author mcowiti
 */

@Stateless
public class ConsensomeFilesBean {

    private final static int NUMBER_TX_COLUMNS=11; // after remove familyId row 12
    private final static int NUMBER_SRX_COLUMNS=9;// 9 after removing Qvalue 10 after remove familyId init=11;

	public File generateTranscriptomicConsensomeExcelData(String query, String doi, List<Consensome> list, Integer maxNumber) throws IOException
    {
        File f = File.createTempFile(Long.toString(System.currentTimeMillis()),".xls");

        SXSSFWorkbook book = new SXSSFWorkbook(100);
        book.setCompressTempFiles(true);
        SXSSFSheet sheet = (SXSSFSheet) book.createSheet("Consensome Data");

        int rowIndx = 0;


        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, NUMBER_TX_COLUMNS));
        Row title = sheet.createRow(rowIndx++);
        Cell headerCell = title.createCell(0);
        headerCell.setCellValue(query);

        sheet.createRow(rowIndx++);

        if (list.size() == 0 || list.size() >=maxNumber) {
            toManyResultsMsg(sheet, maxNumber,NUMBER_TX_COLUMNS);
        } else {
        	 addTxHeaders(sheet.createRow(rowIndx++));
            for (Consensome data : list) {
                Row row = sheet.createRow(rowIndx++);
                fillTxRow(row, data,doi);
            }
        }

        FileOutputStream out = new FileOutputStream(f);
        book.write(out);
        out.close();
        return f;
    }
	
	public File generateCistromicConsensomeExcelData(String query, String doi,
                                                     List<CisConsensome> list, Integer maxNumber) throws IOException
    {
        File f = File.createTempFile(Long.toString(System.currentTimeMillis()),".xls");

        SXSSFWorkbook book = new SXSSFWorkbook(100);
        book.setCompressTempFiles(true);
        SXSSFSheet sheet = (SXSSFSheet) book.createSheet("Consensome Data");

        int rowIdx = 0;

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, NUMBER_SRX_COLUMNS));
        Row title = sheet.createRow(rowIdx++);
        Cell headerCell = title.createCell(0);
        headerCell.setCellValue(query);

        //empty why?
        sheet.createRow(rowIdx++);

        if (list.size() == 0 || list.size() >=maxNumber) {
            toManyResultsMsg(sheet, maxNumber,NUMBER_SRX_COLUMNS);
        } else {
        	 addCisHeaders(sheet.createRow(rowIdx++));
            for (CisConsensome data : list) {
                Row row = sheet.createRow(rowIdx++);
                fillCisRow(row, data,doi);
            }
        }

        FileOutputStream out = new FileOutputStream(f);
        book.write(out);
        out.close();
        return f;
    }

	 private void toManyResultsMsg(SXSSFSheet sheet, int maxNumber,int numberOfColumns) {
		 
	       sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, numberOfColumns));
	       Row title = sheet.createRow(1);
	        fillCell(title, 0, "Your query returned no data or too many results, the max number or results that you can download is "+maxNumber);
     }
	 
    private void addTxHeaders(Row row)
    {
        int index = 0;
        fillCell(row, index++,"ID");
        //fillCell(row, index++,"FamilyID");
        fillCell(row, index++,"Family");
        fillCell(row, index++,"Physiological System");
        fillCell(row, index++,"Organ");
        fillCell(row, index++,"Species");
        fillCell(row, index++,"Gene");
        fillCell(row, index++,"Percentile");
        fillCell(row, index++,"Discovery Rate");
        fillCell(row, index++,"GMFC");
        fillCell(row, index++,"cPValue");
        fillCell(row, index,"DOI");
    }
    
    private void addCisHeaders(Row row)
    {
        int index = 0;
        fillCell(row, index++,"ID");
        //fillCell(row, index++,"FamilyID");
        fillCell(row, index++,"Family");
        fillCell(row, index++,"Physiological System");
        fillCell(row, index++,"Organ");
        fillCell(row, index++,"Species");
        fillCell(row, index++,"Gene");
        fillCell(row, index++,"averageScore"); 
        //fillCell(row, index++,"qValue");
        fillCell(row, index++,"percentile");
        fillCell(row, index,"DOI");
    }
    
    private void fillCisRow(Row row, CisConsensome data,String doi) {

        if(null == data)
            return;

        int index = 0;

        fillCell(row, index++, data.getId() );
        //fillCell(row, index++, data.getKey().getFamilyId());
        fillCell(row, index++, data.getKey().getFamily());
        fillCell(row, index++, data.getKey().getPhysiologicalSystem());
        fillCell(row, index++, data.getKey().getOrgan());
        fillCell(row, index++, data.getKey().getSpecies());
        fillCell(row, index++, data.getGene());
        fillCell(row, index++, data.getAverageScore());
        //fillCell(row, index++, data.getqValue());
        fillCell(row, index++, data.getPercentile());
        fillCell(row, index, doi);
    }
    

    private void fillTxRow(Row row, Consensome data,String doi) {

        if(null == data)
            return;

        int index = 0;

        fillCell(row, index++, data.getId() );
        //fillCell(row, index++, data.getKey().getFamilyId());
        fillCell(row, index++, data.getKey().getFamily());
        fillCell(row, index++, data.getKey().getPhysiologicalSystem());
        fillCell(row, index++, data.getKey().getOrgan());
        fillCell(row, index++, data.getKey().getSpecies());
        fillCell(row, index++, data.getGene());
        fillCell(row, index++, data.getPercentile());
        fillCell(row, index++, data.getDiscrate());
        fillCell(row, index++, data.getGmFc());
        fillCell(row, index++, data.getCPValue());
        fillCell(row, index, doi);
        
    }
    
    private void fillCell(Row r, int index, String value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }
    private void fillCell(Row r, int index, Integer value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }

    private void fillCell(Row r, int index, Double value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }

    private void fillCell(Row r, int index, Long value) {
        Cell c = r.createCell(index);
        c.setCellValue(value);
    }

}
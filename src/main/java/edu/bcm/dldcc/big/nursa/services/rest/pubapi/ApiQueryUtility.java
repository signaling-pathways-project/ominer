package edu.bcm.dldcc.big.nursa.services.rest.pubapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetApiQueryDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetMinimalDTO;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetApiQueryDTO.APIQueryMode;
import edu.bcm.dldcc.big.nursa.model.omics.dto.DatasetApiQueryDTO.LigandScheme;

public class ApiQueryUtility {

public static DatasetApiQueryDTO getDatasetApiQueryDTO(Integer geneId, String symbol, String ligandSource, String ligandSourceId){
		
		DatasetApiQueryDTO query= new DatasetApiQueryDTO();
		if(symbol != null){
			query.geneSymbol=symbol;
			query.apiQueryMode=APIQueryMode.symbol;
			return query;
		}
		if(geneId != null){
			query.geneId=geneId;
			query.apiQueryMode=APIQueryMode.geneid;
			return query;
		}
		if(ligandSource != null){
			query.ligandId=ligandSource;
			query.ligandScheme=LigandScheme.getLigandScheme(ligandSourceId);
			query.apiQueryMode=APIQueryMode.ligand;
			return query;
		}
			
		return null;
	}

public static List<DatasetMinimalDTO> getDatasetSample(){
	List<DatasetMinimalDTO> lt= new ArrayList<DatasetMinimalDTO>();
	lt.add(new DatasetMinimalDTO("doi1","name1", "description1", new Date()));
	lt.add(new DatasetMinimalDTO("doi2","name2", "description2", new Date()));
	return lt;
}

}

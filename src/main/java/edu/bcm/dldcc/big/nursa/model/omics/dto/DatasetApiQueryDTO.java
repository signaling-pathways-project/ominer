package edu.bcm.dldcc.big.nursa.model.omics.dto;


/**
 * Dataset Query Object for API query
 * @author mcowiti
 *
 */
public class DatasetApiQueryDTO {

	public String geneSymbol;
	public Integer geneId;
	public String ligandId;
	public APIQueryMode apiQueryMode;
	public LigandScheme ligandScheme;
	
	public enum APIQueryMode{
		symbol,geneid,ligand;
	}
	
	public enum LigandScheme{
		cas("cas"),pubchem("PubChem"),chebi("Chebi"),iuphar("IUPHAR");
		private String scheme;
		
		private LigandScheme(String scheme) {
			this.scheme = scheme;
		}

		public String getScheme() {
			return scheme;
		}

		public static LigandScheme getLigandScheme(String scheme){
			try{
				return LigandScheme.valueOf(scheme);
			}catch (Exception e){
				return null;
			}
		}
	}
}

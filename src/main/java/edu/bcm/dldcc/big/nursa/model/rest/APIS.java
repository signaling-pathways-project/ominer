package edu.bcm.dldcc.big.nursa.model.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Public API urls
 * These need to be set in db
 * If you have a new URL to manage, add it here. The system will pick it up on restatr and add it to db
 * if you need to produce it to be used in the UI, add entry in {@link edu.bcm.dldcc.big.nursa.services.rest.RestApiUrlsProducer}
 * @author mcowiti
 *
 */
public enum APIS {
	
	dx_doi{//DX_DOI_URL
		public String getUrl(){
			return "http://dx.doi.org/";
		}
		public String getReplacementTerm(){return null;}
		public  boolean isAppendId(){return true;}
	},
	sra{//SRA
		public String getUrl(){
			return "https://trace.ncbi.nlm.nih.gov/Traces/sra/?study=";
		}
		public String getReplacementTerm(){return null;}
		public  boolean isAppendId(){return true;}
	},
    geo{
        public String getUrl(){
            return "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=";
        }
        public String getReplacementTerm(){return null;}
        public  boolean isAppendId(){return true;}
    },
dmc_geo{
	public String getUrl(){
		return "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?targ=self&form=xml&view=quick&acc=";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pubmed_download{
	public String getUrl(){
		return "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&retmode=xml&email=support@nursa.org&id=";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pubmed_downloadWithParams{
	public String getUrl(){//params ?db=pubmed&retmode=xml&email=support@nursa.org&id=
		return "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return false;}
	public List<RestParameter> getParams(){
		List<RestParameter> params=new ArrayList<RestParameter>();
		params.add(new RestParameter("db","pubmed"));
		params.add(new RestParameter("retmode","xml"));
		params.add(new RestParameter("email","support@nursa.org"));
		params.add(new RestParameter("id","188"));
		return params;
	}
},
iuphar_link{
	public String getUrl(){
		return "http://www.guidetopharmacology.org/GRAC/LigandDisplayForward?ligandId=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
}
,
chebi_link{
	public String getUrl(){
		return "http://www.ebi.ac.uk/chebi/advancedSearchFT.do?searchString=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
}
,
ncbi_pubchem_link{
	public String getUrl(){
		return "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
    ncbi_pubchem_compund_link{
		public String getUrl(){
			return "https://pubchem.ncbi.nlm.nih.gov/compound/";
		}
		public String getReplacementTerm(){return null;}
		public  boolean isAppendId(){return true;}
	},
ncbi_elsevier_idconv{
	public String getUrl(){
		return "https://www.ncbi.nlm.nih.gov/pmc/utils/idconv/v1.0/?tool=NURSA&email=support@nursa.orgu&format=json&ids=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
ncbi_pubchem_compound_cid_json{
	public String getUrl(){//PUBCHEM FETCH URL
		return "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/{pubchemid}/JSON/?response_type=display";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return false;}
},
ncbi_pubchem_compound_cid_png{
	public String getUrl(){//PUBCHEM PNG URL
		return "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/{pubchemid}/record/PNG/?record_type=2d";
	}
	public String getReplacementTerm(){return "\\{pubchemid\\}";}
	public  boolean isAppendId(){return false;}
},
ncbi_pubchem_compound_cid_imagefly{
	public String getUrl(){//PUBCHEM IMAGEFLY URL
		return "https://pubchem.ncbi.nlm.nih.gov/image/imagefly.cgi?width=175&height=175&cid=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pubmed_link_baseurl{
	public String getUrl(){//PUBMED_LINK_URL
		return "http://www.ncbi.nlm.nih.gov/pubmed/";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},

 pubmed_pmc_link_baseurl{// TODO pmc is in leteratureView
	public String getUrl(){
		return "http://www.ncbi.nlm.nih.gov/pmc/";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},


pharmgkb_pathway_link_baseurl{
	public String getUrl(){//PATHWAY_BASEURL
		return "https://www.pharmgkb.org/pathway/";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_clin_link_baseurl{
	public String getUrl(){
		return "https://www.pharmgkb.org/clinicalAnnotation/";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_label_link_baseurl{//LABEL_BASEURL
	public String getUrl(){
		return "https://www.pharmgkb.org/label/";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_guidelinel_link_baseurl{//GUIDELINE_BASEURL
	public String getUrl(){
		return "https://www.pharmgkb.org/guideline/";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_variant_link_baseurl{
	public String getUrl(){
		return "https://www.pharmgkb.org/rsid/";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},

pharmgkb_pathway{
	public String getUrl(){//PATHWAY_DATA_URL
		return "https://api.pharmgkb.org/v1/data/pathway?";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return false;}
},
pharmgkb_dosage{
	public String getUrl(){//DOSAGE_URL_BY
		return "https://api.pharmgkb.org/v1/data/guideline?";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return false;}
},
pharmgkb_gene_get{
	public String getUrl(){//GURL
		return "https://api.pharmgkb.org/v1/data/gene?symbol=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_molecule_get{
	public String getUrl(){//CHEM_URL
		return "https://api.pharmgkb.org/v1/data/chemical?crossReferences.resource=";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_dosage_single_gene_label{//SINGLE_GENE_DOSAGE_Q_URL
	public String getUrl(){
		return "https://api.pharmgkb.org/v1/data/label?";
	}
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return false;}
},
pharmgkb_clin_single_gene_q{//SINGLE_Q_GENE_URL
	public String getUrl(){
		return "https://api.pharmgkb.org/v1/data/clinicalAnnotation?location.genes.symbol=";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
},
pharmgkb_clin_single_chem_q{//Q_BY_CHEM_URL
	public String getUrl(){
		return "https://api.pharmgkb.org/v1/data/clinicalAnnotation?relatedChemicals.accessionId=";
	}	
	public String getReplacementTerm(){return null;}
	public  boolean isAppendId(){return true;}
};

public abstract String getUrl();
public abstract boolean isAppendId();
public abstract String getReplacementTerm();
public  List<RestParameter> getParams(){ return new ArrayList<RestParameter>();}
}

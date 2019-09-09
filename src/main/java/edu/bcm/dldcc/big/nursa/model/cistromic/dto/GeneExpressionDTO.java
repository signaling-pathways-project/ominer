/**
 * 
 */
package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



/**
 * MicroaArray Expression datapoint DTO  (repalces GeneWsPojo)
 * This useful for API response. For framework with binding such as JSF, use Datapoint is better.
 * @author mcowiti
 */
@XmlRootElement (name = "dataPoint")
public class GeneExpressionDTO {
	
	private String id;
	private Integer geneId; 
	private String symbol;
	private String foldchangeOperator;
	private String foldchangeValue; 
	private String pvalueOperator;
	private String pvalue;
	private String accessionNumber;
	private String identifier;
	private String identifierType;
	
	public GeneExpressionDTO(){
		
	}
	
	public GeneExpressionDTO(String id){
		this(id,null,null,null,null,null,null,null,null);
	}
	
	public GeneExpressionDTO(String id, Integer geneId, String foldchangeOperator,
			String foldchangeValue, String pvalueOperator,
			String pvalue,String symbol,String identifier, String identifierType) {
		this.id=id;
		this.geneId =(geneId!=null)? geneId:TmNone.none.ordinal();
		this.foldchangeOperator =(foldchangeOperator!=null)? foldchangeOperator:TmNone.none.name();
		this.foldchangeValue = (foldchangeValue!=null)?foldchangeValue:TmNone.none.name();
		this.pvalueOperator = (pvalueOperator!=null)?pvalueOperator:TmNone.none.name();
		this.pvalue =(pvalue!=null)? pvalue:TmNone.none.name();
		this.symbol=(symbol!=null)?symbol:TmNone.none.name();
		this.identifier=(identifier != null)?identifier:TmNone.none.name();
		this.identifierType=(identifierType!=null)?identifierType:TmNone.none.name();
	}
	
	
	@XmlElement
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@XmlElement
	public Integer getGeneId() {
		return geneId;
	}
	public void setGeneId(Integer geneId) {
		this.geneId =(geneId!=null)? geneId:TmNone.none.ordinal();
	}
	@XmlElement
	public String getFoldchangeOperator() {
		return foldchangeOperator;
	}
	public void setFoldchangeOperator(String foldchangeOperator) {
		this.foldchangeOperator =(foldchangeOperator!=null)? foldchangeOperator:TmNone.none.name();
	}
	@XmlElement
	public String getFoldchangeValue() {
		return foldchangeValue;
	}
	public void setFoldchangeValue(String foldchangeValue) {
		this.foldchangeValue = (foldchangeValue!=null)?foldchangeValue:TmNone.none.name();
	}
	@XmlElement
	public String getPvalueOperator() {
		return pvalueOperator;
	}
	public void setPvalueOperator(String pvalueOperator) {
		this.pvalueOperator = (pvalueOperator!=null)?pvalueOperator:TmNone.none.name();
	}
	@XmlElement
	public String getPvalue() {
		return pvalue;
	}
	public void setPvalue(String pvalue) {
		this.pvalue =(pvalue!=null)? pvalue:TmNone.none.name();
	}
	@XmlElement
	public String getAccessionNumber() {
		return accessionNumber;
	}
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber =(accessionNumber!=null)? accessionNumber:TmNone.none.name();
	}
	
	
	@XmlElement (name="name")
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol=(symbol!=null)?symbol:TmNone.none.name();
	}

	@XmlElement (name="probeset")
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@XmlElement (name="probesetType")
	public String getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(String identifierType) {
		this.identifierType = identifierType;
	}
	
	
}
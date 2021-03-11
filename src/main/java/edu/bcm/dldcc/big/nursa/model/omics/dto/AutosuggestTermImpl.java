package edu.bcm.dldcc.big.nursa.model.omics.dto;


import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;


/**
 * AutosuggestTerm for auto suggestion elements
 * @author mcowiti
 *
 */
public class AutosuggestTermImpl implements AutosuggestTerm, Comparable<Object>{
	private String identifier;
	private String synonymTerm;
	private String officialSymbol;
    private String type;

    public AutosuggestTermImpl(){

    }

    public AutosuggestTermImpl(String officialSymbol)
    {
        this.officialSymbol = officialSymbol;
    }

    public AutosuggestTermImpl(String term, String symbol){
        this.synonymTerm = (term!=null )?term: TmNone.none.name();
        this.officialSymbol = (symbol!=null )?symbol:TmNone.none.name();
    }

    public AutosuggestTermImpl(String symbol, BigDecimal identifier) {
        this.officialSymbol = (symbol!=null )?symbol:TmNone.none.name();
        this.identifier=(identifier!=null )?identifier.toString():TmNone.none.name();
    }

    public AutosuggestTermImpl(String term, String symbol, String type)
    {
        this.synonymTerm = (term!=null )?term:TmNone.none.name();
        this.officialSymbol = (symbol!=null )?symbol:TmNone.none.name();
        this.type = (type != null)? type:TmNone.none.name();
    }

    public AutosuggestTermImpl(String term, String symbol, String type, BigDecimal identifier) {
        this.synonymTerm = (term!=null )?term:TmNone.none.name();
        this.officialSymbol = (symbol!=null )?symbol:TmNone.none.name();
        this.identifier=(identifier!=null )?identifier.toString():TmNone.none.name();
        this.type = (type != null)? type: TmNone.none.name();
    }


    public int compareTo(Object o) {
		return this.getSynonymTerm().compareTo(((AutosuggestTermImpl)o).getSynonymTerm());
	}
	
    @XmlElement
    public String getSynonymTerm(){
	     return synonymTerm;
	  }
	  

	public void setSynonymTerm(String term) {
		 this.synonymTerm = (term!=null && !term.equals(""))?term:TmNone.none.name();
	}

	
	@XmlElement
	public String getOfficialSymbol() {
		return officialSymbol;
	}

	public void setOfficialSymbol(String symbol) {
		this.officialSymbol = symbol;
	}

	@XmlElement
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

    @XmlElement
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

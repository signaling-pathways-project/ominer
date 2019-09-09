package edu.bcm.dldcc.big.nursa.model.cistromic.dto;

import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;

import java.io.Serializable;
import java.util.Objects;

/**
 * Form represents Cistromic Query parameters
 * @author mcowiti
 *
 */
public class SimpleQueryForm implements Serializable {
	private static final long serialVersionUID = -8540728695507477249L;

	public String omicsType;
	public String geneSearchType;
	public String gene; 
	public String ps;
	public String organ;
	public String tissue;
	public String goTerm;
	public String species; 
	public String pathwayCategory;
	public String doi;
	public String signalingPathway;
	public Integer minScore;
	public Integer maxScore;
	public Double significance;
	public String direction;
	public Double foldChange;
	public Double foldChangeMin;
	public Double foldChangeMax;
	public String bsm;
    public Double percentile;


	public boolean isRange=false;
	public boolean isFindMax=false;

	public String ags;

	public SimpleQueryForm(String omicsType,String doi,Double significance) {
		this.omicsType = omicsType;
		this.doi=doi;
		this.significance=significance;
	}

	public SimpleQueryForm(String omicsType,String doi) {
		this.omicsType = omicsType;
		this.doi=doi;
	}

    public SimpleQueryForm(String omicsType, String queryType,String queryValue,String doi) {
        this.omicsType = omicsType;
        this.geneSearchType=queryType;
        this.gene=queryValue;
        this.doi=doi;
    }

	public SimpleQueryForm(String omicsType, String queryType,String queryValue,String doi,Integer minScore,Integer maxScore) {
		this.omicsType = omicsType;
		this.geneSearchType=queryType;
		this.gene=queryValue;
		this.doi=doi;
		this.minScore=minScore;
		this.maxScore=maxScore;
	}

    public SimpleQueryForm(String omicsType, String geneSearchType, String gene, String doi, String ps, String organ, String tissue,
                           String species, String pathwayCategory, String signalingPathway,
                           Integer minScore, Integer maxScore, String bsm, Double percentile) {
		super();
		this.omicsType = omicsType;
		this.geneSearchType = geneSearchType;
		this.gene = gene;
		this.doi = doi;
		this.ps = ps;
		this.organ = organ;
		this.tissue = tissue;
		this.species = species;
		this.pathwayCategory = pathwayCategory;
		this.signalingPathway = signalingPathway;
		this.minScore=minScore;
		this.maxScore=maxScore;
		this.bsm=bsm;
		this.percentile=percentile;
	}

	public SimpleQueryForm(String omicsType, String geneSearchType, String gene, String doi,
						   String ps, String organ, String tissue,  String species,
						   String pathwayCategory, String signalingPathway,
						   Double significance, Double foldChange, Double foldChangeMin, Double foldChangeMax,
						   String direction,String bsm,Double percentile) {
		this.omicsType = omicsType;
		this.geneSearchType = geneSearchType;
		this.gene = gene;
		this.doi =doi;
		this.ps = ps;
		this.organ = organ;
		this.tissue = tissue;
		this.species = species;
		this.pathwayCategory = pathwayCategory;
		this.signalingPathway = signalingPathway;
		this.significance = significance;
		this.foldChange = foldChange;
		this.foldChangeMin = foldChangeMin;
		this.foldChangeMax = foldChangeMax;
		this.direction=direction;
		this.bsm=bsm;
		this.percentile=percentile;
	}




   public String cacheKey() {
        final StringBuilder sb= new StringBuilder(omicsType);
        sb.append("#").append(geneSearchType).append("#").append(gene).append("#");
        if(ps!=null)
            sb.append(ps).append("#");
        if(organ!=null)
            sb.append(organ).append("#");

        sb.append(species).append("#").append(pathwayCategory).append("#")
                .append(signalingPathway).append("#")
                .append(foldChange).append("#").append(foldChangeMin).append("#").append(foldChangeMax)
                .append("#").append(significance).append("#").append(minScore).append("#").append(maxScore);
        return sb.toString();
    }

    public String toStringForApi() {
        final StringBuffer sb = new StringBuffer("SimpleQueryForm{");
        sb.append("omicsType='").append(omicsType).append('\'');
        sb.append(", gene='").append(gene).append('\'');
        sb.append(", ps='").append(ps).append('\'');
        sb.append(", organ='").append(organ).append('\'');
        sb.append(", goTerm='").append(goTerm).append('\'');
        sb.append(", ags='").append(ags).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String toStringByExp() {
		final StringBuffer sb = new StringBuffer("SimpleQueryForm{");
		sb.append("omicsType='").append(omicsType).append('\'');
		sb.append(", significance=").append(significance);
		sb.append(", direction='").append(direction).append('\'');
		sb.append(", foldChange=").append(foldChange);
		sb.append(", foldChangeMin=").append(foldChangeMin);
		sb.append(", foldChangeMax=").append(foldChangeMax);
		sb.append('}');
		return sb.toString();
	}

	public String toStringConsensome() {
		final StringBuilder sb = new StringBuilder("SimpleQueryForm{");
		sb.append("omicsType='").append(omicsType).append('\'');
		sb.append(", geneSearchType='").append(geneSearchType).append('\'');
		sb.append(", psId='").append(ps).append('\'');
		sb.append(", organ='").append(organ).append('\'');
		sb.append(", species='").append(species).append('\'');
		sb.append(", percentile='").append(percentile).append('\'');
		sb.append(", familyid='").append(signalingPathway).append('\'');
		sb.append('}');
		return sb.toString();
	}

	@Override
	public String toString() {
		return "SimpleQueryForm{" +
				"omicsType='" + omicsType + '\'' +
				", geneSearchType='" + geneSearchType + '\'' +
				", gene='" + gene + '\'' +
				", psId='" + ps + '\'' +
				", organ='" + organ + '\'' +
				", tissue='" + tissue + '\'' +
				", species='" + species + '\'' +
				", pathwayCategory='" + pathwayCategory + '\'' +
				", doi='" + doi + '\'' +
				", familyId='" + signalingPathway + '\'' +
				", minScore=" + minScore +
				", maxScore=" + maxScore +
				", significance=" + significance +
				", direction='" + direction + '\'' +
				", foldChange=" + foldChange +
				", foldChangeMin=" + foldChangeMin +
				", foldChangeMax=" + foldChangeMax +
				", isRange=" + isRange +
				'}';
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleQueryForm that = (SimpleQueryForm) o;
        return Objects.equals(omicsType, that.omicsType) &&
                Objects.equals(geneSearchType, that.geneSearchType) &&
                Objects.equals(gene, that.gene) &&
                Objects.equals(ps, that.ps) &&
                Objects.equals(organ, that.organ) &&
                Objects.equals(species, that.species) &&
                Objects.equals(pathwayCategory, that.pathwayCategory) &&
                Objects.equals(signalingPathway, that.signalingPathway) &&
                Objects.equals(minScore, that.minScore) &&
                Objects.equals(maxScore, that.maxScore) &&
                Objects.equals(significance, that.significance) &&
                Objects.equals(foldChange, that.foldChange) &&
                Objects.equals(foldChangeMin, that.foldChangeMin) &&
                Objects.equals(foldChangeMax, that.foldChangeMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(omicsType, geneSearchType, gene, ps, organ, species, pathwayCategory, signalingPathway, minScore, maxScore, significance, foldChange, foldChangeMin, foldChangeMax);
    }
}

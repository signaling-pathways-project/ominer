package edu.bcm.dldcc.big.nursa.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Transient;

import edu.bcm.dldcc.big.nursa.model.common.Article;
import edu.bcm.dldcc.big.nursa.model.omics.BsmView;
import edu.bcm.dldcc.big.nursa.model.omics.NURSADataset;
import edu.bcm.dldcc.big.nursa.services.PubmedAbstractBean;
import org.omnifaces.cdi.ViewScoped;

import edu.bcm.dldcc.big.nursa.model.common.Reference;
import edu.bcm.dldcc.big.nursa.model.core.MiRNAInteraction;

/**
 * Component to help with Reference in UI dialogs/dialog
 * @author mcowiti
 *
 */
@Named("referenceUtil")
@ViewScoped
public class ReferenceUtil implements Serializable{
	
	private static final long serialVersionUID = 6802388986959747782L;
	@Inject private PubmedAbstractBean pubmedAbstractBean;

	private Reference reference;


	/**
	 * UI was passing mirna.interactions.references, supposedly as list of References, which is clearly not right.
	 * Detected bug in widlfly(how was prior versions dealing with this?)
	 * miRNAs tend have lots of interaction, so  limiting
	 * @param interactions
	 * @return
	 */
	public List<Reference> getMiRNAInteractionReferences(List<MiRNAInteraction> interactions){
		return interactions.stream().limit(10).map(s->s.getReference()).collect(Collectors.toList());
	}
	
	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		
		this.reference = reference;
	}


	public String getPubmed(NURSADataset dataset){
		String pubmed=null;

		/*if (dataset.getReferencesByBsm().size()>0){
			BsmView bsmView=dataset.getReferencesByBsm().get(0);
			pubmed=(bsmView.getEvidenceType()== BsmView.EvidenceType.pubmed)?bsmView.getEvidence():null;
		}*/
		if(dataset.getReferences() != null){
		    pubmed=dataset.getReference().getPubmedId();
        }
		return pubmed;
	}

	public String getDatasetCitation(NURSADataset dataset)
	{
		StringBuilder citation = new StringBuilder();
		Article article=null;
		if(dataset.getReference()!=null)
			article=dataset.getReference().getArticle();

		// GEO
		//TODO need check FileHelper#generateRisFile too
		if (null != dataset.getRepo() && (dataset.getRepo().startsWith("GSE") || (dataset.getRepo().startsWith("SRP"))))
		{
			if(dataset.getContributors() != null || dataset.getPublished() !=null){ //FIXME since dmc GEO fails are allowed
				String sData = "";
				if (dataset.getPublished() == null)
					sData = "";
				else
					sData = (new SimpleDateFormat("yyyy")).format(dataset.getPublished());

				if(article!= null) { //1.22.2019 condition added since article can be null due to no pubmedid to retrieve Article
					if (sData.equals("") && dataset.getReference() != null) {
						sData = article.getPublishYear();
					}
					buildPublication(dataset.getContributors(), dataset.getName(), sData, dataset.getDoi().getDoi(), citation);
				}else{
					citation.append("Dataset has incomplete record(Missing Pubmed Information)");
				}
			}else{
				if (article != null){
					buildPublication(article.getAuthorsList(), dataset.getName(), article.getPublishYear(), dataset.getDoi().getDoi(), citation);
				}else
					citation.append("Dataset has incomplete record(contributors,published date)");
			}
		}
		else if (null != article)
		{
			buildPublication(article.getAuthorsList(), dataset.getName(), article.getPublishYear(), dataset.getDoi().getDoi(), citation);
		}
		else
		{
			citation.append("Not available");
		}

		return citation.toString();
	}

	public void buildPublication(String authors, String name ,String date, String doi, StringBuilder builder)
	{
		builder.append(getCitationAuthors(authors));
		builder.append(" (");
		builder.append(date);
		builder.append(") ");
		builder.append(name);
		builder.append(", v1.0. SignalingPathway Project Datasets. ");
		builder.append(doi);
	}

	/**
	 * If make changes here, also do in BaseArticleBean
	 * Need DRY this
	 * @param authorsList
	 * @return
	 */
	public  String getCitationAuthors(String authorsList) {
	    return pubmedAbstractBean.getCitationAuthors(authorsList);

	}
}

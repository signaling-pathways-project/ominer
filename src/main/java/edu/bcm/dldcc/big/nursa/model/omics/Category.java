package edu.bcm.dldcc.big.nursa.model.omics;

/**
 * Created by alexey on 2/23/16.
 *
 * interface to handel same logic to work with SignalingPathway and TissueCategory
 */
public interface Category {

    Long getId();
    void setId(Long id);

    void setName(String name);
    String getName();

    Category getParent();
    void setParent(Category parent);
}

package edu.bcm.dldcc.big.nursa.model.omics;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.bcm.dldcc.big.nursa.model.omics.dto.ApiOmicsDatapoint;
import edu.bcm.dldcc.big.nursa.model.omics.dto.OmicsDatapoint;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * New Fold Change Representation
 * @author mcowiti
 */

@Entity
@XmlRootElement(name="dataPoint")
@Table(name="expmicroarrayexpression_orig")
@XmlAccessorType(XmlAccessType.FIELD)

@NamedQueries({
        @NamedQuery(
            name = "getReactomeTXDatapointsCount",
            query = "SELECT count(distinct f.id) " +
                "FROM FoldChangeViewForApi f, DatasetViewDTO d where " +
                "f.expid=d.expid and d.dtype='Transcriptomic' and f.id >= :startId  and f.pValue <=:pvalue and d.doi=:doi " +
                "and f.gene is not null and f.pValue is not null"),
        @NamedQuery(
                name = "getReactomeSRXDatapointsCount",
                query = "SELECT count(distinct f.id) " +
                        "FROM BindingScore f, DatasetViewDTO d where " +
                        "f.experiment.id=d.expid and f.id >= :startId and d.dtype='Cistromic' and d.doi=:doi "),
})

@NamedNativeQueries({
        @NamedNativeQuery(
                name = "getReactomeTXDatapoints",
                query = "SELECT distinct f.id as id, f.symbol as symbol, d.internalexperimentid as experimentId, f.foldChange as foldChange, f.pValue  as pvalue " +
                        "FROM expmicroarrayexpression_orig f, DATASET_VIEW d where " +
                        "f.experiment_id=d.expid and d.dtype='Transcriptomic' and f.id >= :startId  and f.pValue <=:pvalue and d.doi=:doi " +
                        "and f.symbol is not null and f.pValue is not null order by id asc",
                resultSetMapping="ApiOmicsDatapointTxResult"),

        @NamedNativeQuery(
                name = "getReactomeSRXDatapoints",
                query = "SELECT distinct f.id as id,f.gene as symbol, d.internalexperimentid as experimentId, f.score as score " +
                        "FROM AntigenBindingdata f, DATASET_VIEW d where " +
                        "f.experiment_id=d.expid and f.id >= :startId and d.dtype='Cistromic' and d.doi=:doi order by id asc",
        resultSetMapping = "ApiOmicsDatapointSrxResult"),

})
@SqlResultSetMappings({
    @SqlResultSetMapping(name="ApiOmicsDatapointSrxResult",
        classes = {
                @ConstructorResult(
                        targetClass = ApiOmicsDatapoint.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "symbol", type = String.class),
                                @ColumnResult(name = "experimentId", type = String.class),
                                @ColumnResult(name = "score", type = Integer.class)
                        })
        }),
        @SqlResultSetMapping(name="ApiOmicsDatapointTxResult",
                classes = {
                        @ConstructorResult(
                                targetClass = ApiOmicsDatapoint.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "symbol", type = String.class),
                                        @ColumnResult(name = "experimentId", type = String.class),
                                        @ColumnResult(name = "foldChange", type = Double.class),
                                        @ColumnResult(name = "pValue", type = Double.class)
                                })
                }),

        })


public class FoldChangeViewForApi {

    @Id
    @JsonProperty(value="datapointid")
    private Long id;

    @JsonProperty
    @Column(name="experiment_id")
    private Long expid;

    @JsonProperty
    @Column(name="symbol")
    private String gene;

    @JsonProperty
    private Long egeneid;

    @JsonProperty
    @Column(name="probeidentifier")
    private String prob;
    @JsonProperty
    @Column(name="probeidentifiertype")
    private String probType;
    @JsonProperty
    @Basic
    private double foldChange;
    @JsonProperty
    @Basic
    private double pValue;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExpid() {
        return expid;
    }

    public void setExpid(Long expid) {
        this.expid = expid;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getProb() {
        return prob;
    }

    public void setProb(String prob) {
        this.prob = prob;
    }

    public String getProbType() {
        return probType;
    }

    public void setProbType(String probType) {
        this.probType = probType;
    }


    public void setFoldChange(double foldChange) {
        this.foldChange = foldChange;
    }

    public double getpValue() {
        return pValue;
    }

    public void setpValue(double pValue) {
        this.pValue = pValue;
    }

    public Long getEgeneid() {
        return egeneid;
    }

    public void setEgeneid(Long egeneid) {
        this.egeneid = egeneid;
    }

    public Double getFoldChange() {

        if (foldChange <1D && foldChange !=0D) {
            return -1/foldChange;
        }
        return foldChange;
    }

    public Double getFoldChangeRaw() {

        return foldChange;
    }

}

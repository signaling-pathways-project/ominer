package edu.bcm.dldcc.big.nursa.model.transcriptomic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by alexey on 5/11/15.
 */
@Entity
@SqlResultSetMapping(name = "ExpAnnotationViewEntity", entities = @EntityResult(entityClass = ExpAnnotationViewEntity.class,
        fields = {@FieldResult(name="id", column="id"),
                @FieldResult(name="quantity", column="quantity"),
                @FieldResult(name="timehours", column="timehours"),
                @FieldResult(name="timeunit", column="timeunit"),
                @FieldResult(name="quantityUnit", column="quantityunit_unit"),
                @FieldResult(name="experimentId", column="experiment_id"),
                @FieldResult(name="molsynonName", column="MOLSYNON_NAME"),
                @FieldResult(name="molsynUrl", column="DOI_URL")}))
@Table(name = "EXPANNOTATION_VIEW")
public class ExpAnnotationViewEntity {

    @Id
    private String id;

    private BigDecimal quantity;

    private BigDecimal timehours;

    private String timeunit;

    @Column(name = "quantityunit_unit")
    private String quantityUnit;

    @Column(name = "experiment_id")
    private Integer experimentId;

    @Column(name = "DOI_URL")
    private String molsynUrl;

    @Column(name = "molsynon_name")
    private String molsynonName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

      public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTimehours() {
        return timehours;
    }

    public void setTimehours(BigDecimal timehours) {
        this.timehours = timehours;
    }

    public String getTimeunit() {
        return timeunit;
    }

    public void setTimeunit(String timeunit) {
        this.timeunit = timeunit;
    }

    public String getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(String quantityunitUnit) {
        this.quantityUnit = quantityunitUnit;
    }

    public Integer getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    public String getMolsynUrl() {
        return molsynUrl;
    }

    public void setMolsynUrl(String molsynUrl) {
        this.molsynUrl = molsynUrl;
    }

    public String getMolsynonName() {
        return molsynonName;
    }

    public void setMolsynonName(String molsynonName) {
        this.molsynonName = molsynonName;
    }
}

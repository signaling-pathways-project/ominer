package edu.bcm.dldcc.big.nursa.model.omics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@XmlRootElement(name = "txdatapointreport")
@XmlAccessorType(XmlAccessType.FIELD)
public class OmicsDatapointReport implements Serializable {

    @JsonProperty
    private String pathwayNode;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<OmicsDatapoint> datapoints= new ArrayList<OmicsDatapoint>();

    public OmicsDatapointReport() {
    }

    public OmicsDatapointReport(String pathwayNode, List<OmicsDatapoint> datapoints) {
        this.pathwayNode = pathwayNode;
        this.datapoints = datapoints;
    }

    public String getPathwayNode() {
        return pathwayNode;
    }

    public void setPathwayNode(String pathwayNode) {
        this.pathwayNode = pathwayNode;
    }

    public List<OmicsDatapoint> getDatapoints() {
        return datapoints;
    }

    public void setDatapoints(List<OmicsDatapoint> datapoints) {
        this.datapoints = datapoints;
    }
}

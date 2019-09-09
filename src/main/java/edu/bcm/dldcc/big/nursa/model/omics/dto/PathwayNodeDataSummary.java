package edu.bcm.dldcc.big.nursa.model.omics.dto;

import edu.bcm.dldcc.big.nursa.model.omics.PathwayCategory;
import edu.bcm.dldcc.big.nursa.services.rest.omics.QueryType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

@XmlRootElement(name = "pathwayNodeDataSummary")
public class PathwayNodeDataSummary {
    private String type;
    private Long nodeId;
    private Integer count;

    public PathwayNodeDataSummary() {
    }

    public PathwayNodeDataSummary(String type, Long nodeId, Integer count) {
        this.type = type;
        this.nodeId = nodeId;
        this.count = count;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathwayNodeDataSummary that = (PathwayNodeDataSummary) o;
        return  Objects.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash( nodeId);
    }
}

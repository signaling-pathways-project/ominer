package edu.bcm.dldcc.big.nursa.model.oai;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by alexey on 10/27/15.
 */

@Entity
@SqlResultSetMapping(name = "MetadataNursa", entities = @EntityResult(entityClass = MetadataNursa.class,
                    fields = {@FieldResult(name = "doi", column = "doi"),
                            @FieldResult(name = "releaseDate", column = "releaseDate"),
                            @FieldResult(name = "name", column = "name"),
                            @FieldResult(name = "authorslist", column = "authorslist"),
                            @FieldResult(name = "description", column = "description"),
                            @FieldResult(name = "repo", column = "repo"),
                            @FieldResult(name = "pubmedid", column = "pubmedid"),
                            @FieldResult(name = "regMolList", column = "regMolList"),
                            @FieldResult(name = "tissueNames", column = "tissueNames")}))

public class MetadataNursa {

    @Id
    private String doi;

    private Date releaseDate;

    private String name;

    private String authorslist;

    private String description;

    private String repo;

    private String pubmedid;

    private String regMolList;

    private String tissueNames;

    public MetadataNursa(BigDecimal doi, Date releaseDate, String name, String authorslist, String description, String repo, String pubmedid, String regMolList, String tissueNames) {
        this.doi = doi.toString();
        this.releaseDate = releaseDate;
        this.name = name;
        this.authorslist = authorslist;
        this.description = description;
        this.repo = repo;
        this.pubmedid = pubmedid;
        this.regMolList = regMolList;
        this.tissueNames = tissueNames;
    }

    public MetadataNursa() {
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorslist() {
        return authorslist;
    }

    public void setAuthorslist(String authorslist) {
        this.authorslist = authorslist;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getPubmedid() {
        return pubmedid;
    }

    public void setPubmedid(String pubmedid) {
        this.pubmedid = pubmedid;
    }

    public String getRegMolList() {
        return regMolList;
    }

    public void setRegMolList(String regMolList) {
        this.regMolList = regMolList;
    }

    public String getTissueNames() {
        return tissueNames;
    }

    public void setTissueNames(String tissueNames) {
        this.tissueNames = tissueNames;
    }
}

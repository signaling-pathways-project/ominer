package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by alexey on 5/9/16.
 */
@Entity
@Table(name="Metabolite")
public class Metabolite
{
    @Id
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

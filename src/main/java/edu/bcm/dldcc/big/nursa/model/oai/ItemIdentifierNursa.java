package edu.bcm.dldcc.big.nursa.model.oai;

import org.dspace.xoai.dataprovider.model.ItemIdentifier;
import org.dspace.xoai.dataprovider.model.Set;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexey on 10/16/15.
 */
@Entity
@SqlResultSetMapping(name = "ItemIdentifierNursa", entities = @EntityResult(entityClass = ItemIdentifierNursa.class,
        fields = {@FieldResult(name="identifier", column="identifier"),
                  @FieldResult(name="datestamp", column = "release_date")}))
public class ItemIdentifierNursa implements ItemIdentifier {

    @Id
    private String identifier;

    private Date datestamp;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Date getDatestamp() {
        return datestamp;
    }

    @Override
    public List<Set> getSets() {
        return new ArrayList<Set>();
    }

    @Override
    public boolean isDeleted() {
        return false;
    }


}

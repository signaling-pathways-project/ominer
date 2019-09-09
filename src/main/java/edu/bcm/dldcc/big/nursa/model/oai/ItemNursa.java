package edu.bcm.dldcc.big.nursa.model.oai;

import org.dspace.xoai.dataprovider.model.Item;
import org.dspace.xoai.dataprovider.model.Set;
import org.dspace.xoai.model.oaipmh.About;
import org.dspace.xoai.model.oaipmh.Metadata;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexey on 10/21/15.
 */
public class ItemNursa implements Item {

    List<About> abouts =new ArrayList<About>();
    Metadata metadata;
    String identifier;
    Date datestamp;

    public ItemNursa setDatestamp(Date datestamp) {
        this.datestamp = datestamp;
        return this;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public List<About> getAbout() {
        return abouts;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public String getIdentifier() {
        return identifier;
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

    public ItemNursa(Metadata m) {
        super();
        metadata = m;

    }
}

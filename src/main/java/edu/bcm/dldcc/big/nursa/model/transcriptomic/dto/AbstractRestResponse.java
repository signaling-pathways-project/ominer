package edu.bcm.dldcc.big.nursa.model.transcriptomic.dto;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Link.Builder;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public  class AbstractRestResponse extends Response {

	public boolean bufferEntity() {
		return false;
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public Set<String> getAllowedMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, NewCookie> getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	public EntityTag getEntityTag() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHeaderString(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Locale getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getLastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Link getLink(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Builder getLinkBuilder(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<Link> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	public URI getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public MediaType getMediaType() {
		// TODO Auto-generated method stub
		return null;
	}

	public MultivaluedMap<String, Object> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public StatusType getStatusInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public MultivaluedMap<String, String> getStringHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasEntity() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasLink(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T readEntity(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T readEntity(GenericType<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T readEntity(Class<T> arg0, Annotation[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T readEntity(GenericType<T> arg0, Annotation[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}

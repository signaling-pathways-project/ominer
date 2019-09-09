package edu.bcm.dldcc.big.nursa.model.omics.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexey on 1/19/16.
 */
public class CategoryNode
{
    public String name;
    public Long id;

    private Map<Long, CategoryNode> children;

    public void addChildren(CategoryNode incoming)
    {
        if (null == children)
        {
            children = new HashMap<Long, CategoryNode>();
        }
        children.put(incoming.id, incoming);
    }

    public CategoryNode(Long id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public CategoryNode getChild(Long id)
    {
        if (null != children )
        {
            return children.get(id);
        }

        return null;
    }


    public List<CategoryNode> getChildren()
    {
        if (null != children&& null !=  children.values()) {
            return new ArrayList<CategoryNode>(children.values());
        }
        return null;
    }

}

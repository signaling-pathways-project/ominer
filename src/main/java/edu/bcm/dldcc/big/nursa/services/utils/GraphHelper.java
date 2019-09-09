package edu.bcm.dldcc.big.nursa.services.utils;

import edu.bcm.dldcc.big.nursa.model.cistromic.dto.SunBurstData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexey on 3/30/15.
 * this class contains methods to convert graph data to sunberst
 */
public class GraphHelper
{
    /**
     *
     * @param result in the following format level1->level2->...->levelN->NumberOfExperiments->NumberOfDatapoints
     *               all objects expected to have the same number of levels.
     * @return
     */
    public static SunBurstData agregateResults(List<Object[]> result)
    {
        if (null == result||result.get(0).length <3)
            return  null;

        SunBurstData root = new SunBurstData();
        if (result.get(0).length == 3)
        {
            for (Object[] record:result)
                root.addChildrenWithUpdate(new SunBurstData( (String) record[0],((BigDecimal) record[1]).intValueExact(), ((BigDecimal) record[2]).intValueExact(), null, null));

            return root;
        }

        int size = result.get(0).length;
        // this is a temporary storage to keep the refferences to the lowest level

       TreeNode treeMap = new TreeNode(root);

        // I have to have keep two maps in memory while constructing the final structure.
        // This is due to the fact that SunBurstData does not supports navigation to child by name.
        // Yes, I can iterate through all children but I don't like this idea.
        for (Object[] record: result)
        {
            Integer numberOfExperiments = ((BigDecimal) record[size-2]).intValueExact();
            Integer numberOfDatapoints = ((BigDecimal) record[size-1]).intValueExact();
            String  recordName = (String) record[size-3];
            SunBurstData parent = root;
            TreeNode nodeParrent = treeMap;

            for (int i = 0; i<record.length - 3;i++)
            {
                String levelName = (String) record[i];
                if (i==0) { // if it is a first iteration point to root
                    nodeParrent = treeMap;
                }

                TreeNode currentRootNode = nodeParrent.get(levelName);
                if(currentRootNode != null)
                {
                    SunBurstData levelParent = currentRootNode.getNode();
                    levelParent.updateNumbers(numberOfExperiments, numberOfDatapoints);
                }
                else
                {
                    // If level does not exists :
                    SunBurstData newLevel = new SunBurstData(levelName, numberOfExperiments, numberOfDatapoints, new ArrayList<SunBurstData>(), null );
                    TreeNode newNode =  nodeParrent.addChild(levelName, newLevel);
                    if (parent.getChildren().size() > 0) {
                        parent.addChildrenWithUpdate(newLevel);
                    }
                    else {
                        parent.addChildren(newLevel);
                    }
                    currentRootNode = newNode;
                }
                parent = currentRootNode.getNode();
                nodeParrent = currentRootNode;
            }

            parent.addChildren(new SunBurstData(recordName, numberOfExperiments, numberOfDatapoints, null,null));
        }
        return root;
    }

}

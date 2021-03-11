package edu.bcm.dldcc.big.nursa.util;


import edu.bcm.dldcc.big.nursa.model.omics.dto.TmNone;

import java.util.Random;

public class Utility {
	
	public static String generateString(Random rng, String characters, int length)
	{
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}

    public static String stringOrTnNone(String s)
    {
        return (null != s)?s: TmNone.none.name();
    }
}

/*******************************************************************************
 * Copyright 2018 by The Alan Turing Institute
 * 
 *******************************************************************************/
package uk.turing.aida.tabulardata.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ernesto
 * Created on 30 Jul 2018
 *
 */
public class Utils {
	
	public static String removeQuotes(String str){
		//Remove first and last characters if starting-ending by " or '
		return StringUtils.strip(StringUtils.strip(str, "\""), "\'");
	}

}

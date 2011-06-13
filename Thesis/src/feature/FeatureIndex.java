/*
 * Copyright 2005 FBK-irst (http://www.fbk.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package feature;

import java.io.*;
import java.util.*;

/**
 * An object that maps features to indexes.
 * A <code>FeatureIndex</code> cannot contain duplicate
 * features; each feature can map to at most one index.
 *
 * @author		Claudio Giuliano
 * @version 	%I%, %G%
 * @since			1.0
 * @see FeatureIndex
 */
public class FeatureIndex
{	
	/**
	 * to do.
	 */
	private SortedMap map;

	/**
	 * to do.
	 */
	private int count;

	/**
	 * Constructs a <code>FeatureIndex</code> object.
         *
         * @param count
         */
	public FeatureIndex(int count)
	{
		//logger.info("FeatureIndex " + count);
		
		map = new TreeMap();
		//map = new HashMap();
		
		this.count = count;
	} // end constructor
	
	/**
	 * Returns the <i>index</i> of the specified feature and adds
	 * the feature to the index if it is not present yet.
	 *
	 * @param feature	the feature.
	 * @return 			the <i>index</i> of the specified feature.
	 */
	public int put(String feature)
	{
		//logger.debug("FeatureIndex.put : " + feature + "(" + count + ")");
		Integer index = (Integer) map.get(feature);
		
		if (index == null)
		{

			index = new Integer(count++);
			map.put(feature, index);
		}
		
		return index.intValue();
	} // end get

	//
	public int size()
	{
		return map.size();
	} // end size

	/**
	 * Returns the <i>index</i> of the specified feature and adds
	 * the feature to the index if it is not present yet.
	 *
	 * @param feature	the feature.
	 * @return 			the <i>index</i> of the specified feature.
	 */
	public int getIndex(String feature)
	{
		//logger.debug("FeatureIndex.get : " + feature + "(" + count + ")");
		Integer index = (Integer) map.get(feature);
		
		if (index == null)
			return -1;
			
		return index.intValue();
	} // end get
	
	//
	public void clear()
	{
		count = 0;
		map.clear();
	} // end clear
		
	/**
	 * Returns a <code>String</code> object representing this
	 * <code>Word</code>.
	 *
	 * @return a string representation of this object.
	 */
    @Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry me = (Map.Entry) it.next();
			sb.append(me.getValue());
			sb.append("\t");
			sb.append(me.getKey());
			sb.append("\n");
		}
		return sb.toString();
	} // end toString


} // end class FeatureIndex
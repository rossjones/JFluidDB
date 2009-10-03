package com.fluidinfo.fom;

import java.io.IOException;
import org.json.JSONException;
import com.fluidinfo.FluidException;

/**
 * Ensures BaseFOM classes implement the getItem method
 * 
 * @author ntoll
 *
 */
public interface FOMInterface {
	/**
	 * Gets and caches the item from FluidDB
	 */
	void getItem() throws FluidException, IOException, FOMException, JSONException;
}

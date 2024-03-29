/*
Copyright 2017, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_lib_encodingrulestester;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;

/**
 * Store test case parameters
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class EncodingRulesTesterTcParam implements IVCT_TcParam {
    // Get test case parameters
    //      use some constants for this example till we get params from a file
    private List<String> fomFiles = new ArrayList<String>();
    private List<String> somFiles = new ArrayList<String>();
    private URL[]        fomUrls;
    private URL[]        somUrls;
    private long         sleepTimeCycle     = 1000;
    private long         sleepTimeWait      = 3000;
    private long         sleepTestTimeWait = 30000;

    public EncodingRulesTesterTcParam(final String paramJson) throws TcInconclusive {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(paramJson);

			// get an integer from the JSON object
			String tempString = (String) jsonObject.get("sleepTestTimeWaitSeconds");
			if (tempString != null) {
				sleepTestTimeWait = Long.parseLong(tempString) * 1000;
			} else {
				sleepTestTimeWait = 30000;
			}

			// get FOM files list from the JSON object
			JSONArray fomArray = (JSONArray) jsonObject.get("fomFiles");
			if (fomArray == null) {
                throw new TcInconclusive("EncodingRulesTesterTcParam: missing FOM files");
			}
			else {
		        int index = 0;
				this.fomUrls = new URL[fomArray.size()];
				Iterator iter = fomArray.iterator();
				while (iter.hasNext()) {
					JSONObject element = (JSONObject) iter.next();
					String fileName = (String) element.get("fileName");
					this.fomFiles.add(fileName);
			        // add FOM file in url array
			        try {
			        	URI uri = (new File(fileName)).toURI();
						this.fomUrls[index++] = uri.toURL();
					}
			        catch (MalformedURLException e) {
						e.printStackTrace();
		                throw new TcInconclusive("EncodingRulesTesterTcParam: MalformedURLException FOM");
					}
					catch (Exception e) {
						e.printStackTrace();
		                throw new TcInconclusive("EncodingRulesTesterTcParam: Exception FOM");
					}
				}
			}
			// get SOM files list from the JSON object
			JSONArray somArray =  (JSONArray) jsonObject.get("somFiles");
			if (somArray == null) {
                throw new TcInconclusive("EncodingRulesTesterTcParam: missing SOM files");
			}
			else {
		        int index = 0;
				this.somUrls = new URL[somArray.size()];
				Iterator iter = somArray.iterator();
				while (iter.hasNext()) {
					JSONObject element = (JSONObject) iter.next();
					String fileName = (String) element.get("fileName");
					this.somFiles.add(fileName);
			        // add SOM file in url array
			        try {
			        	URI uri = (new File(fileName)).toURI();
						this.somUrls[index++] = uri.toURL();
					}
			        catch (MalformedURLException e) {
						e.printStackTrace();
		                throw new TcInconclusive("EncodingRulesTesterTcParam: MalformedURLException SOM");
					}
					catch (Exception e) {
						e.printStackTrace();
		                throw new TcInconclusive("EncodingRulesTesterTcParam: Exception SOM");
					}
				}
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
            throw new TcInconclusive("EncodingRulesTesterTcParam: ParseException");
		}
    }


    /**
     * @return the RTI host value
     */
    public float getPopulationGrowthValue() {
        return 1.03f;
    }


    /**
     * @return value of sleep time cycle
     */
    public long getSleepTimeCycle() {
        return this.sleepTimeCycle;
    }


    /**
     * @return value of sleep time wait
     */
    public long getSleepTimeWait() {
        return this.sleepTimeWait;
    }

    /**
     * @return value of test sleep time wait
     */
    public long getTestTimeWait() {
        return this.sleepTestTimeWait;
    }


    /**
     * @return the fomUrls
     */
    @Override
    public URL[] getUrls() {
        return this.fomUrls;
    }


    /**
     * @return the somUrls
     */
    public URL[] getSomUrls() {
        return this.somUrls;
    }
}

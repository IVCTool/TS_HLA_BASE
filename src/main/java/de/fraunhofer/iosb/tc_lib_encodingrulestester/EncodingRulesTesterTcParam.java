/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

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
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private String federation_name;
    private String rtiHost;
    private String rtiPort;
    private String settingsDesignator;
    private URL[]        fomUrls;
    private URL[]        somUrls;
    private long         sleepTimeCycle     = 1000;
    private long         sleepTimeWait      = 3000;
    private long         sleepTestTimeWait = 30000;
    private String sutFederate;


    public EncodingRulesTesterTcParam(final String paramJson) throws TcInconclusive {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(paramJson);
			// get a String from the JSON object
			federation_name =  (String) jsonObject.get("federationName");
			if (federation_name == null) {
                throw new TcInconclusive("EncodingRulesTesterTcParam: the key  federationName  was not found");
			}
			// get a String from the JSON object
			rtiHost =  (String) jsonObject.get("rtiHostName");
			if (rtiHost == null) {
                throw new TcInconclusive("EncodingRulesTesterTcParam: the key  rtiHostName  was not found");
			}
			rtiPort = (String) jsonObject.get("rtiPort");
			if (rtiPort == null) {
				throw new TcInconclusive("EncodingRulesTesterTcParam: the rti port id was not found");
			}
			settingsDesignator = "crcAddress=" + this.rtiHost + ":" + this.rtiPort;
			
			// get a String from the JSON object
			sutFederate =  (String) jsonObject.get("sutFederateName");
			if (sutFederate == null) {
                throw new TcInconclusive("EncodingRulesTesterTcParam: the key  sutFederateName  was not found");
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// get SOM files list from the JSON object
			JSONArray somArray =  (JSONArray) jsonObject.get("somFiles");
			if (somArray == null) {
                throw new TcInconclusive("EncodingRulesTesterTcParam: missing SOM files");
			}
			else {
				this.somUrls = new URL[somArray.size()];
				Iterator iter = somArray.iterator();
				while (iter.hasNext()) {
					JSONObject element = (JSONObject) iter.next();
					String fileName = (String) element.get("fileName");
					this.somFiles.add(fileName);
				}
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }


    /**
     * @return the federation name
     */
    @Override
    public String getFederationName() {
        return this.federation_name;
    }


    /**
     * @return the RTI host value
     */
    public float getPopulationGrowthValue() {
        return 1.03f;
    }


    /**
     * @return the RTI host value
     */
    public String getRtiHost() {
        return this.rtiHost;
    }


    /**
     * @return the settings designator
     */
    @Override
    public String getSettingsDesignator() {
        return this.settingsDesignator;
    }


    /**
     * @return value of sleep time for tmr
     */
    public long getSleepTimeCycle() {
        return this.sleepTimeCycle;
    }


    /**
     * @return value of sleep time for tmr
     */
    public long getSleepTimeWait() {
        return this.sleepTimeWait;
    }
    
    public long getTestTimeWait() {
        return this.sleepTestTimeWait;
    }


    /**
     * @return name of sut federate
     */
    public String getSutFederate() {
        return this.sutFederate;
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

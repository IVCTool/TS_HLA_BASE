/*
Copyright 2017, FRANCE (DGA/Capgemini)

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

package nato.ivct.etc.fr.tc_lib_cs_verification;

import de.fraunhofer.iosb.tc_lib.IVCT_TcParam;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nato.ivct.etc.fr.fctt_common.utils.TextInternationalization;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Store test case parameters
 *
 * @author FRANCE (DGA/Capgemini)
 */
public class CS_Verification_TcParam implements IVCT_TcParam {
    // Get test case parameters
    private String federationName = "";		// not useful for CS_Verification
    private String resultDir;
    private List<String> fomFiles = new ArrayList<String>();
    private List<String> somFiles = new ArrayList<String>();
    private URL[]  urls;
    private String settingsDesignator = "";	// not useful for CS_Verification
    

    public CS_Verification_TcParam(final String paramJson) throws TcInconclusive {
    	
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject) jsonParser.parse(paramJson);
			// get TC result directory from the JSON object
			this.resultDir =  (String) jsonObject.get("resultDirectory");
			if (this.resultDir == null) {
                throw new TcInconclusive(TextInternationalization.getString("etc_fra.noResultDir"));
			}
			// get FOM files list from the JSON object
			JSONArray fomArray = (JSONArray) jsonObject.get("fomFiles");
			if (fomArray == null) {
                throw new TcInconclusive(TextInternationalization.getString("etc_fra.noFomFilesKey"));
			}
			else {
				Iterator iter = fomArray.iterator();
				while (iter.hasNext()) {
					JSONObject element = (JSONObject) iter.next();
					String fileName = (String) element.get("fileName");
					this.fomFiles.add(fileName);
				}
			}
			// get SOM files list from the JSON object
			JSONArray somArray =  (JSONArray) jsonObject.get("somFiles");
			if (somArray == null) {
                throw new TcInconclusive(TextInternationalization.getString("etc_fra.noSomFilesKey"));
			}
			else {
				Iterator iter = somArray.iterator();
				while (iter.hasNext()) {
					JSONObject element = (JSONObject) iter.next();
					String fileName = (String) element.get("fileName");
					this.somFiles.add(fileName);
				}
			}
		}
		catch (ParseException e1) {
			throw new TcInconclusive(TextInternationalization.getString("etc_fra.invalidConfig"));
		}
    }


    /**
     * @return the federation name
     */
    @Override
    public String getFederationName() {
        return this.federationName;
    }


    /**
     * @return the settings designator
     */
    @Override
    public String getSettingsDesignator() {
        return this.settingsDesignator;
    }


    /**
     * @return the urls
     */
    @Override
    public URL[] getUrls() {
        return this.urls;
    }


    /**
     * @return the result directory value
     */
    public String getResultDir() {
        return this.resultDir;
    }


    /**
     * @return the FOM list value
     */
    public List<String> getFomFiles() {
        return this.fomFiles;
    }


    /**
     * @return the SOM list value
     */
    public List<String> getSomFiles() {
        return this.somFiles;
    }
}

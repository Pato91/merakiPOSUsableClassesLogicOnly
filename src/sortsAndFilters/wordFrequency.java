/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sortsAndFilters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author meraki
 */
public class wordFrequency {

    public int wordTimes(String fullText, String subString) {
        int freq = 0;
        if (fullText != null && subString != null) {
            List<String> fullString = (List) Arrays.asList(fullText.split(" "));

              freq = freq + Collections.frequency(fullString, subString);
        }
        return freq;
    }
}

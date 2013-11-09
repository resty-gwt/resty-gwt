package org.fusesource.restygwt.client.codec;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.fusesource.restygwt.client.codec.EncoderDecoderTestGwt.Email;

class WithArraysAndCollections {
    
    int[] ages;

    @JsonIgnore
    Set<int[]> ageSet;

    Email[] emailArray;

    List<Email> emailList;

    Set<Email> emailSet;

    List<Email>[] emailListArray;

    Set<Email>[] emailSetArray;
    
    Map<String, List<Email>> personalEmailList;

    Map<String, Set<Email>> personalEmailSet;
    
    Map<String, List<Email>[]> personalEmailListArray;

    Map<String, Set<Email>[]> personalEmailSetArray;

    List<Map<String, Set<Email>>> personalEmailSetList;
    
    Set<Map<String, List<Email>>> personalEmailListSet;
    
    Map<Email, Map<String, Set<Email>>> personalEmailSetMap;

    public String toString(){
        return Arrays.toString( ages ) + "," +
                Arrays.toString( emailArray ) + "," +
                emailList + "," + personalEmailList + "," + ageSet + "," + emailSet +
                "," + personalEmailSet + "," + 
                Arrays.toString( emailListArray ) + "," + 
                Arrays.toString( emailSetArray ) + "," + 
                personalEmailListArray.keySet() + "=>" + Arrays.toString( personalEmailListArray.values().iterator().next() ) + "," +
                personalEmailSetArray.keySet() + "=>" + Arrays.toString( personalEmailSetArray.values().iterator().next() ) + "," +
                personalEmailSetList + "," +
                personalEmailListSet + "," +
                personalEmailSetMap
                ;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package localization;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

/**
 *
 * @author Ivan
 */
@ManagedBean(name = "language")
@SessionScoped
public class LanguageBean implements Serializable {

    private String localeCode; 
    
    private static Map<String, Object> countries; 
    static {
        countries = new LinkedHashMap<String, Object>(); 
        countries.put("English (US)", Locale.forLanguageTag("en-US")); 
        countries.put("Serbian (Latn)", Locale.forLanguageTag("sr-Latn-RS"));
        countries.put("Serbian (Cyrl)", Locale.forLanguageTag("sr-RS"));
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }
    
    public Map<String, Object> getCountries(){
        return countries; 
    }
    
    public void countryLocaleCodeChanged(ValueChangeEvent e){
        String newLocaleValue = e.getNewValue().toString(); 
        
        for(Map.Entry<String, Object> entry: countries.entrySet()){
            if(entry.getValue().toString().equals(newLocaleValue)){
                FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale) entry.getValue());
            }
        }
    }
}

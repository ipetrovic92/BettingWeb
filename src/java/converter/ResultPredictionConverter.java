/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Ivan
 */
@FacesConverter("resultPredictionConverter")
public class ResultPredictionConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        switch (value) {
            case "X":
                return 0;
            case "1":
                return 1;
            case "2":
                return 2;
            default:
                return -1;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        switch ((int) value) {
            case 0:
                return "X";
            case 1:
                return "1";
            case 2:
                return "2";
            default:
                return "Error";
        }
    }

}

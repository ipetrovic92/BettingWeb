/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Iterator;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Ivan
 */
@ManagedBean
public class ValidationUtil {

    public void onDateSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Iterator<FacesMessage> it = context.getMessages();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}

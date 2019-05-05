/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class AccountFormValidator implements Validator {

    @Override
    public boolean supports(Class c) {
        return AccountForm.class.equals(c);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountForm form = (AccountForm) target;
        
        if (form.getUsername() == null || form.getUsername().trim().isEmpty()) {
            errors.rejectValue("username", "error.usernameEmptyOrNull", "Käyttäjätunnusta ei voi jättää tyhjäksi");
        }
        
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            errors.rejectValue("name", "error.nameEmptyOrNull", "Nimeä ei voi jättää tyhjäksi");
        }
                
        if (form.getProfile() == null || form.getProfile().trim().isEmpty()) {
            errors.rejectValue("username", "error.profileEmptyOrNull", "Profiilin osoite ei voi olla tyhjä");
        }
        
        if (form.getPassword() == null || form.getPassword().trim().isEmpty()) {
            errors.rejectValue("password", "error.passwordEmptyOrNull", "Salasanaa ei voi jättää tyhjäksi");
        }
            
        if (!form.getPassword().equals(form.getConfirmation())) {
            errors.rejectValue("confirmation", "error.passwordsDontMatch", "Salasanat eivät täsmää");
        }
    }   
}

package projekti;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountForm {
    
    private String username;  
    private String password;
    private String confirmation;     
    private String name;
    private String profile;    
}

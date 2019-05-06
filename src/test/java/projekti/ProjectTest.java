package projekti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectTest extends org.fluentlenium.adapter.junit.FluentTest {

    @LocalServerPort
    private Integer port;
    
    @Test
    public void testProject() {
        // Avaa rekisteröitymissivu
        goTo("http://localhost:" + port + "/register");
        //Täytä tiedot
        find("#inputUsername").fill().with("Rolle");
        find("#inputPassword").fill().with("hunter2");
        find("#inputConfirmation").fill().with("hunter2");
        find("#inputName").fill().with("Rolle Polle");
        find("#inputProfile").fill().with("rolle");
        find("form").first().submit();
        //Uudelleenohjaa kirjautumissivulle
        assertTrue(window().title().contains("Kirjaudu sisään"));
        //Täytä tiedot väärin
        find("#inputUsername").fill().with("Rolle");
        find("#inputPassword").fill().with("Hunter2");
        find("form").first().submit();
        //Väärät tiedot uudelleenohjaa kirjautumissivulle
        assertTrue(window().title().contains("Kirjaudu sisään"));
        
        //Tarkista, että käyttäjänimen kirjainkoko merkitsee
        find("#inputUsername").fill().with("rolle");
        find("#inputPassword").fill().with("hunter2");
        find("form").first().submit();
        assertTrue(window().title().contains("Kirjaudu sisään"));
        
        //Täytä tiedot oikein
        find("#inputUsername").fill().with("Rolle");
        find("#inputPassword").fill().with("hunter2");
        find("form").first().submit();
        //Uudelleenohjaus omalle profiilisivulle
        assertTrue(window().title().contains("Profiili"));
        //Nimen pitäisi näkyä jossain, jos kyseessä on oikea profiili
        assertTrue(pageSource().contains("Rolle Polle"));
        //Lähetä uusi viesti
        find("#newMessageTextarea").fill().with("viestin sisältö");
        find("#newMessageForm").first().submit();
        //Viestin sisällön pitäisi näkyä sivulla
        assertTrue(pageSource().contains("viestin sisältö"));
        //Pitempikin viesti tulisi toimia
        find("#newMessageTextarea").fill().with("Sed ut perspiciatis unde omnis "
                + "iste natus error sit voluptatem accusantium doloremque "
                + "laudantium, totam rem aperiam, eaque ipsa quae ab illo "
                + "inventore veritatis et quasi architecto beatae vitae "
                + "dicta sunt explicabo. Nemo enim ipsam voluptatem quia "
                + "voluptas sit aspernatur aut odit aut fugit, sed quia "
                + "consequuntur magni dolores eos qui ratione voluptatem "
                + "sequi nesciunt. Neque porro quisquam est, qui dolorem "
                + "ipsum quia dolor sit amet, consectetur, adipisci velit, "
                + "sed quia non numquam eius modi tempora incidunt ut labore "
                + "et dolore magnam aliquam quaerat voluptatem. Ut enim ad "
                + "minima veniam, quis nostrum exercitationem ullam corporis "
                + "suscipit laboriosam, nisi ut aliquid ex ea commodi "
                + "consequatur? Quis autem vel eum iure reprehenderit qui in "
                + "ea voluptate velit esse quam nihil molestiae consequatur, "
                + "vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");
        find("#newMessageForm").first().submit();
        assertTrue(pageSource().contains("exercitationem ullam"));
        for (int i = 0; i < 23; i++) {
            find("#newMessageTextarea").fill().with("25 viestiä täyteen");
            find("#newMessageForm").first().submit();
        }
        assertTrue(pageSource().contains("25 viestiä täyteen"));
        assertTrue(pageSource().contains("viestin sisältö"));
        assertTrue(pageSource().contains("exercitationem ullam"));
        find("#newMessageTextarea").fill().with("viesti numero 26");
        find("#newMessageForm").first().submit();
        //Ensimmäisenä lähetetty viesti ei näy enää
        assertFalse(pageSource().contains("viestin sisältö"));
        assertTrue(pageSource().contains("exercitationem ullam"));
        find("#newMessageTextarea").fill().with("viesti numero 27");
        find("#newMessageForm").first().submit();
        //Toisenakaan lähetetty viesti ei näy enää
        assertFalse(pageSource().contains("viestin sisältö"));
        assertFalse(pageSource().contains("exercitationem ullam"));
        assertTrue(pageSource().contains("25 viestiä täyteen"));
    }
}

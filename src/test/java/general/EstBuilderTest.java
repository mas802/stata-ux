package general;

import java.util.Arrays;
import java.util.List;

import istata.domain.EstBean;
import istata.service.StataService;

import org.junit.Test;

public class EstBuilderTest {

    @Test
    public void handleUpdatesTest() {

        StataService service = new StataService();

        EstBean e1 = new EstBean();
        e1.setCommand("cmd1");

        EstBean e2 = new EstBean();
        e2.setCommand("<i> mod");

        EstBean e3 = new EstBean();
        e3.setCommand("<i0>");

        EstBean e4 = new EstBean();
        e4.setCommand("<i>");

        List<EstBean> result = service.realiseEstDo(Arrays.asList(e1,e2,e3,e4));

        for ( EstBean e: result ) {
            System.out.println( e.getCommand() );
    
        }
    }
}

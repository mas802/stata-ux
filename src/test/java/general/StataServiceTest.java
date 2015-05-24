package general;

import istata.service.StataService;

import org.junit.Test;

public class StataServiceTest {

    @Test
    public void handleUpdatesTest() {

        StataService service = new StataService();

        service.handleUpdate("{res}{sf}{ul off}{txt}\n" + "{com}.");
        service.handleUpdate(" use \"dta/hrv_happy_analysis_20150514.dta\", clear\n");
        service.handleUpdate("{txt}");

        System.out.println(service.results(""));
    }
}

package stata.utils;

import static org.junit.Assert.*;
import istata.interact.StataUtils;

import org.junit.Test;

public class ScmlTest {

    @Test
    public void test() {
        String s = "{res}{sf}{ul off}{txt}\n"+
                    "{com}. use \"dta/hrv_happy_analysis_20150514.dta\", clear\n"+
                    "{txt}";
        
        System.out.println(StataUtils.smcl2plain(s, false));
    }

}

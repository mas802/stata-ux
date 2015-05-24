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

    @Test
    public void testHline() {

        final long start = System.currentTimeMillis();

        String r = null;
        for ( int i=0;i<1;i++) {
        String s = "{res}{sf}{ul off}{txt}\n"+
                    "{com}. {hline 100}"+
                    "{txt}";
        
        r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);
        
        System.out.println( System.currentTimeMillis() - start);

        assertEquals(104, r.length());

    }


    @Test
    public void testSpace() {

        final long start = System.currentTimeMillis();

        String r = null;
        for ( int i=0;i<1;i++) {
        String s = "{res}{sf}{ul off}{txt}\n"+
                    "{com}. {space 100}"+
                    "{txt}";
        
        r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);
        
        System.out.println( System.currentTimeMillis() - start);
        
        assertEquals(104, r.length());

    }
    

    @Test
    public void testHlite() {

        final long start = System.currentTimeMillis();

        String r = null;
        for ( int i=0;i<1;i++) {
        String s = "{res}{sf}{ul off}{txt}\n"+
                    "{com}. {hilite:harmby}"+
                    "{txt}";
        
        r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);
        
        System.out.println( System.currentTimeMillis() - start);
        
        assertEquals(10, r.length());

    }
    
    @Test
    public void testHelp() {

        final long start = System.currentTimeMillis();

        String r = null;
        for ( int i=0;i<1;i++) {
        String s = "{res}{sf}{ul off}{txt}\n"+
                    "{com}. {help j_robustsingular##|_new:F(14,2342)}= ."+
                    "{txt}";
        
        r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);
        
        System.out.println( System.currentTimeMillis() - start);
        
        assertEquals(17, r.length());

    }
    
    @Test
    public void testCmd() {

        final long start = System.currentTimeMillis();

        String r = null;
        for ( int i=0;i<1;i++) {
        String s = "{res}{sf}{ul off}{txt}\n"+
                    "{com}. -{cmd:ssc describe z}-"+
                    "{txt}";
        
        r = StataUtils.smcl2plain(s, false);
        }
        System.out.println(r.length() + " - \n\n" + r);
        
        System.out.println( System.currentTimeMillis() - start);
        
        assertEquals(20, r.length());

    }

}

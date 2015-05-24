package stata.utils;

import istata.interact.IStata;
import istata.interact.IStataListener;
import istata.interact.StataFactory;
import istata.interact.StataNotRunningException;
import istata.interact.model.StataResult;

import org.junit.Test;

public class StataRunTest {

    @Test
    public void testUpdate() {

        final long start = System.currentTimeMillis();

        IStata stata = new StataFactory().getInstance();

        IStataListener l = new IStataListener() {

            @Override
            public void handleUpdate(String update) {

                System.out.println(update);
                System.out.println(System.currentTimeMillis() - start);
            }

            @Override
            public void handleResult(StataResult result) {
                // TODO Auto-generated method stub

            }
        };

        stata.addStataListener(l);

        try {
            stata.run("sysuse auto");
        } catch (RuntimeException e) {
            // make sure test is succesful
            System.err.println("Runtime Error " + e.getMessage());
        }

    }

    @Test
    public void testWorkingdir() {

        // final long start = System.currentTimeMillis();

        IStata stata = new StataFactory().getInstance();

        try {
            stata.getVars("", true);
            String wd = stata.getWorkingdir();
            System.out.println(wd);
        } catch (StataNotRunningException e) {
            // make sure test is succesful
            System.err.println("Runtime Error " + e.getMessage());
        }

    }
}

package general;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import istata.domain.CmdRepository;
import istata.domain.ContentLine;
import istata.interact.IStata;
import istata.interact.IStataListener;
import istata.interact.StataFactory;
import istata.interact.model.StataVar;
import istata.service.StataService;


@RunWith(MockitoJUnitRunner.class)
public class SuggestTest {
    

    @Mock
    private StataFactory stataFactory;

    @InjectMocks
    private StataService service = new StataService();

    @Mock
    private CmdRepository cmdRepository;
    
    @Mock
    private IStata stata;
    
    @Test
    public void someTest() {
        // Mockito.stubVoid(stataFactory.addStataListener(service));
        
        String[] cmd = {"reg this","rag that","sum me", "sum me"};
        List<ContentLine> cmds = new ArrayList<ContentLine>();
        for ( String s:cmd ) {
            ContentLine cl = new ContentLine();
            cl.setContent(s);
            cmds.add(cl);
        }

        
        String[] var = {"one one      |", "one1  one1     |", "summary summary      |"};
        List<StataVar> vars = new ArrayList<StataVar>();
        for ( String s:var ) {
            StataVar cl = new StataVar( s + "");
            vars.add(cl);
        }
        
        Mockito.when( cmdRepository.findAll()).thenReturn( cmds );
        Mockito.when( stataFactory.getInstance()).thenReturn( stata );
        Mockito.when( stata.getVars("", false)).thenReturn( vars );
        
        List<ContentLine> list = service.cmdFiltered("sum", -1);
        
        for ( ContentLine cl:list) {
            System.out.println(cl.getContent());
        }
        
        Assert.assertEquals(2, list.size());
    }
    
    
    @Test
    public void removeDuplicatesTest() {
        // Mockito.stubVoid(stataFactory.addStataListener(service));
        
        String[] cmd = {"reg this","rag that","sum me", "sum me"};
        List<ContentLine> cmds = new ArrayList<ContentLine>();
        for ( String s:cmd ) {
            ContentLine cl = new ContentLine();
            cl.setContent(s);
            cmds.add(cl);
        }

        
        String[] var = {"one one      |", "one1  one1     |", "summary summary      |"};
        List<StataVar> vars = new ArrayList<StataVar>();
        for ( String s:var ) {
            StataVar cl = new StataVar( s + "");
            vars.add(cl);
        }
        
        Mockito.when( cmdRepository.findAll()).thenReturn( cmds );
        Mockito.when( stataFactory.getInstance()).thenReturn( stata );
        Mockito.when( stata.getVars("", false)).thenReturn( vars );
        
        List<ContentLine> list = service.cmdFiltered("sum m", -1);
        
        for ( ContentLine cl:list) {
            System.out.println(cl.getContent());
        }
        
        Assert.assertEquals(1, list.size());
    }
}

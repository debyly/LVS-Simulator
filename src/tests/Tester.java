package tests;

import model.LVS;
import model.LineController;
import org.junit.jupiter.api.Test;

public class Tester {

    @Test
    public void lineControllerTest(){

        LineController controller = new LineController(true,LVS.testLVS(10,0.00005,0.0002,0.0005,0.0005));


    }
}

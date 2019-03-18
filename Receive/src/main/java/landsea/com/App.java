package landsea.com;

import landsea.com.receive.*;

import java.util.logging.Logger;

/**
 * <Description>
 *
 * @author tongziqi
 * @version 1.0
 * @createDate 2019/02/14 13:58
 * @see landsea.com
 */

public class App {

    public static void main(String[] args) {
        Thread receive_notice  = new Receive_notice();
        Thread receive_typhoon = new Receive_typhoon();
        Thread receive_weather = new Receive_weather();
        Thread receive_state = new Receive_state();
        Thread Receive_typhoonList = new Receive_typhoonList();
        Receive_typhoonList.start();
        receive_notice.start();
        receive_typhoon.start();
        receive_weather.start();
        receive_state.start();

    }
}

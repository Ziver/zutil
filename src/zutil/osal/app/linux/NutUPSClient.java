package zutil.osal.app.linux;

import zutil.StringUtil;
import zutil.Timer;
import zutil.log.LogUtil;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a java interface for the linux service "upsd" from the
 * The NUT (Network UPS Tools) package.
 *
 * Created by Ziver on 2016-04-01.
 */
public class NutUPSClient {
    private static final Logger logger = LogUtil.getLogger();

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 3493;
    public static final int POLL_INTERVAL = 2000;
    private static final Pattern keyValuePattern = Pattern.compile("\\W*:\\W*");

    private static final String PARAMETER_MANUFACTURER = "ups.mfr";
    private static final String PARAMETER_MODEL = "ups.model";
    private static final String PARAMETER_DESCRIPTION = "ups.description";
    private static final String PARAMETER_POWER_LOAD = "ups.load"; //ups.power
    private static final String PARAMETER_POWER_USAGE = "ups.realpower"; //ups.power
    private static final String PARAMETER_BATTERY_CHARGE = "battery.charge";
    private static final String PARAMETER_BATTERY_VOLTAGE = "battery.voltage";
    private static final String PARAMETER_POLL_INTERVAL = "driver.parameter.pollinterval";


    private String host;
    private int port;
    private ArrayList<UPSDevice> upsDevices;
    private Timer pollTimer;


    public NutUPSClient(String host, int port){
        this.upsDevices = new ArrayList<>();
        this.host = host;
        this.port = port;
        this.pollTimer = new Timer(POLL_INTERVAL);
    }


    public UPSDevice getUPS(String id){
        update();
        return __getUPS(id);
    }
    private UPSDevice __getUPS(String id){
        for (UPSDevice ups : upsDevices){
            if (ups.equals(id))
                return ups;
        }
        return null;
    }

    public UPSDevice[] getUPSList(){
        update();
        return upsDevices.toArray(new UPSDevice[0]);
    }


    protected synchronized void update(){
        if(pollTimer.hasTimedOut()){
            logger.fine("Starting UPS data refresh ("+host+":"+port+")");
            try(Socket s = new Socket(host, port)) {
                Writer out = new OutputStreamWriter(s.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                // Refresh device list
                HashMap<String,String> tmp = new HashMap<>();
                sendListCommand(out, in, "UPS", tmp);
                for (String upsId : tmp.keySet()){
                    if(__getUPS(upsId) == null) {
                        logger.fine("Registering new UPS device: "+upsId);
                        upsDevices.add(new UPSDevice(upsId));
                    }
                }

                // Refresh device data
                for (UPSDevice ups : upsDevices){
                    ups.update(out, in);
                }

            } catch (Exception e){
                logger.log(Level.WARNING, null, e);
            }
            // reset timer
            pollTimer.start();
        }
    }

    /**
     * Reference http://networkupstools.org/docs/developer-guide.chunked/ar01s09.html
     */
    private void sendListCommand(Writer out, BufferedReader in, String cmd, HashMap<String,String> parameters) throws IOException {
        String request = "LIST " + cmd;
        out.write(request + "\n");
        out.flush();

        String line = in.readLine();
        if ( ! line.startsWith("BEGIN LIST"))
            throw new IOException("Unexpected response from upsd: Request: '"+request+"' Response: '"+line+"'");

        Pattern listKeyValuePatter = Pattern.compile("\\w* (?:\\w* )?([\\w.]+) \"(.*)\"");
        while ((line=in.readLine()) != null){
            if (line.startsWith("END"))
                break;
            Matcher m = listKeyValuePatter.matcher(line);
            m.matches();
            parameters.put(
                    m.group(1),
                    m.group(2));
        }
    }



    public class UPSDevice{
        private String id;
        private HashMap<String, String> parameters = new HashMap<>();


        protected UPSDevice(String id){
            this.id = id;
        }

        protected synchronized void update(Writer out, BufferedReader in) throws IOException {
            if(pollTimer == null || pollTimer.hasTimedOut()){
                parameters.clear();
                logger.fine("Updating UPS parameters for: "+id);
                sendListCommand(out, in, "VAR "+id, parameters);
            }
        }

        public boolean equals(Object o){
            if (o instanceof String)
                return id.equals(o);
            else if (o instanceof UPSDevice)
                return id.equals(((UPSDevice) o).id);
            return false;
        }


        public String getId(){
            return id;
        }
        public String getModelName(){
            return parameters.get(PARAMETER_MANUFACTURER) + " " + parameters.get(PARAMETER_MODEL);
        }
        public String getDescription() {
            return parameters.get(PARAMETER_DESCRIPTION);
        }
        public int getPowerLoad(){
            return Integer.parseInt(parameters.get(PARAMETER_POWER_LOAD));
        }
        public int getPowerUsage(){
            return Integer.parseInt(parameters.get(PARAMETER_POWER_USAGE));
        }
        public int getBatteryCharge(){
            return Integer.parseInt(parameters.get(PARAMETER_BATTERY_CHARGE));
        }
        public double getBatteryVoltage(){
            return Double.parseDouble(parameters.get(PARAMETER_BATTERY_VOLTAGE));
        }

    }
}

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
//import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileDownloaderMain {

	static InputStream inputStream;
	
	private static boolean stop = false;

	private static final Logger LOGGER = Logger.getLogger(FileDownloaderMain.class.getName());

	
    public static void start(String[] args) {
    	
        System.out.println("start");
        while (!stop) {

    		try {
    			LOGGER.info("Waiting 15s start delay");
    			TimeUnit.SECONDS.sleep(15);
    			
    			LOGGER.info("Reading configuration file..");
    			Properties prop = getPropValues();
    			URL file_url = new URL(prop.getProperty("file_url"));
    			String file_dest = new String(prop.getProperty("file_download_dir"));
    			long time_interval =  new Long(prop.getProperty("time_interval"));
    			
    			LOGGER.info("Configuration has been loaded from file config.properties");

    				while(!stop) {
    					LOGGER.info("Downloading file from: " + file_url);
    					long startTime = System.currentTimeMillis();
    		
    					DownloadFileDownloader downloader = new DownloadFileDownloader();
    					downloader.downloadFile(file_url, file_dest);
    		
    					long estimatedTime = System.currentTimeMillis() - startTime;
    		
    					LOGGER.info("File has been downloaded as " + file_dest + " in " + (estimatedTime) + "ms");
    					TimeUnit.SECONDS.sleep(time_interval);
    				}
    			} catch (Exception e) {
    			LOGGER.log(Level.SEVERE, "ERROR: ", e);
//    			System.exit(1);
    		}
        }
    }
 
    public static void stop(String[] args) {
        System.out.println("stop");
        stop = true;
    }	
	
    public static void main(String[] args) {
    	prepareLogger();
    	
        if ("start".equals(args[0])) {
            start(args);
        } else if ("stop".equals(args[0])) {
            stop(args);
        }
	}

	private static void prepareLogger() {
		Handler fileHandler = null;
		try {

			// Creating consoleHandler and fileHandler
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
			Calendar cal = Calendar.getInstance();

			String logfile_name = "logs/log_" + dateFormat.format(cal.getTime()) + ".txt";

			// This block configure the logger with handler and formatter
			fileHandler = new FileHandler(logfile_name, true);

			// Assigning handlers to LOGGER object
			LOGGER.addHandler(fileHandler);

			SimpleFormatter formatter = new SimpleFormatter();
			fileHandler.setFormatter(formatter);

			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);

		} catch (IOException exception) {
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}

		LOGGER.info("------- Logger has been started -------");
	}

	private static Properties getPropValues() throws IOException {

		Properties prop = new Properties();
		String propFileName = "config.properties";

		try {

			inputStream = FileDownloaderMain.class.getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "ERROR: ", e);
		} finally {
			inputStream.close();
		}
		return prop;
	}

}

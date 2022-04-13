package uk.ac.soton.comp1206.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;

/**
 * A file handler to deal with reading from/writing to files
 */
public class FileHandler {
    /**
     * The reader object
     */
    private BufferedReader reader;
    /**
     * The writer object
     */
    private FileWriter writer;
    /**
     * the name of the file
     */
    private String fileName;
    /**
     * The logger
     */
    private static final Logger logger = LogManager.getLogger(FileHandler.class);

    /**
     * Initialise the file handler
     * @param fileName the name of the file to be accessed
     */
    public FileHandler(String fileName){
        this.fileName = "src/main/resources/data/" + fileName;
    }

    /**
     * Check whether the file exists
     * @return if the file exists
     */
    public boolean fileExists(){
        return new File(fileName).exists();
    }

    /**
     * Set up the file reader
     */
    public void setReader(){
        try {
            reader = new BufferedReader(new FileReader(fileName));
            logger.info(reader);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * set up the file writer
     */
    public void setWriter(){
        try {
            writer = new FileWriter(fileName);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * Get a line from the reader
     * @return the line
     */
    public String getLine() {
        try {
            String line = reader.readLine();
            logger.info(line);
            return line;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    /**
     * Check whether a file is ready to be accessed
     * @return whether the file is ready
     */
    public boolean fileIsReady() {
        try {
            return (reader.ready());
        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }

    /**
     * Write a line to the file
     * @param line the line to be written
     */
    public void writeLine(String line){
        try {
            writer.append(line+"\n");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * Finish writing to the file and flush the output
     */
    public void writeFinish(){
        try {
            writer.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}

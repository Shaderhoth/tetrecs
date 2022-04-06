package uk.ac.soton.comp1206.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;

public class FileHandler {
    BufferedReader reader;
    FileWriter writer;
    String fileName;
    private static final Logger logger = LogManager.getLogger(FileHandler.class);
    public FileHandler(String fileName){
        this.fileName = "src/main/resources/data/" + fileName;
    }
    public boolean fileExists(){
        return new File(fileName).exists();
    }
    public void setReader(){
        try {
            reader = new BufferedReader(new FileReader(fileName));
            logger.info(reader);
        } catch (Exception e) {
            logger.error(e);
        }
    }
    public void setWriter(){
        try {
            writer = new FileWriter(fileName);
        } catch (Exception e) {
            logger.error(e);
        }
    }

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

    public boolean fileIsReady() {
        try {
            return (reader.ready());
        } catch (IOException e) {
            logger.error(e);
            return false;
        }
    }
    public void writeLine(String line){
        try {
            writer.append(line+"\n");
        } catch (IOException e) {
            logger.error(e);
        }
    }
    public void writeFinish(){
        try {
            writer.flush();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}

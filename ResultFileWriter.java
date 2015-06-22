package quest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
 
 
public class ResultFileWriter {
    File fileResult;
    PrintWriter out;
 
    public ResultFileWriter(String dir) {
        fileResult = new File(dir + "results.txt");
        try {
 
        	out = new PrintWriter(fileResult);
 
        } catch (FileNotFoundException e) {
           
            e.printStackTrace();
        }
    }
 
    public void close() {
    	out.close();
    }
 
    public void writeToFile(String s) {
        System.out.print(s);
        out.print(s);
    }
}
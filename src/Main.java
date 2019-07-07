import org.apache.commons.io.LineIterator;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;

import deidentification.module.filehandle.FileHandler;

public class Main
{
    
    public static void main(String[] args)
    {
        LineIterator iterator;
        FileHandler fileHandler;
        fileHandler = new FileHandler();
        int lines = 0;
        
        //============= sqlite ==============//
        
        
        SQLiteConfig config = new SQLiteConfig();
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
        
        SQLiteDataSource ds = new SQLiteDataSource(config);
        Connection con = null;
        Statement stat = null;
        try
        {
            ds.setUrl("jdbc:sqlite::memory:");
            con = ds.getConnection();
            
            if (null != con)
            {
                stat = con.createStatement();
                stat.executeUpdate("create table test(word TEXT);");
                System.out.println("SQLite create table finish");
            }
            
            //===================================
            
            // get total line count
            Instant lineCountStart = Instant.now();
            
            iterator = fileHandler.loadFile("plrabn12.txt", "UTF-8");
            String strSQL;
            while (iterator.hasNext())
            {
                String line = iterator.nextLine();
                if (null != stat)
                {
                    strSQL = "INSERT INTO test(word) VALUES('" + line + "');";
                    System.out.println("SQL: " + strSQL);
                    stat.executeUpdate("INSERT INTO test(word) VALUES('" + line + "');");
                }
                System.out.println(line);
                ++lines;
                if (10000000 < lines)
                {
                    break;
                }
            }
            
            ResultSet rs = stat.executeQuery("SELECT count(*) as total FROM test;");
            if (rs.next())
            {
                System.out.println("SQLite count:" + rs.getString("total"));
            }
            con.close();
            
            Instant lineCountEnd = Instant.now();
            
            long timeElapsedLineCount = Duration.between(lineCountStart, lineCountEnd).toMillis();
            
            System.out.println("total count:" + lines + " Line count time: " + timeElapsedLineCount + "ms");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}

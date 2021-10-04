package eigen;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Eigen {
    public static void main(String[] args) throws IOException
    {
        if(args.length > 1){
            System.out.println("Use \"eigen file\"");
        }
        else if(args.length == 1){
            runFile(args[0]);
        }
        else{
            runCLI();
        }
    }

    //run the file
    private static void runFile(String path) throws IOException{
        var bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void run(String charStream) throws IOException{
        var scanner =  new Scanner (charStream);
        var tokens = scanner.scanTokens();
    }

    //run code on CLI
    private static void runCLI() throws IOException{
    }
}

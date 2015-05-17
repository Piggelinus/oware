import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class TextEditor {
    
    final static String FILE_NAME = "knowledge-base.txt";
    
    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
    }
    
    public TextEditor() {
        try {
            removeLosingStates(FILE_NAME);
        } catch (Exception e) {}
    }
    
    public void removeLosingStates(String fname) throws FileFailedException {
        try {
            File file = new File(fname);
            File newFile = new File("edited-" + fname);
            FileWriter writer = new FileWriter(newFile);
            Scanner reader = new Scanner(file);
            int lines = 0;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (!line.contains(" 2") && !line.contains(" 0") && line.length() > 16) {
                    writer.write(line + "\n");
                    lines++;
                    System.out.println(lines);
                }
            }
            writer.close();
        }
        catch (Exception e) {
            //GameManagerImpl.out.println(e.getMessage());
        }
    }
}
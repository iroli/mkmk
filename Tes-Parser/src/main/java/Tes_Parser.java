import java.util.Scanner;
import org.w3c.dom.Document;


/**
 * Main class that contatin initializing sequence and Container class for shared data use.
 * @author Ivan Strebkov
 * @see Tes_Parser.Container Shared use data
 * @see Tes_Parser#main(String[]) Start point
 * @see Tes_Parser#InitSequence1(Container) Initializing sequence 1
 * @see Tes_Parser#InitSequence2(Container) Initializing sequence 2
 * */
public class Tes_Parser {
    /**
     * A Container class which is used to share data
     * @author Ivan Strebkov
     *  */
    public static class Container{
        String filepath = null;
        Document document = null;   // DOM tree is here.
        int doc_type = 0;           // -1 - Unrecognizable data; 0 - Empty file; 1 - Terms; 2 - Publications.
    }


    /**
     * Start point of the program. Starts 2 initializing sequencies and gives the control to the one of two file menus.
     * @author Ivan Strebkov
     * @param args - Parameters from user
     * @see Tes_Parser#InitSequence1(Container) Initializing sequence 1
     * @see Tes_Parser#InitSequence2(Container) Initializing sequence 2
     * @see TERMS#Terms_main(Container) Main terms menu
     * @see PUBLICATIONS#Publications_main(Container) Main publications menu
     * @see Tes_Parser.Container Shared use data
     */
    public static void main(String[] args) {
        int ret;

        // Shared data "variables" are being created.
        Container container = new Container();

        // Initializing.
        do {
            // Receiving and loading the file.
            ret = InitSequence1(container);
            if (ret == 1) { // Exit code was returned.
                return;
            }

            // Determining data type: Terms or Publications.
            ret = InitSequence2(container);
            if (ret == 1) { // Exit code was returned.
                return;
            } else if (ret == 0) {  // File successfully determined.
                break;
            }
        }
        while (true); // In case ret == -1 Initializing starts again (file change).

        // End of initializing.
        // Giving the control to corresponding management class.
        switch (container.doc_type) {
            case 1 -> TERMS.Terms_main(container);
            case 2 -> PUBLICATIONS.Publications_main(container);
        }
    }


    /**
     * Initializing sequence #1 that select, load and create (if needed) the resulting xml file.
     * @author Ivan Strebkov
     * @param container Shared use data
     * @return Returns the result of file loading:
     * 0 - file loaded;
     * 1 - file not loaded and exit must be executed.
     * @see Tes_Parser.Container Shared use data
     * @see FILE#FileSelect(Container) File path and name inserter
     * @see FILE#FileLoad(Container) File loader
     */
    public static int InitSequence1(Container container) {          //Получение и считывание файла
        Scanner in = new Scanner(System.in);
        container.document = null;

        do {
            // Reading file path.
            FILE.FileSelect(container);
            // Creating (if needed) and loading the file.
            FILE.FileLoad(container);
            if (container.document == null) {           // document remains null if no file loaded.
                label:
                while (true) {
                    System.out.println("\nRetry with another path or Quit program? (r|q):");
                    String ans = in.next();
                    in.nextLine();
                    switch (ans) {
                        case "r":
                        case "R":
                            // Brake the cycle for the new path reading.
                            break label;
                        case "q":
                        case "Q":
                            // Exit program.
                            return 1;
                        default:
                            System.out.print("\nIncorrect input. Try again.");
                            break;
                    }
                }
            }
        }
        while (container.document == null);
        return 0;
    }


    /**
     * Initializing sequence #2 that determines the type of the loaded file.
     * @author Ivan Strebkov
     * @param container Shared use data
     * @return Returns the result of file type determination sequence:
     * -1 - file type not determined and file must be changed;
     * 0 - file type determined;
     * 1 - file type not determined and exit must be executed.
     * @see Tes_Parser.Container Shared use data
     * @see DOM#GetDOMType(Document) Xml tree type recognition
     */
    public static int InitSequence2(Container container) {
        Scanner in = new Scanner(System.in);

        int doc_type = DOM.GetDOMType(container.document);
        if (doc_type > 0) {
            // File type successfully determined.
            switch (doc_type) {
                case 1 -> System.out.print("\nFile type: Terms.\n");
                case 2 -> System.out.print("\nFile type: Publications.\n");
            }
            container.doc_type = doc_type;
        } else if (doc_type == 0) {
            // File is empty.
            label:
            while (true) {
                System.out.print("""
                        
                        File has unknown type (<collection> is empty). Select file type:
                        1 -- Terms
                        2 -- Publications
                        """);
                String ans = in.next();
                in.nextLine();
                switch (ans) {
                    case "1":
                        container.doc_type = 1;
                        break label;
                    case "2":
                        container.doc_type = 2;
                        break label;
                    default:
                        System.out.println("\nIncorrect input. Try again.");
                        break;
                }
            }
        } else {
            // File is not empty but contains unexpected data.
            while (true) {
                System.out.print("\nFile contains unknown data.\nRetry with another path or Quit program? (r|q):\n");
                String ans = in.next();
                in.nextLine();
                switch (ans) {
                    case "r":
                    case "R":
                        // Initialization must be done again (file change).
                        return -1;
                    case "q":
                    case "Q":
                        // Exit program.
                        return 1;
                    default:
                        System.out.println("\nIncorrect input. Try again.");
                        break;
                }
            }
        }
        return 0; // Successful completion.
    }
}
import org.w3c.dom.Node;
import java.util.List;
import java.util.Scanner;

public class Terms_Management {
    public static void Terms_main(Tes_Parser.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;

        //Меню появляется циклически до момента выхода.
        while (true) {
            System.out.print("""
                    
                    Select an option:
                    f  -- Find a term
                    e  -- Show Empty terms
                    w  -- Save (Write) changes
                    q  -- Quit program
                    wq -- Save (Write) changes and Quit program
                    """);
            ans = in.next();
            in.nextLine();
            label1:
            switch (ans) {
                case "f", "F" -> Term_menu(container);
                case "e", "E" -> Show_Empty(container);
                case "w", "W" -> File_Management.FileSave(container);
                case "q", "Q" -> {
                    label2:
                    while (true) {
                        System.out.print("\nAre you sure? All unsaved changes will be lost! (y|n):\n");
                        ans = in.next();
                        in.nextLine();
                        switch (ans) {
                            case "y":
                            case "Y":
                                break label2;
                            case "n":
                            case "N":
                                break label1;
                            default:
                                System.out.print("\nIncorrect input. Try again.");
                                break;
                        }
                    }
                    return;
                }
                case "wq", "wQ", "Wq", "WQ" -> {
                    File_Management.FileSave(container);
                    return;
                }
                default -> System.out.println("\nIncorrect input. Try again.");
            }
        }
    }



    public static void Term_menu(Tes_Parser.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;

        Node concept;

        concept = DOM_Management.GetNode(container);
        if (concept == null) {
            System.out.print("\n[UNEXPECTED ERROR: NODE DOES NOT EXIST.\n");
            return;
        }
        String concept_code = DOM_Management.GetProp("code", concept);

        label:
        while (true) {
            System.out.printf("""

                    Term %s. Select an option:
                    ` -- Show name
                    1 -- Edit name
                    2 -- Show related terms
                    3 -- Add related term
                    4 -- Remove related term
                    5 -- Show related publications
                    6 -- Add related publication
                    7 -- Remove related publication
                    q -- Quit to main menu
                    """, concept_code);
            ans = in.next();
            in.nextLine();
            switch (ans) {
                case "`":
                    System.out.printf("\nTerm Name:\n%s\n", DOM_Management.GetProp("term", concept));
                    break;
                case "1":
                    System.out.print("\nInsert term name:\n");
                    ans = in.nextLine();
                    DOM_Management.SetProp("term", ans, concept);
                    break;
                case "2":
                    break;
                case "3":
                    break;
                case "4":
                    break;
                case "5":
                    break;
                case "6":
                    break;
                case "7":
                    break;
                case "q":
                case "Q":
                    break label;
                default:
                    System.out.println("\nIncorrect input. Try again.");
                    break;
            }
        }
//        concept.getLastChild().setTextContent("test");
    }



    public static void Show_Empty(Tes_Parser.Container container) {
        List<String> nodes = DOM_Management.ScanEmpty(container.document);
        System.out.print("\nList of terms witout names:");
        for (int i = 0; i < nodes.size(); ++i) {
            if (i % 8 == 0) {
                System.out.println();
            }
            System.out.printf("%s", nodes.get(i));
            if (i + 1 < nodes.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }
}

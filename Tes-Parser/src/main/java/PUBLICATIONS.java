import org.w3c.dom.Node;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PUBLICATIONS {
    public static void Publications_main(Tes_Parser.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;

        //Меню появляется циклически до момента выхода.
        while (true) {
            System.out.print("""
                    
                    Select an option:
                    f  -- Find a publication
                    e  -- Show Empty publications
                    w  -- Save (Write) changes
                    q  -- Quit program
                    wq -- Save (Write) changes and Quit program
                    """);
            ans = in.next();
            in.nextLine();
            label1:
            switch (ans) {
                case "f", "F" -> Publication_menu(container);
                case "e", "E" -> DOM.Show_Empty(container);
                case "w", "W" -> FILE.FileSave(container);
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
                    FILE.FileSave(container);
                    return;
                }
                default -> System.out.println("\nIncorrect input. Try again.");
            }
        }
    }



    public static void Publication_menu(Tes_Parser.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;

        Node publication;

        publication = DOM.FindNode(container);
        if (publication == null) {
            System.out.print("\n[UNEXPECTED ERROR: NODE DOES NOT EXIST.\n");
            return;
        }
        String publication_code = Objects.requireNonNull(DOM.GetProp("code", publication)).getTextContent();

        label:
        while (true) {
            System.out.printf("""

                    Term %s. Select an option:
                    `  -- Show name
                    1  -- Edit name
                    2  -- Show authors
                    3  -- Add author
                    4  -- Remove author
                    5  -- Show year
                    6  -- Edit year
                    7  -- Show pages
                    8  -- Edit pages
                    9  -- Show link
                    0  -- Edit link
                    sr -- Show raw term content
                    er -- Edit raw term content
                    rm -- Remove current term
                    q  -- Quit to main menu
                    """, publication_code);
            ans = in.next();
            in.nextLine();
            List<String> props;
            String value;
            switch (ans) {
                case "`":
                    System.out.printf("\nPublication Name:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("title", publication)).getTextContent());
                    break;


                case "1":
                    System.out.print("\nInsert publication name:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("title", publication)).setTextContent(ans);
                    break;


                case "2":
                    props = DOM.GetPropList("author", publication);
                    System.out.print("\nList of authors:\n");
                    for (int i = 0; i < props.size(); ++i) {
                        System.out.printf("%d -- %s\n", i + 1, props.get(i));
                    }
                    break;


                case "3":
                    System.out.print("\nInsert author:\n");
                    value = in.nextLine();
                    if (DOM.GetPropByValue("author", value, publication) != null) {
                        System.out.printf("Author %s already exist.\n", value);
                        break;
                    }
                    DOM.CreateProp("author", value, publication, container);
                    System.out.printf("Author %s added to publication %s.\n", value, publication_code);
                    break;


                case "4":
                    System.out.print("\nInsert author:\n");
                    value = in.nextLine();
                    Node prop = DOM.GetPropByValue("author", value, publication);
                    if (prop == null) {
                        System.out.printf("Author %s does not exist.\n", value);
                        break;
                    }
                    DOM.RemoveProp("author", value, publication);
                    System.out.printf("Author %s removed from term %s.\n", value, publication_code);
                    break;


                case "5":
                    System.out.printf("\nYear:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("year", publication)).getTextContent());
                    break;


                case "6":
                    System.out.print("\nInsert year:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("year", publication)).setTextContent(ans);
                    break;


                case "7":
                    System.out.printf("\nPages:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("pages", publication)).getTextContent());
                    break;


                case "8":
                    System.out.print("\nInsert pages:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("pages", publication)).setTextContent(ans);
                    break;


                case "9":
                    System.out.printf("\nLink:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("link", publication)).getTextContent());
                    break;


                case "0":
                    System.out.print("\nInsert link:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("link", publication)).setTextContent(ans);
                    break;


                case "sr":
                case "sR":
                case "Sr":
                case "SR":
                    System.out.printf("\nPublication raw content:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("raw", publication)).getTextContent());
                    break;


                case "er":
                case "eR":
                case "Er":
                case "ER":
                    System.out.print("\nInsert publication raw content:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("raw", publication)).setTextContent(ans);
                    break;


                case "rm":
                case "rM":
                case "Rm":
                case "RM":
                    boolean flag = true;
                    while (flag) {
                        System.out.print("\nAre you sure (y|n): ");
                        ans = in.next();
                        in.nextLine();
                        switch (ans) {
                            case "y":
                            case "Y":
                                Node root = container.document.getDocumentElement();
                                root.removeChild(publication);
                                System.out.printf("\nPublication %s was removed.\n", publication_code);
                                break label;
                            case "n":
                            case "N":
                                flag = false;
                                break;
                            default:
                                System.out.println("\nIncorrect input. Try again.");
                                break;
                        }
                    }
                    break;


                case "q":
                case "Q":
                    break label;


                default:
                    System.out.println("\nIncorrect input. Try again.");
                    break;
            }
        }
    }
}

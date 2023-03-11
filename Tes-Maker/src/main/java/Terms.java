import org.w3c.dom.Node;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Terms {
    public static void Terms_main(Tes_Editor.Container container) {
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
                case "e", "E" -> DOM.Show_Empty(container);
                case "w", "W" -> File.FileSave(container);
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
                    File.FileSave(container);
                    return;
                }
                default -> System.out.println("\nIncorrect input. Try again.");
            }
        }
    }



    public static void Term_menu(Tes_Editor.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;

        Node concept;
        Node rel_concept;

        concept = DOM.FindNode(container);
        if (concept == null) {
            System.out.print("\n[UNEXPECTED ERROR: NODE DOES NOT EXIST.\n");
            return;
        }
        String concept_code = Objects.requireNonNull(DOM.GetProp("code", concept)).getTextContent();

        label:
        while (true) {
            System.out.printf("""

                    Term %s. Select an option:
                    `  -- Show name
                    1  -- Edit name
                    2  -- Show related terms
                    3  -- Add related term
                    4  -- Remove related term
                    5  -- Show related publications
                    6  -- Add related publication
                    7  -- Remove related publication
                    sr -- Show raw term content
                    er -- Edit raw term content
                    rm -- Remove current term
                    q  -- Quit to main menu
                    """, concept_code);
            ans = in.next();
            in.nextLine();
            List<String> props;
            String value;
            Node prop;
            switch (ans) {
                case "`":
                    System.out.printf("\nTerm Name:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("term", concept)).getTextContent());
                    break;


                case "1":
                    System.out.print("\nInsert term name:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("term", concept)).setTextContent(ans);
                    break;


                case "2":
                    props = DOM.GetPropList("related", "term", concept);
                    System.out.print("\nList of related terms without names:\n");
                    for (int i = 0; i < props.size(); ++i) {
                        System.out.printf("%d -- %s\n", i + 1, props.get(i));
                    }
                    break;


                case "3":
                    System.out.print("\nInsert term code:\n");
                    value = in.nextLine();
                    if (DOM.GetPropByValue("related", "term", value, concept) != null) {
                        System.out.printf("Relation with term %s already exist.\n", value);
                        break;
                    }
                    DOM.CreateProp("related", value, "term", concept, container);
                    System.out.printf("Relation with %s added to term %s.\n", value, concept_code);
                    rel_concept = DOM.GetNode(value, container);
                    if (rel_concept == null) {
                        DOM.CreateNode(value, container);
                        System.out.printf("Created node with code: %s\n", value);
                    }
                    rel_concept = DOM.GetNode(value, container);
                    assert rel_concept != null;
                    DOM.CreateProp("related", concept_code, "term", rel_concept, container);
                    System.out.printf("Relation with %s added to term %s.\n", concept_code, value);
                    break;


                case "4":
                    System.out.print("\nInsert term code:\n");
                    value = in.nextLine();
                    prop = DOM.GetPropByValue("related", value, "term", concept);
                    if (prop == null) {
                        System.out.printf("Relation with term %s does not exist.\n", value);
                        break;
                    }
                    DOM.RemoveProp("related", value, "term", concept);
                    System.out.printf("Relation with %s removed from term %s.\n", value, concept_code);
                    rel_concept = DOM.GetNode(value, container);
                    if (rel_concept == null) {
                        System.out.printf("Related node with code %s does not exist.\n", value);
                        break;
                    }
                    DOM.RemoveProp("related", concept_code, "term", rel_concept);
                    System.out.printf("Relation with %s removed from term %s.\n", concept_code, value);
                    break;


                case "5":
                    props = DOM.GetPropList("related", "source", concept);
                    System.out.print("\nList of related publications without names:\n");
                    for (int i = 0; i < props.size(); ++i) {
                        System.out.printf("%d -- %s\n", i + 1, props.get(i));
                    }
                    break;


                case "6":
                    System.out.print("\nInsert publication code:\n");
                    value = in.nextLine();
                    if (DOM.GetPropByValue("related", "source", value, concept) != null) {
                        System.out.printf("Relation with publication %s already exist.\n", value);
                        break;
                    }
                    DOM.CreateProp("related", value, "source", concept, container);
                    System.out.printf("\nRelated publication %s added to term %s.\n", value, concept_code);
                    break;


                case "7":
                    System.out.print("\nInsert publication code:\n");
                    value = in.nextLine();
                    prop = DOM.GetPropByValue("related", value, "term", concept);
                    if (prop == null) {
                        System.out.printf("Relation with publication %s does not exist.\n", value);
                        break;
                    }
                    DOM.RemoveProp("related", value, "source", concept);
                    System.out.printf("\nRelated publication %s removed from term %s.\n", value, concept_code);
                    break;


                case "sr":
                case "sR":
                case "Sr":
                case "SR":
                    System.out.printf("\nTerm raw content:\n%s\n",
                            Objects.requireNonNull(DOM.GetProp("raw", concept)).getTextContent());
                    break;


                case "er":
                case "eR":
                case "Er":
                case "ER":
                    System.out.print("\nInsert term raw content:\n");
                    ans = in.nextLine();
                    Objects.requireNonNull(DOM.GetProp("raw", concept)).setTextContent(ans);
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
                                root.removeChild(concept);
                                System.out.printf("\nTerm %s was removed.\n", concept_code);
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

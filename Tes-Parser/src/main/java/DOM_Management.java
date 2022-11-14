import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DOM_Management {
    public static int GetDOMType(Document document) {
        // Типы возвращааемых значений
        // -1 -- Содержит неизвестные данные
        //  0 -- <collection> пуст
        //  1 -- термины
        //  2 -- литература

        Node root = document.getDocumentElement();

        if (!root.getNodeName().equals("collection")) {
            return -1;
        }
        NodeList nodes = root.getChildNodes();
        if (nodes.getLength() == 0) {
            return 0;
        }
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.TEXT_NODE && node.getNodeName().equals("concept")) {
                return 1;
            } else if (node.getNodeType() != Node.TEXT_NODE && node.getNodeName().equals("publication")) {
                return 2;
            } else if (node.getNodeType() != Node.TEXT_NODE) {
                return -1;
            }
        }
        return 0;
    }


    public static Node GetNode(Tes_Parser.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;

        System.out.print("\nInsert code of the term:\n");
        ans = in.nextLine();
        String code = ans;

        Node root = container.document.getDocumentElement();
        while (true) {
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);
                if (node.getNodeType() != Node.TEXT_NODE) {
                    NodeList props = node.getChildNodes();
                    for (int j = 0; j < props.getLength(); ++j) {
                        Node prop = props.item(j);
                        if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals("code")) {
                            if (prop.getTextContent().equals(code)) {
                                return node;
                            }
                        }
                    }
                }
            }

            //Если нода не найдена
            label:
            while (true) {
                System.out.print("\nTerm does not exist. Create one? (y|n)\n");
                ans = in.next();
                in.nextLine();
                switch (ans) {
                    case "y":
                    case "Y":
                        CreateNode(code, container);
                        break label;
                    case "n":
                    case "N":
                        return null;
                    default:
                        System.out.println("\nIncorrect input. Try again.");
                        break;
                }
            }
        }
    }


    public static void CreateNode(String code, Tes_Parser.Container container) {
        Element node = container.document.createElement("concept");
        Element node_code = container.document.createElement("code");
        node_code.setTextContent(code);
        Element node_name = null;
        if (container.doc_type == 1) {
            node_name = container.document.createElement("term");
        } else if (container.doc_type == 2) {
            node_name = container.document.createElement("title");
        }
        node.appendChild(node_code);
        node.appendChild((node_name));
        if (container.doc_type == 2) {
            Element node_year = container.document.createElement("year");
            Element node_pages = container.document.createElement("pages");
            Element node_link = container.document.createElement("link");
            node.appendChild(node_year);
            node.appendChild(node_pages);
            node.appendChild(node_link);
        }
        container.document.getDocumentElement().appendChild(node);

        System.out.printf("\nCreated node with <code>: %s\n", code);
    }


    public static List<String> ScanEmpty(Document document) {
        List<String> out_nodes = new ArrayList<String>();
        NodeList nodes = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.TEXT_NODE) {
                NodeList props = node.getChildNodes();
                for (int j = 0; j < props.getLength(); ++j) {
                    Node prop = props.item(j);
                    if (prop.getNodeType() != Node.TEXT_NODE &&
                            (prop.getNodeName().equals("term") || prop.getNodeName().equals("title"))) {
                        if (prop.getTextContent().equals("")) {
                            for (int k = 0; k < props.getLength(); ++k) {
                                prop = props.item(k);
                                if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals("code")) {
                                    out_nodes.add(prop.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
        }

        return out_nodes;
    }


    public static String GetProp(String propname, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                return prop.getTextContent();
            }
        }
        return "";
    }
    public static String GetProp(String propname, String proptype, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                if (prop.hasAttributes()) {
                    NamedNodeMap attrs = prop.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        Node attr = attrs.item(j);
                        if (attr.getNodeName().equals("type") && attr.getTextContent().equals(proptype)) {
                            return prop.getTextContent();
                        }
                    }
                }
            }
        }
        return "";
    }


    public static void SetProp(String propname, String value, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                prop.setTextContent(value);
                System.out.println("Property set successfully.");
            }
        }
    }
    public static void SetProp(String propname, String proptype, String value, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                if (prop.hasAttributes()) {
                    NamedNodeMap attrs = prop.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        Node attr = attrs.item(j);
                        if (attr.getNodeName().equals("type") && attr.getTextContent().equals(proptype)) {
                            prop.setTextContent(value);
                        }
                    }
                }
            }
        }
    }
}

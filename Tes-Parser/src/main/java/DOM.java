import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DOM {
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


    public static Node FindNode(Tes_Parser.Container container) {
        Scanner in = new Scanner(System.in);
        String ans;
        String type = "";
        String Type = "";
        switch (container.doc_type) {
            case 1 -> {
                type = "term";
                Type = "Term";
            }
            case 2 -> {
                type = "publication";
                Type = "Publication";
            }
        }

        System.out.printf("\nInsert code of the %s:\n", type);
        String code = in.nextLine();

        while (true) {
            Node node = GetNode(code, container);
            if (node != null) {
                return node;
            }

            //Если нода не найдена
            label:
            while (true) {
                System.out.printf("\n%s does not exist. Create one? (y|n)\n", Type);
                ans = in.next();
                in.nextLine();
                switch (ans) {
                    case "y":
                    case "Y":
                        CreateNode(code, container);
                        System.out.printf("\nCreated %s with code: %s\n", type, code);
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
    public static Node GetNode(String code, Tes_Parser.Container container){
        NodeList nodes = container.document.getDocumentElement().getChildNodes();
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
        return null;
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
        node.appendChild(container.document.createElement("raw"));
        container.document.getDocumentElement().appendChild(node);
    }


    public static void Show_Empty(Tes_Parser.Container container) {
        List<String> nodes = DOM.ScanEmpty(container.document);
        switch (container.doc_type) {
            case 1 -> System.out.print("\nList of terms without names:");
            case 2 -> System.out.print("\nList of publications without names:");
        }
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
    public static List<String> ScanEmpty(Document document) {
        List<String> out_nodes = new ArrayList<>();
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


    public static void CreateProp(String propname, String value, Node node, Tes_Parser.Container container) {
        Element prop = container.document.createElement(propname);
        prop.setTextContent(value);
        node.appendChild(prop);
    }
    public static void CreateProp(String propname, String value, String proptype, Node node,
                                  Tes_Parser.Container container) {
        Element prop = container.document.createElement(propname);
        prop.setTextContent(value);
        prop.setAttribute("type", proptype);
        node.appendChild(prop);
    }


    public static void RemoveProp(String propname, String value, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)
                    && prop.getTextContent().equals(value)) {
                node.removeChild(prop);
                return;
            }
        }
        System.out.print("Prop is not found.\n");
    }
    public static void RemoveProp(String propname, String value, String proptype, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)
                    && prop.getTextContent().equals(value)) {
                if (prop.hasAttributes()) {
                    NamedNodeMap attrs = prop.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        Node attr = attrs.item(j);
                        if (attr.getNodeName().equals("type") && attr.getTextContent().equals(proptype)) {
                            node.removeChild(prop);
                            return;
                        }
                    }
                }
            }
        }
        System.out.print("Prop is not found.\n");
    }


    public static Node GetProp(String propname, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                return prop;
            }
        }
        return null;
    }
    /*public static Node GetProp(String propname, String proptype, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                if (prop.hasAttributes()) {
                    NamedNodeMap attrs = prop.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        Node attr = attrs.item(j);
                        if (attr.getNodeName().equals("type") && attr.getTextContent().equals(proptype)) {
                            return prop;
                        }
                    }
                }
            }
        }
        return null;
    }*/
    public static Node GetPropByValue(String propname, String value, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)
                    && prop.getTextContent().equals(value)) {
                return prop;
            }
        }
        return null;
    }
    public static Node GetPropByValue(String propname, String value, String proptype, Node node) {
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)
                    && prop.getTextContent().equals(value)) {
                if (prop.hasAttributes()) {
                    NamedNodeMap attrs = prop.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        Node attr = attrs.item(j);
                        if (attr.getNodeName().equals("type") && attr.getTextContent().equals(proptype)) {
                            return prop;
                        }
                    }
                }
            }
        }
        return null;
    }


    public static List<String> GetPropList(String propname, Node node) {
        List<String> out_nodes = new ArrayList<>();
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                out_nodes.add(prop.getTextContent());
            }
        }
        return out_nodes;
    }
    public static List<String> GetPropList(String propname, String proptype, Node node) {
        List<String> out_nodes = new ArrayList<>();
        NodeList props = node.getChildNodes();
        for (int i = 0; i < props.getLength(); ++i) {
            Node prop = props.item(i);
            if (prop.getNodeType() != Node.TEXT_NODE && prop.getNodeName().equals(propname)) {
                if (prop.hasAttributes()) {
                    NamedNodeMap attrs = prop.getAttributes();
                    for (int j = 0; j < attrs.getLength(); ++j) {
                        Node attr = attrs.item(j);
                        if (attr.getNodeName().equals("type") && attr.getTextContent().equals(proptype)) {
                            out_nodes.add(prop.getTextContent());
                        }
                    }
                }
            }
        }
        return out_nodes;
    }
}

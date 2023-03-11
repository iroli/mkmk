import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class File {
    public static void FileSelect(Tes_Editor.Container container) {
        String filepath;
        Scanner in = new Scanner(System.in);
        System.out.print("\nInsert target xml path:\n");
        filepath = in.next();
        in.nextLine();
        container.filepath = filepath;
    }


    public static void FileLoad(Tes_Editor.Container container) {
        Scanner in = new Scanner(System.in);

        try(FileInputStream fis = new FileInputStream(container.filepath)) {
            System.out.printf("\nFile founded. File size: %d bytes. \n", fis.available());
        }
        catch(IOException ex){
            label:
            while (true) {
                System.out.println("\nFile does not exist. Create one? (y|n):");
                String ans = in.next();
                in.nextLine();
                switch (ans) {
                    case "y":
                    case "Y":
                        //Создаётся файл
                        FileCreate(container);
                        break label;
                    case "n":
                    case "N":
                        //Выход
                        return;
                    default:
                        System.out.println("\nIncorrect input. Try again.");
                        break;
                }
            }
        }

        try {
            // Создается построитель документа
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Создается дерево DOM документа из файла
            container.document = documentBuilder.parse(container.filepath);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace(System.out);
        }
    }


    public static void FileCreate(Tes_Editor.Container container) {
        //
        String text =
                """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <collection/>""";   // инициализирующая строка xml
        try(FileOutputStream fos = new FileOutputStream(container.filepath))
        {
            // перевод строки в байты
            byte[] buffer = text.getBytes();

            fos.write(buffer, 0, buffer.length);
            System.out.println("\nFile has been created and initialized in xml format.");
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }


    public static void FileSave(Tes_Editor.Container container) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            // pretty print XML
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(container.document);
            FileOutputStream fos = new FileOutputStream(container.filepath);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}

import java.util.Scanner;
import org.w3c.dom.Document;



public class Tes_Parser {
    public static class Container{
        String filepath = null;
        Document document = null;
        int doc_type = 0;
    }



    public static void main(String[] args) {
        int ret;

        //Создаются "глобальные" переменные для отправки в методы
        Container container = new Container();

        //Инициализирующая последовательность
        do {
            //Получение и считывание файла
            ret = InitSequence1(container);
            if (ret == 1) { //Была возвращена команда на выход
                return;
            }

            //Определяем тип данных: термины или литература
            ret = InitSequence2(container);
            if (ret == 1) { //Была возвращена команда на выход
                return;
            } else if (ret == 0) {  //Была возвращена команда на смену файла
                break;
            }
        }
        while (true);

        //Конец инициализирующей последовательности
        //Передача управления В соответствующую функцию менеджмента
        switch (container.doc_type) {
            case 1 -> Terms_Management.Terms_main(container);
            case 2 -> Publications_Management.Publications_main(container);
        }
    }



    public static int InitSequence1(Container container) {          //Получение и считывание файла
        Scanner in = new Scanner(System.in);
        container.document = null;

        do {
            //Считывается путь до файла
            File_Management.FileSelect(container);
            //Создаётся (при надобности) и загружается файл
            File_Management.FileLoad(container);
            if (container.document == null) {
                label:
                while (true) {
                    System.out.println("\nRetry with another path or Quit program? (r|q):");
                    String ans = in.next();
                    in.nextLine();
                    switch (ans) {
                        case "r":
                        case "R":
                            //Цикл прерывается и выполнение идёт заново
                            break label;
                        case "q":
                        case "Q":
                            //Выход
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



    public static int InitSequence2(Container container) {          //Определяем тип данных: термины или литература
        Scanner in = new Scanner(System.in);

        int doc_type = DOM_Management.GetDOMType(container.document);
        if (doc_type > 0) {
            //По содержимому файла удалось определить тип
            switch (doc_type) {
                case 1 -> System.out.print("\nFile type: Terms.\n");
                case 2 -> System.out.print("\nFile type: Publications.\n");
            }
            container.doc_type = doc_type;
        } else if (doc_type == 0) {
            //Файл пуст
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
            //Файл непуст и содержит неизвестные данные
            while (true) {
                System.out.print("\nFile contains unknown data.\nRetry with another path or Quit program? (r|q):\n");
                String ans = in.next();
                in.nextLine();
                switch (ans) {
                    case "r":
                    case "R":
                        //Цикл прерывается и выполнение идёт заново
                        return -1;
                    case "q":
                    case "Q":
                        //Выход
                        return 1;
                    default:
                        System.out.println("\nIncorrect input. Try again.");
                        break;
                }
            }
        }
        return 0;
    }
}
import java.io.IOException;
import java.util.Scanner;

public class Menu {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] main) throws IOException {

        System.out.println("Write path to image you want to process: ");
        String path = sc.nextLine();
        Processing processing = new Processing(path);


        System.out.println("Processing Methods:\n 1. Regionprops (only monochrome)\n 2. Kirsch filtration (RGB or mono)\n" +
                "3. Zamkniecie elementem linijnym\n 4.Mapa odległości geodezyjnej\n5.exit");
        int choice = sc.nextInt();
            switch(choice){
                case 1:
                    processing.regionProps();
                    break;
                case 2:
                    processing.kirschFiltration();
                    break;
                case 3:
                    processing.closingLinear();
                    break;
                case 4:
                    processing.geodeticDistanceMap();
                    break;
                case 5:
                    System.exit(0);

                default:
                    System.out.println("Wrong input");
            }


    }
}

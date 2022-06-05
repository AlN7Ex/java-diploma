import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LinksSuggester {

    private Scanner scanner;
    private List<Suggest>  suggests = new ArrayList<>();

    public LinksSuggester(File file) throws IOException, WrongLinksFormatException {
        scanner = new Scanner(file);
    }

    public void check() {
        while (scanner.hasNext()) {
            String next = scanner.nextLine();
            String[] split = next.split("\\t");
            if (split.length != 3) {
                throw new WrongLinksFormatException("Wrong links structure");
            }
            else {
                suggests.add(new Suggest(split[0], split[1], split[2]));
            }
        }
    }

    public List<Suggest> suggest(String text) {
        List<Suggest> tempSuggest = new ArrayList<>();
        for (Suggest sug : suggests) {
            if (text.toLowerCase().contains(sug.getKeyWord().toLowerCase())) {
                tempSuggest.add(sug);
            }
        }
        return tempSuggest;
    }
}

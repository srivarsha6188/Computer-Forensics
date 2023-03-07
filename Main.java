package org.example;

import java.util.*;

import static org.example.ForensicPathologist.*;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Please enter the command to use:");
        Scanner scanner = new Scanner(System.in);
        List<String> output = new ArrayList<>();
        List<String> input = Arrays.stream(scanner.nextLine().split(" ")).toList();
        scanner.close();

        if (input.size() < 3) {
            System.out.println("Please enter search and contents to search.. enron_search <search action>  <contents to search>");
            return;
        }
        if (!input.get(0).equals("enron_search")) {
            System.out.println("Please enter search and contents to search.. enron_search <search action>  <contents to search>");
            return;
        }
        ForensicPathologist.init();
        switch (input.get(1)) {
            case "term_search" -> output = termSearch(input.subList(2, input.size()));
            case "address_search" -> output = addressSearch(input.get(2), input.get(3));
            case "interaction_search" -> output = interactionSearch(input.get(2), input.get(3));
            default ->
                    System.out.println("Please enter search and contents to search. enron_search <search action>  <contents to search>");
        }
        for (int x = 0; x < output.size(); x++)
            System.out.println((x + 1) + ". " + output.get(x));

        System.out.println("\nResults found: " + output.size());

    }


}
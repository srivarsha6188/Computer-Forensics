package org.example;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.*;

public class QueryProvider {

    public static SearchTerm getTermSearch(List<String> terms) {
        return new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {

                    StringTokenizer stringTokenizer = new StringTokenizer(
                            message.getContent().toString().toLowerCase(),
                            " .,");
                    List<String> tokens = new ArrayList<>();

                    while (stringTokenizer.hasMoreTokens())
                        tokens.add(stringTokenizer.nextToken());
                    if (new HashSet<>(tokens).containsAll(terms))
                        return true;
                } catch (MessagingException | IOException | NullPointerException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };
    }

    public static SearchTerm getAddressSearch(String firstName, String lastName) {
        return new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {
                    for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements(); ) {
                        Header h = e.nextElement();
                        if (h.getValue().equals(firstName + " " + lastName)
                                || h.getValue().equals(lastName + " " + firstName)
                                || h.getValue().equals(firstName + ", " + lastName)
                                || h.getValue().equals(lastName + ", " + firstName))
                            return true;
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                return false;
            }
        };
    }

    public static SearchTerm getInteractionSearch(String person1, String person2) {
        return new SearchTerm() {
            @Override
            public boolean match(Message message) {
                try {
                    if ((message.getFrom()[0].toString().equalsIgnoreCase(person1)
                            && message.getHeader("To")[0].toString().equalsIgnoreCase(person2))
                            || (message.getFrom()[0].toString().equalsIgnoreCase(person2)
                            && message.getHeader("To")[0].toString().equalsIgnoreCase(person1)))
                        return true;
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                return false;
            }
        };
    }


}
